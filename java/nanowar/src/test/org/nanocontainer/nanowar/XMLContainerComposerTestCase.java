/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 */
public class XMLContainerComposerTestCase extends MockObjectTestCase {

    public void testDefaultConfiguration() throws Exception {
        XMLContainerComposer composer = new XMLContainerComposer();

        MutablePicoContainer application = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(application, servletContextMock.proxy());
        assertNotNull(application.getComponentInstance("applicationScopedInstance"));

        MutablePicoContainer session = new DefaultPicoContainer();
        Mock httpSessionMock = mock(HttpSession.class);
        composer.composeContainer(session, httpSessionMock.proxy());
        assertNotNull(session.getComponentInstance("sessionScopedInstance"));

        MutablePicoContainer request = new DefaultPicoContainer();
        Mock httpRequestMock = mock(HttpServletRequest.class);
        composer.composeContainer(request, httpRequestMock.proxy());
        assertNotNull(request.getComponentInstance("requestScopedInstance"));
    }

    public void testCustomConfiguration() throws Exception {
        Map config = new HashMap();
        config.put(XMLContainerComposer.APPLICATION_CONFIG_KEY,
                new String[]{"nanowar-application.xml","nanowar/application.xml"});
        config.put(XMLContainerComposer.SESSION_CONFIG_KEY,
                new String[]{"nanowar-session.xml","nanowar/session.xml"});
        config.put(XMLContainerComposer.REQUEST_CONFIG_KEY,
                new String[]{"nanowar-request.xml","nanowar/request.xml"});
        XMLContainerComposer composer = new XMLContainerComposer(config);

        MutablePicoContainer application = new DefaultPicoContainer();
        Mock servletContextMock = mock(ServletContext.class);

        composer.composeContainer(application, servletContextMock.proxy());
        assertNotNull(application.getComponentInstance("applicationScopedInstance"));
        assertNotNull(application.getComponentInstance("applicationScopedInstance2"));

        MutablePicoContainer session = new DefaultPicoContainer();
        Mock httpSessionMock = mock(HttpSession.class);
        composer.composeContainer(session, httpSessionMock.proxy());
        assertNotNull(session.getComponentInstance("sessionScopedInstance"));
        assertNotNull(session.getComponentInstance("sessionScopedInstance2"));

        MutablePicoContainer request = new DefaultPicoContainer();
        Mock httpRequestMock = mock(HttpServletRequest.class);
        composer.composeContainer(request, httpRequestMock.proxy());
        assertNotNull(request.getComponentInstance("requestScopedInstance"));
        assertNotNull(request.getComponentInstance("requestScopedInstance2"));
    }

}
