/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.sample.webwork;

import org.nanocontainer.nanowar.sample.model.Cheese;
import org.nanocontainer.nanowar.sample.service.CheeseService;

import webwork.action.ActionSupport;
import webwork.action.CommandDriven;

/**
 * Example of a WebWork action that relies on constructor injection.
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CheeseAction extends ActionSupport implements CommandDriven {

    private final CheeseService cheeseService;
    private Cheese cheese = new Cheese();

    public CheeseAction(CheeseService cheeseService) {
        this.cheeseService = cheeseService;
    }
    
    public Cheese getCheese() {
        return cheese;
    }
    
    public String doSave() {
        try {
            cheeseService.save(cheese);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Couldn't save cheese: " + cheese);
            return ERROR;
        }
    }

    public String doFind() {
        try {
            cheeseService.find(cheese);
            return SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            addErrorMessage("Couldn't find cheese: "+ cheese);
            return ERROR;
        }
    }
    
    
    public String doRemove() throws Exception {
        try {
        cheeseService.remove(cheeseService.find(cheese));
        return SUCCESS;
        } catch(Exception e) {
            e.printStackTrace();
            addErrorMessage("Could'nt remove cheese " + cheese);
            return ERROR;
        }
    }
}
