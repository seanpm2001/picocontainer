/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.rhino;

import org.mozilla.javascript.*;
import org.nanocontainer.reflection.DefaultReflectionFrontEnd;
import org.nanocontainer.reflection.ReflectionFrontEnd;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

public class DefaultNanoRhinoScriptable extends ScriptableObject implements NanoRhinoScriptable {

    private ReflectionFrontEnd reflectionFrontEnd;

    public MutablePicoContainer getPicoContainer() {
        return reflectionFrontEnd.getPicoContainer();
    }

    public String getClassName() {
        return "NanoRhinoScriptable";
    }

    public static Object jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) throws ClassNotFoundException {
        DefaultReflectionFrontEnd defaultReflectionFrontEnd;
        if (args.length == 1) {
            ComponentAdapterFactory caf = null;
            {
                DefaultReflectionFrontEnd tmpFrontEnd = new DefaultReflectionFrontEnd();
                tmpFrontEnd.registerComponentWithClassKey(ComponentAdapterFactory.class.getName(), (String) args[0]);
                caf = (ComponentAdapterFactory) tmpFrontEnd.getPicoContainer().getComponentInstance(ComponentAdapterFactory.class);
            }
            defaultReflectionFrontEnd = new DefaultReflectionFrontEnd(new DefaultPicoContainer(caf));
        } else {
            defaultReflectionFrontEnd = new DefaultReflectionFrontEnd();
        }
        DefaultNanoRhinoScriptable rhino = new DefaultNanoRhinoScriptable();
        rhino.reflectionFrontEnd = defaultReflectionFrontEnd;
        return rhino;
    }

    public static void jsFunction_addComponent(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ClassNotFoundException {
        DefaultNanoRhinoScriptable rhino = (DefaultNanoRhinoScriptable) thisObj;
        if (args.length == 1) {
            rhino.reflectionFrontEnd.registerComponentImplementation((String) args[0]);
        } else if (args.length == 2) {
            rhino.reflectionFrontEnd.registerComponent((String) args[0], (String) args[1]);
        }
    }

    public static void jsFunction_addComponentWithClassKey(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ClassNotFoundException {
        DefaultNanoRhinoScriptable rhino = (DefaultNanoRhinoScriptable) thisObj;
        rhino.reflectionFrontEnd.registerComponentWithClassKey((String) args[0], (String) args[1]);
    }

    public static void jsFunction_addComponentInstance(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ClassNotFoundException {
        DefaultNanoRhinoScriptable rhino = (DefaultNanoRhinoScriptable) thisObj;
        MutablePicoContainer picoContainer = rhino.reflectionFrontEnd.getPicoContainer();
        picoContainer.registerComponentInstance(((NativeJavaObject) args[0]).unwrap());
    }

    public static void jsFunction_addContainer(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        DefaultNanoRhinoScriptable parent = (DefaultNanoRhinoScriptable) thisObj;
        DefaultNanoRhinoScriptable child = (DefaultNanoRhinoScriptable) args[0];
        parent.reflectionFrontEnd.getPicoContainer().addChild(child.reflectionFrontEnd.getPicoContainer());

    }
}
