/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.tck.AbstractLazyInstantiationTest;

/**
 * @author Aslak Helles&oslash;y
 */
public class DefaultLazyInstantiationTestCase extends AbstractLazyInstantiationTest {
    protected MutablePicoContainer createPicoContainer() {
        return new DefaultPicoContainer();
    }
}
