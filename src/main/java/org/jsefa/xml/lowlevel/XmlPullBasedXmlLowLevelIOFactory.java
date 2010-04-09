package org.jsefa.xml.lowlevel;

import org.jsefa.xml.lowlevel.config.XmlLowLevelConfiguration;

/**
 * XmlPull-based implementation of {@link XmlLowLevelIOFactory}.
 * <p>
 * Instances of this class are immutable and thread-safe.
 * 
 * @author Sebastian Hoß
 * @author Norman Lahme-Huetig
 * 
 */
public class XmlPullBasedXmlLowLevelIOFactory extends XmlLowLevelIOFactory {

    private final XmlLowLevelConfiguration config;

    public static XmlPullBasedXmlLowLevelIOFactory createFactory(final XmlLowLevelConfiguration config) {
        return new XmlPullBasedXmlLowLevelIOFactory(config);
    }

    XmlPullBasedXmlLowLevelIOFactory(final XmlLowLevelConfiguration config) {
        this.config = config;
    }

    /**
     */
    @Override
    public XmlLowLevelDeserializer createDeserializer() {
        return new XmlPullBasedXmlLowLevelDeserializer(this.config);
    }

    /**
     */
    @Override
    public XmlLowLevelSerializer createSerializer() {
        return new XmlPullBasedXmlLowLevelSerializer(this.config);
    }



}
