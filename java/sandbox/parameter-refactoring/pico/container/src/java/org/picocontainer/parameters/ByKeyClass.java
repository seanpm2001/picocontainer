/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import java.util.Collection;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

/**
 * lookup component adapters registered with certain key class
 * @author Konstantin Pribluda
 *
 */
public class ByKeyClass extends AbstractLookup {

	Class keyClass;
	
	
	public ByKeyClass(Class keyClass) {
		this.keyClass = keyClass;
	}

	@Override
	void extract(PicoContainer container, Collection<ComponentAdapter> store) {
		for(ComponentAdapter candidate: container.getComponentAdapters()) {
			if(keyClass.isAssignableFrom(candidate.getComponentKey().getClass())) {
				store.add(candidate);
			}
		}
	}

}