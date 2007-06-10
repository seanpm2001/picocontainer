/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.injectors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.Parameter;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.InjectionFactory;
import org.picocontainer.behaviors.CachingBehavior;
import org.picocontainer.injectors.SetterInjectionFactory;

import java.io.Serializable;

/**
 * Creates instances of {@link ConstructorInjector} decorated by
 * {@link CachingBehavior}.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AnyInjectionFactory implements InjectionFactory, Serializable {

    private final ConstructorInjectionFactory cdiDelegate = new ConstructorInjectionFactory();
    private final SetterInjectionFactory sdiDelegate = new SetterInjectionFactory();

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor, LifecycleStrategy lifecycleStrategy, ComponentCharacteristic componentCharacteristic, Object componentKey, Class componentImplementation, Parameter... parameters) throws PicoIntrospectionException {
        if (ComponentCharacteristics.SDI.isSoCharacterized(componentCharacteristic)) {
            return sdiDelegate.createComponentAdapter(componentMonitor, lifecycleStrategy, componentCharacteristic, componentKey, componentImplementation, parameters);
        }
        return cdiDelegate.createComponentAdapter(componentMonitor, lifecycleStrategy, componentCharacteristic, componentKey, componentImplementation, parameters);
    }

}
