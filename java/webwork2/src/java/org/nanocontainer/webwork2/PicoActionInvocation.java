/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.webwork2;

import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.lifecycle.LifecyclePicoAdapter;
import org.picoextras.servlet.KeyConstants;
import org.picoextras.servlet.RequestScopeObjectReference;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Chris Sturm
 */
public class PicoActionInvocation extends DefaultActionInvocation implements KeyConstants {

    public PicoActionInvocation(ActionProxy proxy) throws Exception {
        super(proxy);
    }

    public PicoActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
        super(proxy, extraContext);
    }

    public PicoActionInvocation(ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(proxy, extraContext, pushAction);
    }

    protected void createAction() {
        // load action
        try {
            MutablePicoContainer container = new DefaultPicoContainer();
            container.addParent(getParentContainer());

            Class actionClass = proxy.getConfig().getClazz();
            container.registerComponentImplementation(actionClass);
            action = (Action) container.getComponentInstance(actionClass);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unknown action name: " + e.getMessage());
        }
    }

    private MutablePicoContainer getParentContainer() {
        HttpServletRequest request = (HttpServletRequest) getStack().getContext().get(WebWorkStatics.HTTP_REQUEST);
        ObjectReference ref = new RequestScopeObjectReference(request, REQUEST_CONTAINER);
        LifecyclePicoAdapter lifecycle = (LifecyclePicoAdapter) ref.get();
        return (MutablePicoContainer) lifecycle.getPicoContainer();
    }
}
