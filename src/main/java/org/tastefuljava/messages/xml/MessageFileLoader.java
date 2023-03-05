package org.tastefuljava.messages.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.tastefuljava.messages.expr.CompilationContext;
import org.tastefuljava.messages.expr.Expression;
import org.tastefuljava.messages.expr.Compiler;
import org.tastefuljava.messages.expr.StandardContext;
import org.tastefuljava.messages.type.GenericContext;
import org.tastefuljava.messages.type.Type;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class MessageFileLoader extends DefaultHandler {
    private static final String DTD_SYSTEM_ID = "messages.dtd";
    private static final String DTD_PUBLIC_ID
            = "-//tastefuljava.org//Message File 1.0//EN";
    private static final Pattern RE_FILENAME
            = Pattern.compile("^(?:.*[\\\\/:])?([^\\\\/:]*)");

    private final Messages messages;
    private TextBuilder text;
    private TextHandler textHandler;
    private String prefix;
    private MessageBuilder message;
    private SequenceBuilder sequence;
    private ListBuilder list;
    private ChooseBuilder choose;
    private GenericContext gc;

    private final Compiler comp = new Compiler();
    private CompilationContext context = new CompilationContext(
            new StandardContext());

    private MessageFileLoader(Messages messages) {
        this.messages = messages;
        context.defineConst("messages", messages);
    }

    public static Messages loadMessages(InputStream stream)
            throws IOException {
        return loadMessages(stream, new Messages());
    }

    public static Messages loadMessages(InputStream stream, Messages messages)
            throws IOException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            SAXParser parser = factory.newSAXParser();
            MessageFileLoader handler = new MessageFileLoader(messages);
            parser.parse(new InputSource(stream), handler);
            return handler.messages;
        } catch (SAXException | ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws IOException, SAXException {
        String fileName = fileName(systemId);
        if (DTD_PUBLIC_ID.equals(publicId)
                || DTD_SYSTEM_ID.equals(fileName)) {
            InputSource source = new InputSource(
                    getClass().getResourceAsStream(DTD_SYSTEM_ID));
            source.setPublicId(DTD_PUBLIC_ID);
            source.setSystemId(DTD_SYSTEM_ID);
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
                gc = new GenericContext(gc);
                break;

            case "definition": {
                String name = attr(attrs, "name", "");
                String value = attr(attrs, "value", null);
                if (value != null) {
                    Expression expr = compileExpr(value);
                    context.define(name, expr);
                }
                break;
            }

            case "message": {
                gc = new GenericContext(gc);
                context = new CompilationContext(context);
                message = new MessageBuilder(
                        prefix + attr(attrs, "name", ""));
                compileGenerics(attr(attrs, "generics", ""));
                List<Type> params = compileParams(attr(attrs, "parameters", ""));
                message.setParamCount(params.size());
                startSequence();
                break;
            }

            case "description":
                startText(true);
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
                gc = gc.getLink();
                break;

            case "description": {
                String s = endText(true);
                if (!s.isEmpty()) {
                    messages.setDescription(s);
                }
                break;
            }

            case "definition":
                break;

            case "message": {
                gc = gc.getLink();
                context = context.getLink();
                Expression expr = endSequence();
                message.setText(expr);
                Message msg = message.build();
                messages.setMessage(msg.getName(), msg);
                message = null;
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

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (textHandler != null) {
            textHandler.addChars(ch, start, length);
        }
    }

    private Expression compileExpr(String value) throws SAXException {
        try {
            return comp.compile(context, value);
        } catch (IOException ex) {
            throw new SAXException(ex.getMessage());
        }
    }

    private List<Type> compileParams(String value) throws SAXException {
        try {
            return comp.parseParams(context, gc, value);
        } catch (IOException ex) {
            throw new SAXException(ex.getMessage());
        }
    }

    private int compileGenerics(String value) throws SAXException {
        try {
            return comp.parseGenerics(context, gc, value);
        } catch (IOException ex) {
            throw new SAXException(ex.getMessage());
        }
    }

    private void startSequence() {
        sequence = new SequenceBuilder(sequence);
        startText(true);
    }

    private Expression endSequence() {
        String s = endText(true);
        if (!s.isEmpty()) {
            sequence.addText(s);
        }
        Expression expr = sequence.toExpression();
        sequence = sequence.getLink();
        return expr;
    }

    private void startInSequenceElement() {
        String s = endText(false);
        if (!s.isEmpty()) {
            sequence.addText(s);
        }
    }

    private void endInSequenceElement() {
        startText(false);
    }

    private void startText(boolean trimHead) {
        textHandler = text = new TextBuilder(trimHead);
    }

    private String endText(boolean trimTail) {
        String s = text.end(trimTail);
        textHandler = text = null;
        return s;
    }

    private static String attr(Attributes attrs, String name, String def) {
        String s = attrs.getValue(name);
        return s != null ? s : def;
    }

    private static String fileName(String uri) {
        Matcher matcher = RE_FILENAME.matcher(uri);
        if (!matcher.matches()) {
            return uri;
        }
        return matcher.group(1);
    }
}
