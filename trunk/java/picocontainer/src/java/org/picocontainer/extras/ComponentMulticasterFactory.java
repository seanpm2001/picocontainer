/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Chris Stevenson*
 *****************************************************************************/

package org.picocontainer.extras;

import org.picocontainer.defaults.Invoker;

import java.util.List;

/**
 * @author Chris Stevenson
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface ComponentMulticasterFactory {
    Object createComponentMulticaster(
            ClassLoader classLoader,
            List objectsToAggregateCallFor,
            boolean callInReverseOrder,
            Invoker invoker
            );
}
