/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer.internals;

import org.picocontainer.defaults.InstanceComponentAdapter;

import java.util.List;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ConstantParameter implements Parameter {
    private final Object value;

    public ConstantParameter(Object value) {
        this.value = value;
    }

    public ComponentAdapter resolveAdapter(ComponentRegistry componentRegistries) {
        return new InstanceComponentAdapter(value, value);
    }
}
