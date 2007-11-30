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
 * Retrieves dependency from container. ComponentAdapters will use 
 * instances of parameter to obtain component dependencies. 
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author Thomas Heller
 * @author Konstantin Pribluda
 * @see MutablePicoContainer#addComponent(Object,Object,Parameter[]) a method on the
 *      {@link MutablePicoContainer} interface which allows passing in of an array of {@linkplain Parameter Parameters}.
 * @see org.picocontainer.parameters.ComponentParameter an implementation of this interface that allows you to specify the key
 *      used for resolving the parameter.
 * @see org.picocontainer.parameters.ConstantParameter an implementation of this interface that allows you to specify a constant
 *      that will be used for resolving the parameter.
 *      
 * @TODO figure good way to exclude adapter without polluting the interface
 */
public interface Parameter<T> {

      Parameter[] ZERO = new Parameter[0];
      Parameter[] DEFAULT =null;

    /**
     * Retrieve the object from the Parameter that statisfies the expected type.
     * 
     * @param container             the container from which dependencies are resolved.
     * @return the instance or <code>null</code> if no suitable instance can be found.
     *
     * @throws PicoCompositionException if a referenced component could not be instantiated.
     */
     T  resolveInstance(PicoContainer container);

    /**
     * Check if the Parameter can be resolved using the container.
     *
     * @param container             the container from which dependencies are resolved.
     *
     * @param useNames
     * @return <code>true</code> if the component parameter can be resolved.
     *
     */
    boolean isResolvable(PicoContainer container);

    /**
     * check if parameter satisfies 
     * desired type (necessary for adapters where order is unknown - 
     * like setters) 
     * @param container
     * @param expectedType
     * @return
     */
    boolean canSatisfy(PicoContainer container,Class expectedType);

    /**
     * Verify that the Parameter can statisfy the expected type using the container
     *
     * @param container             the container from which dependencies are resolved.
     * @throws PicoCompositionException if parameter and its dependencies cannot be resolved
     */
    void verify(PicoContainer container);

    /**
     * Accepts a visitor for this Parameter. The method is normally called by visiting a {@link ComponentAdapter}, that
     * cascades the {@linkplain PicoVisitor visitor} also down to all its {@linkplain Parameter Parameters}.
     *
     * @param visitor the visitor.
     *
     */
    void accept(PicoVisitor visitor);
}