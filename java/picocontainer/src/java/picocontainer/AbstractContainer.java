/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer;

/**
 * Abstract baseclass for various PicoContainer implementations.
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public abstract class AbstractContainer implements PicoContainer {

    /**
     * This returns a list of all components for the component
     * types the container is hosting.  It may be that some
     * components from the parent container are listed here.
     * The order is not guaranteed at all.
     *
     * @return A list of components being hosted by the
     * container.
     */

    public Object[] getComponents() {
        Class[] componentTypes = getComponentTypes();
        Object[] components = new Object[componentTypes.length];
        for (int i = 0; i < componentTypes.length; i++) {
            Class componentType = componentTypes[i];
            components[i] = getComponent(componentType);
        }
        return components;
    }
}
