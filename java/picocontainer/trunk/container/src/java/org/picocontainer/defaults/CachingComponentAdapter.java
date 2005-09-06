/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.LifecycleManager;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

/**
 * <p>
 * This ComponentAdapter caches the instance
 * </p>
 * <p>
 * This adapter is also a {@link LifecycleManager lifecycle manager} which will apply
 * the delegate's {@link LifecycleStrategy lifecycle strategy} to the cached component instance.
 * The lifecycle state is maintained so that the component instance behaves in the expected way,
 * eg it can't be started if already started, it can't be started or stopped if disposed, it can't
 * be stopped if not started, it can't be disposed if already disposed.
 * </p>
 * 
 * @author Mauro Talevi
 * @version $Revision$
 */
public class CachingComponentAdapter extends DecoratingComponentAdapter implements LifecycleManager {

    private ObjectReference instanceReference;
    private boolean disposed = false;
    private boolean started = false;
    
    public CachingComponentAdapter(ComponentAdapter delegate) {
        this(delegate, new SimpleReference());
    }

    public CachingComponentAdapter(ComponentAdapter delegate, ObjectReference instanceReference) {
        super(delegate);
        this.instanceReference = instanceReference;
    }
    
    public Object getComponentInstance(PicoContainer container)
            throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        if (instanceReference.get() == null) {
            instanceReference.set(super.getComponentInstance(container));
            //TODO decide if component should always be started upon initialisation
            //start(container);
        }
        return instanceReference.get();
    }

    /**
     * Flushes the cache. 
     * If the component instance is started is will stop and dispose it before 
     * flushing the cache.
     */
    public void flush() {
        Object instance = instanceReference.get();
        if ( instance != null && delegateSupportsLifecycle() && started ) {
            stop(instance);
            dispose(instance);
        }
        instanceReference.set(null);
    }

    private boolean delegateSupportsLifecycle() {
        return getDelegate() instanceof LifecycleStrategy;
    }

    /**
     * Starts the cached component instance
     * {@inheritDoc}
     */
    public void start(PicoContainer container) {
        if ( delegateSupportsLifecycle() ){
            if (disposed) throw new IllegalStateException("Already disposed");
            if (started) throw new IllegalStateException("Already started");
            start(getComponentInstance(container));
            started = true;        
        }
    }

    /**
     * Stops the cached component instance
     * {@inheritDoc}
     */
    public void stop(PicoContainer container) {
        if ( delegateSupportsLifecycle() ){
            if (disposed)
                throw new IllegalStateException("Already disposed");
            if (!started)
                throw new IllegalStateException("Not started");
            stop(getComponentInstance(container));
            started = false;
        }
    }

    /**
     * Disposes the cached component instance
     * {@inheritDoc}
     */
    public void dispose(PicoContainer container) {
        if ( delegateSupportsLifecycle() ){
            if (disposed) throw new IllegalStateException("Already disposed");
            dispose(getComponentInstance(container));
            disposed = true;
        }
    }
    
}
