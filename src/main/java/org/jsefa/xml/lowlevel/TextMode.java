package org.jsefa.xml.lowlevel;

/**
 * Enum for the different modes of escaping.
 * 
 * @author Matthias Derer
 * @author Norman Lahme-Huetig
 * 
 */
public enum TextMode {
    /**
     * Denotes that text content should be declared implicitly (no usage of CDATA).
     */
    IMPLICIT,

    /**
     * Denotes that text content should be declared using CDATA sections.
     */
    CDATA
}
