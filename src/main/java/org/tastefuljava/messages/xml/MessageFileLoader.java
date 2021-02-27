package org.tastefuljava.messages.xml;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class MessageFileLoader extends DefaultHandler {
    private static final String DTD_SYSTEM_ID = "messages.dtd";
    private static final String DTD_PUBLIC_ID
            = "-//tastefuljava.org//Message File 1.0//EN";

    private final StringBuilder buf = new StringBuilder();

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
}
