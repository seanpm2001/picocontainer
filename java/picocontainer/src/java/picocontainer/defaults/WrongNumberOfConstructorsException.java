/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.defaults;

import picocontainer.PicoIntrospectionException;

public class WrongNumberOfConstructorsException extends PicoIntrospectionException {

    int numOfCtors;

    public WrongNumberOfConstructorsException(int numOfCtors) {
        this.numOfCtors = numOfCtors;
    }

    public String getMessage() {
        return "Wrong Number of Constructors for Pico Component. Expected 1, found " + numOfCtors;
    }

}
