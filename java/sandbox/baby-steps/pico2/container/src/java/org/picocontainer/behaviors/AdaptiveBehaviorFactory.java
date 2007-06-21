package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.LifecycleStrategy;
import org.picocontainer.ComponentCharacteristic;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.ComponentCharacteristics;
import org.picocontainer.ComponentFactory;
import org.picocontainer.BehaviorFactory;
import org.picocontainer.annotations.Cache;
import org.picocontainer.injectors.AdaptiveInjectionFactory;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class AdaptiveBehaviorFactory implements ComponentFactory, Serializable {

    public ComponentAdapter createComponentAdapter(ComponentMonitor componentMonitor,
                                                   LifecycleStrategy lifecycleStrategy,
                                                   ComponentCharacteristic componentCharacteristic,
                                                   Object componentKey,
                                                   Class componentImplementation,
                                                   Parameter... parameters) throws PicoCompositionException {
        List<ComponentFactory> list = new ArrayList<ComponentFactory>();
        ComponentFactory lastFactory = makeInjectionFactory();
        processThreadSafe(componentCharacteristic, list);
        processImplementationHiding(componentCharacteristic, list);
        processCachedInstance(componentCharacteristic, componentImplementation, list);

        //Instantiate Chain of ComponentFactories
        for (ComponentFactory componentFactory : list) {
            if (lastFactory != null && componentFactory instanceof BehaviorFactory) {
                ((BehaviorFactory)componentFactory).forThis(lastFactory);
            }
            lastFactory = componentFactory;
        }

        return lastFactory.createComponentAdapter(componentMonitor,
                                                  lifecycleStrategy,
                                                  componentCharacteristic,
                                                  componentKey,
                                                  componentImplementation,
                                                  parameters);
    }

    protected AdaptiveInjectionFactory makeInjectionFactory() {
        return new AdaptiveInjectionFactory();
    }

    protected void processThreadSafe(ComponentCharacteristic componentCharacteristic, List<ComponentFactory> list) {
        if (ComponentCharacteristics.THREAD_SAFE.isCharacterizedIn(componentCharacteristic)) {
            list.add(new SynchronizedBehaviorFactory());
        }
    }

    protected void processCachedInstance(ComponentCharacteristic componentCharacteristic,
                                       Class componentImplementation,
                                       List<ComponentFactory> list) {
        if (ComponentCharacteristics.CACHE.isCharacterizedIn(componentCharacteristic) ||
            componentImplementation.getAnnotation(Cache.class) != null) {
            list.add(new CachingBehaviorFactory());
        }
    }

    protected void processImplementationHiding(ComponentCharacteristic componentCharacteristic,
                                             List<ComponentFactory> list) {
        if (ComponentCharacteristics.HIDE.isCharacterizedIn(componentCharacteristic)) {
            list.add(new ImplementationHidingBehaviorFactory());
        }
    }

}
