/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.picocontainer.script;

import org.picocontainer.PicoException;

/**
 * Exception thrown due to invalid markup when assembling {@link org.picocontainer.script.ScriptedPicoContainer}s.
 *
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @author Aslak Helles&oslash;y
 */
public class ScriptedPicoContainerMarkupException extends PicoException {

    public ScriptedPicoContainerMarkupException(String message) {
        super(message);
    }

    public ScriptedPicoContainerMarkupException(String message, Throwable e) {
        super(message, e);
    }

    public ScriptedPicoContainerMarkupException(Throwable e) {
        super(e);
    }
}
