/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.servlet;

import org.picocontainer.defaults.ObjectReference;
import org.nanocontainer.integrationkit.ContainerBuilder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class ServletRequestContainerLauncher {

    private ContainerBuilder containerBuilder;
    private ObjectReference containerRef;
    private HttpServletRequest request;

    public ServletRequestContainerLauncher(ServletContext context, HttpServletRequest request) {
        ObjectReference builderRef = new ApplicationScopeObjectReference(context, KeyConstants.BUILDER);
        containerRef = new RequestScopeObjectReference(request, KeyConstants.REQUEST_CONTAINER);
        containerBuilder = (ContainerBuilder) builderRef.get();
        this.request = request;
    }

    public void startContainer() throws ServletException {
        HttpSession session = request.getSession(true);
        ObjectReference parentContainerRef = new SessionScopeObjectReference(session, KeyConstants.SESSION_CONTAINER);
        if (containerBuilder == null) {
            throw new ServletException("org.nanocontainer.servlet.ServletContainerListener not deployed");
        }
        containerBuilder.buildContainer(containerRef, parentContainerRef, request);
    }

    public void killContainer() {
        if (containerRef.get() != null) {
            containerBuilder.killContainer(containerRef);
        }
    }
}
