/*******************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 ******************************************************************************/
package org.picocontainer.web.sample.webwork2;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Characteristics;
import org.picocontainer.injectors.ProviderAdapter;
import org.picocontainer.web.WebappComposer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

public class WebWork2DemoComposer implements WebappComposer {

    public void composeApplication(MutablePicoContainer pico, ServletContext context) {
        pico.addComponent(CheeseDao.class, InMemoryCheeseDao.class);
    }

    public void composeSession(MutablePicoContainer pico) {
        pico.addComponent(CheeseService.class, DefaultCheeseService.class);
    }

    public void composeRequest(MutablePicoContainer pico) {
        pico.as(Characteristics.NO_CACHE).addComponent(Brand.class, Brand.FromRequest.class);

    }

}