/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.adapters.MonitoringComponentAdapterFactory;


/**
 * A {@link org.picocontainer.defaults.ComponentAdapterFactory} for Guice style annotated methods.
 * The factory creates {@link org.picocontainer.adapters.AnnotationInjectionComponentAdapter}.
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public class AnnotationInjectionComponentAdapterFactory extends MonitoringComponentAdapterFactory {

    /**
     * Create a {@link org.picocontainer.adapters.SetterInjectionComponentAdapter}.
     *
     * @param componentMonitor
     *@param lifecycleStrategy
     * @param registerationCharacteristic
     * @param componentKey            The addComponent's key
     * @param componentImplementation The class of the bean.
     * @param parameters              Any parameters for the setters. If null the addAdapter solves the
*                                dependencies for all setters internally. Otherwise the number parameters must match
*                                the number of the setter. @return Returns a new {@link SetterInjectionComponentAdapter}. @throws org.picocontainer.PicoIntrospectionException if dependencies cannot be solved
     * @throws org.picocontainer.defaults.AssignabilityRegistrationException
     *                                    if  the <code>componentKey</code> is a type
     *                                    that does not match the implementation
     * @throws org.picocontainer.defaults.NotConcreteRegistrationException
     *                                    if the implementation is an interface or an
     *                                    abstract class.
     */
    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic registerationCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters)
            throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new AnnotationInjectionComponentAdapter(componentKey, componentImplementation, parameters, componentMonitor, lifecycleStrategy);
    }
}