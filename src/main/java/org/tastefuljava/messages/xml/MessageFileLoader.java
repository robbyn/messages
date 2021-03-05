package org.tastefuljava.messages.xml;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.tastefuljava.messages.expr.CompilationContext;
import org.tastefuljava.messages.expr.Expression;
import org.tastefuljava.messages.expr.Compiler;
import org.tastefuljava.messages.expr.StandardContext;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class MessageFileLoader extends DefaultHandler {
    private static final String DTD_SYSTEM_ID = "messages.dtd";
    private static final String DTD_PUBLIC_ID
            = "-//tastefuljava.org//Message File 1.0//EN";
    private static final String[] EMPTY_LIST = {};

    private final TextBuilder text = new TextBuilder();
    private final Messages messages = new Messages();
    private String prefix;
    private Message message;
    private Described descriptionHolder;
    private SequenceBuilder sequence;
    private ListBuilder list;
    private ChooseBuilder choose;
    private String textLanguage;
    private String definitionName;

    private final Compiler comp = new Compiler();
    private CompilationContext context = new CompilationContext(
            new StandardContext());
    {
        context.defineConst("messages", messages);
    }

    public static Messages loadMessages(InputStream stream) throws IOException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            MessageFileLoader handler = new MessageFileLoader();
            parser.parse(new InputSource(stream), handler);
            return handler.messages;
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws IOException, SAXException {
        if (DTD_PUBLIC_ID.equals(publicId)
                || DTD_SYSTEM_ID.equals(systemId)) {
            InputSource source = new InputSource(
                    getClass().getResourceAsStream("messages.dtd"));
            source.setPublicId(publicId);
            source.setSystemId(systemId);
            return source;
        }
        return super.resolveEntity(publicId, systemId);
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw new SAXException(e.getMessage());
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw new SAXException(e.getMessage());
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {
        switch (qName) {
            case "messages":
                prefix = attr(attrs, "prefix", "");
                messages.setLanguage(
                        attr(attrs, "language", "en"));
                descriptionHolder = messages;
                break;

            case "definition": {
                String name = attr(attrs, "name", "");
                String value = attr(attrs, "value", null);
                if (value == null) {
                    definitionName = name;
                } else {
                    Expression expr = compileExpr(value);
                    context.define(name, expr);
                }
                break;
            }

            case "message":
                message = new Message(
                        prefix + attr(attrs, "name", ""),
                        list(attrs, "parameters"));
                context = new CompilationContext(context);
                for (String parm: message.getParameters()) {
                    context.addVariable(parm);
                }
                startSequence();
                break;

            case "description":
                text.start(true);
                break;

            case "out": {
                startInSequenceElement();
                String value = attr(attrs, "value", null);
                Expression expr = compileExpr(value);
                sequence.add(expr);
                break;
            }

            case "if": {
                startInSequenceElement();
                choose = new ChooseBuilder(choose);
                String cond = attr(attrs, "condition", null);
                choose.startWhen(compileExpr(cond));
                startSequence();
                break;
            }

            case "choose":
                startInSequenceElement();
                choose = new ChooseBuilder(choose);
                break;

            case "when": {
                String cond = attr(attrs, "condition", null);
                choose.startWhen(compileExpr(cond));
                startSequence();
                break;
            }

            case "otherwise":
                startSequence();
                break;

            case "list": {
                startInSequenceElement();
                String value = attr(attrs, "value", null);
                String var = attr(attrs, "variable", null);
                String sep = attr(attrs, "separator", ", ");
                list = new ListBuilder(compileExpr(value), var, sep, list);
                startSequence();
                context = new CompilationContext(context);
                context.addVariable(var);
                break;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        switch (qName) {
            case "messages":
                descriptionHolder = null;
                break;

            case "description": {
                String s = text.end(true);
                if (!s.isEmpty()) {
                    descriptionHolder.setDescription(s);
                }
                break;
            }

            case "definition":
                break;

            case "message": {
                context = context.getLink();
                Expression expr = endSequence();
                message.setText(expr);
                messages.setMessage(message.getName(), message);
                message = null;
                descriptionHolder = null;
                break;
            }

            case "out":
                endInSequenceElement();
                break;

            case "if": {
                Expression expr = endSequence();
                choose.endWhen(expr);
                sequence.add(choose.toExpression());
                choose = choose.getLink();
                endInSequenceElement();
                break;
            }

            case "choose":
                endInSequenceElement();
                sequence.add(choose.toExpression());
                choose = choose.getLink();
                break;

            case "when": {
                Expression expr = endSequence();
                choose.endWhen(expr);
                break;
            }

            case "otherwise": {
                Expression expr = endSequence();
                choose.othewise(expr);
                break;
            }

            case "list": {
                context = context.getLink();
                Expression expr = endSequence();
                sequence.add(list.toExpression(expr));
                list = list.getLink();
                endInSequenceElement();
                break;
            }
        }
    }

    private Expression compileExpr(String value) throws SAXException {
        try {
            return comp.compile(context, value);
        } catch (IOException ex) {
            throw new SAXException(ex.getMessage());
        }
    }

    private void startSequence() {
        sequence = new SequenceBuilder(sequence);
        text.start(true);
    }

    private Expression endSequence() {
        String s = text.end(true);
        if (!s.isEmpty()) {
            sequence.addText(s);
        }
        Expression expr = sequence.toExpression();
        sequence = sequence.getLink();
        return expr;
    }

    private void startInSequenceElement() {
        String s = text.end(false);
        if (!s.isEmpty()) {
            sequence.addText(s);
        }
    }

    private void endInSequenceElement() {
        text.start(false);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        text.addChars(ch, start, length);
    }

    private static String attr(Attributes attrs, String name, String def) {
        String s = attrs.getValue(name);
        return s != null ? s : def;
    }

    private static String[] list(Attributes attrs, String name) {
        String s = attrs.getValue(name);
        return s != null ? s.split("[,;]") : EMPTY_LIST;
    }
}
