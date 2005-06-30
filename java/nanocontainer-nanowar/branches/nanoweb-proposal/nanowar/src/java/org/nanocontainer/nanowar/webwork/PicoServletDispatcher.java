/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nanocontainer.nanowar.ServletRequestContainerLauncher;

import webwork.action.factory.ActionFactory;
import webwork.dispatcher.ServletDispatcher;

/**
 * Extension to the standard WebWork DispatcherServlet that instantiates a new container in the request scope for each
 * request and disposes of it correctly at the end of the request. <p/> To use, replace the WebWork ServletDispatcher in
 * web.xml with this.
 * 
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class PicoServletDispatcher extends ServletDispatcher {

	public PicoServletDispatcher() {
		super();
		ActionFactory.setActionFactory(new WebWorkActionFactory());
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		ServletRequestContainerLauncher containerLauncher = new ServletRequestContainerLauncher(getServletContext(), request);
		try {
			containerLauncher.startContainer();
			// process the servlet using webwork
			super.service(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			try {
				containerLauncher.killContainer();
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}
	}
}
