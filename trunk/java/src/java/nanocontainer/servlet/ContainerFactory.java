/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package nanocontainer.servlet;



import picocontainer.PicoContainer;



public interface ContainerFactory {



    PicoContainer buildContainer(String configName);



    PicoContainer buildContainerWithParent(PicoContainer parentContainer, String configName);



    ObjectInstantiater buildInstantiater(PicoContainer parentContainer);



    void destroyContainer(PicoContainer container);



}

