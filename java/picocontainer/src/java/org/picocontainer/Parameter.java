/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer;

/**
 * This class provides control over the arguments that will be passed to a constructor. It can be used for finer control
 * over what arguments are passed to a particular constructor.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 * @see MutablePicoContainer#registerComponentImplementation(Object,Class,Parameter[]) registerComponentImplementation
 * @see org.picocontainer.defaults.ComponentParameter
 * @see org.picocontainer.defaults.ConstantParameter
 * @since 1.0
 */
public interface Parameter {
    /**
     * Retrieve the component adapter that should be used to find the instance to be passed in for this parameter.
     *
     * @param picoContainer the container from which dependencies are resolved.
     * @param expectedType  the type that the returned adapter needs to provide.
     * @return the component adapter that should be used to find the instance to be passed in for this parameter. Should
     *         return <code>null</code> if not suitable adapter can be found.
     */
    ComponentAdapter resolveAdapter(PicoContainer picoContainer, Class expectedType);
}
