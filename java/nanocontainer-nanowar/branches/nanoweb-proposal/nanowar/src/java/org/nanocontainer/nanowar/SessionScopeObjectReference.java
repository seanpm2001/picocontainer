/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import org.picocontainer.defaults.ObjectReference;

/**
 * References an object that lives as an attribute of the HttpSession.
 * 
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class SessionScopeObjectReference implements ObjectReference, Serializable {

	// The only reason this class is Serializable and the 'session' field is transient
	// is so that if this class is used as a key in a PicoContainer (as it is in the
	// nanocontainer servlet framework), it won't break serializability of the
	// container. The deserialized class won't be reused for its actual purpose, but
	// discarded. As such, there is no need to resurrect the transient session field
	private transient HttpSession session;

	private String key;

	public SessionScopeObjectReference(HttpSession session, String key) {
		this.session = session;
		this.key = key;
	}

	public void set(Object item) {
		session.setAttribute(key, item);
	}

	public Object get() {
		return session.getAttribute(key);
	}

}
