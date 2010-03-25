package org.jsefa.xml.annotation;

/**
 * Enum for the different types of default names.
 * 
 * @author Norman Lahme-Huetig
 * 
 */
public enum DefaultName {
    /**
     * Denotes that the default name should be the name of the respective field.
     */
    FIELD_NAME,

    /**
     * Denotes that the default name should be the default element name of the respective object type (see XmlDataType).
     */
    TYPE_DEFAULT_NAME
}