/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.hibernate;

import org.picocontainer.PicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentAdapter;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;

import org.picocontainer.defaults.CyclicDependencyException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.AbstractComponentAdapter;

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;

import java.util.HashSet;


public class SessionComponentAdapter extends AbstractComponentAdapter {
	
	Parameter sessionFactoryParameter = null;
	ComponentAdapter sessionFactoryAdapter = null;
	boolean verifying = false;

	/**
	 * construct adapter with net.sf.hibernate.Session.class as key 
	 * and dependecy to net.sf.hibernate.SessionFactory.class
	 */
	public SessionComponentAdapter() {
		this(Session.class,null);
	}
	
	/**
	 * construct component adapter with specified key and dependecy to 
	 * net.sf.hibernate.SessionFactory.class
	 */
	public SessionComponentAdapter(Object componentKey) {
		this(componentKey,null);
	}
	/**
 	 * register adapter with given key and parameter
	 */
	public SessionComponentAdapter(Object componentKey,Parameter parameter) {
		super(componentKey,Session.class);
		this.sessionFactoryParameter = parameter == null ? new ComponentParameter(null) : parameter;
	}
	
	public Object getComponentInstance() throws   PicoInitializationException, PicoIntrospectionException {
		verify();
		try {
			return ((SessionFactory)sessionFactoryAdapter.getComponentInstance()).openSession();
		} catch(HibernateException he) {
			throw new PicoInitializationException(he);
		}

	}
	
	public void verify() throws PicoVerificationException {
		try {
			if(verifying) {
				throw new CyclicDependencyException(new Class[] { SessionFactory.class } );
			}
			verifying = true;
			HashSet unsatisfiableDependencies = new HashSet();
			unsatisfiableDependencies.add(SessionFactory.class);
			
			sessionFactoryAdapter = sessionFactoryParameter.resolveAdapter(getContainer(),SessionFactory.class);
			
			if(sessionFactoryAdapter == null) {
				throw new  UnsatisfiableDependenciesException(this,unsatisfiableDependencies);
			}


		} finally {
			verifying = false;
		}
	}
}
