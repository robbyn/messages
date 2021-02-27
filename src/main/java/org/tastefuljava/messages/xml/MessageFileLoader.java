package org.tastefuljava.messages.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.tastefuljava.messages.expr.CompilationContext;
import org.tastefuljava.messages.expr.Expression;
import org.tastefuljava.messages.expr.ExpressionCompiler;
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

    private final StringBuilder buf = new StringBuilder();
    private boolean wasBlank;
    private boolean skipBlank;
    private final Messages messages = new Messages();
    private Message message;
    private TextBuilder text;
    private SelectBuilder select;
    private String textLanguage;
    private String definitionName;

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

    private final ExpressionCompiler comp = new ExpressionCompiler();
    private CompilationContext context = new CompilationContext(
            new StandardContext());
    {
        context.defineConst("messages", messages);
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
                messages.setPrefix(attr(attrs, "prefix", ""));
                messages.setDefaultLanguage(
                        attr(attrs, "default-language", "en"));
                break;

            case "message":
                message = new Message(
                        messages.getPrefix() + attr(attrs, "name", ""),
                        list(attrs, "parameters"));
                context = new CompilationContext(context);
                for (String parm: message.getParameters()) {
                    context.addVariable(0, parm);
                }
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
 
            case "description":
                buf.setLength(0);
                wasBlank = false;
                skipBlank = true;
                break;

            case "text": {
                textLanguage = attr(
                        attrs, "language", messages.getDefaultLanguage());
                startText();
                break;
            }

            case "out": {
                startInTextElement();
                String value = attr(attrs, "value", null);
                Expression expr = compileExpr(value);
                text.add(expr);
                break;
            }

            case "select":
                startInTextElement();
                select = new SelectBuilder(select);
                break;

            case "when": {
                String cond = attr(attrs, "condition", null);
                select.startWhen(compileExpr(cond));
                startText();
                break;
            }

            case "otherwise":
                startText();
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        switch (qName) {
            case "messages":
                break;

            case "message":
                context = context.getLink();
                messages.setMessage(message.getName(), message);
                message = null;
                break;

            case "definition":
                break;

            case "description":
                if (message != null) {
                    message.setDescription(buf.toString());
                } else {
                    messages.setDescription(buf.toString());
                }
                break;

            case "text": {
                Expression expr = endText();
                message.setText(textLanguage, expr);
                break;
            }

            case "out":
                endInTextElement();
                break;

            case "select":
                endInTextElement();
                text.add(select.toExpression());
                select = select.getLink();
                break;

            case "when": {
                Expression expr = endText();
                select.endWhen(expr);
                break;
            }

            case "otherwise": {
                Expression expr = endText();
                select.othewise(expr);
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

    private void startText() {
        text = new TextBuilder(text);
        buf.setLength(0);
        wasBlank = false;
        skipBlank = true;
    }

    private Expression endText() {
        if (buf.length() > 0) {
            text.addText(buf.toString());
            buf.setLength(0);
            wasBlank = false;
        }
        Expression expr = text.toExpression();
        text = text.getLink();
        return expr;
    }

    private void startInTextElement() {
        if (wasBlank && !skipBlank) {
            buf.append(' ');
        }
        if (buf.length() > 0) {
            text.addText(buf.toString());
            buf.setLength(0);
        }
        wasBlank = false;
        skipBlank = false;
    }

    private void endInTextElement() {
        buf.setLength(0);
        wasBlank = false;
        skipBlank = false;
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        int end = start + length;
        for (int i = start; i < end; ++i) {
            char c = ch[i];
            if (Character.isWhitespace(c)) {
                wasBlank = true;
            } else {
                if (wasBlank) {
                    if (!skipBlank) {
                        buf.append(' ');
                    }
                    wasBlank = false;
                }
                buf.append(c);
                skipBlank = false;
            }
        }
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
