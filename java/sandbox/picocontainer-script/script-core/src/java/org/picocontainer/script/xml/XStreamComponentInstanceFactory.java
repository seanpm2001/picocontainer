/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.script.xml;

import org.picocontainer.PicoContainer;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.DomReader;

/**
 * Implementation of XMLComponentInstanceFactory that uses 
 * XStream to unmarshal DOM elements.
 *
 * @author Paul Hammant
 * @author Marcos Tarruella
 * @author Mauro Talevi
 */
public class XStreamComponentInstanceFactory implements XMLComponentInstanceFactory {
	/** The XStream used to unmarshal the DOM element */
	private final XStream xstream;

	/**
	 * Creates an XStreamComponentInstanceFactory with the default instance
	 * of XStream
	 */
	public XStreamComponentInstanceFactory(){
		this(new XStream(new DomDriver()));
	}
	
	/**
	 * Creates an XStreamComponentInstanceFactory for a given instance
	 * of XStream
	 * @param xstream the XStream instance
	 */
	public XStreamComponentInstanceFactory(XStream xstream){
		this.xstream = xstream;
	}
	
    /**
     * {@inheritDoc}
     *
     * @see XMLComponentInstanceFactory#makeInstance(org.picocontainer.PicoContainer,org.w3c.dom.Element,ClassLoader)
     */
    public Object makeInstance(PicoContainer pico, Element element, ClassLoader classLoader) {
        return xstream.unmarshal(new DomReader(element));
    }
}
