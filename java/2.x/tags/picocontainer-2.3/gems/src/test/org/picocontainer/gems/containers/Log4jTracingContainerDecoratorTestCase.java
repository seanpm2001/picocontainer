/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.containers;

import org.jmock.integration.junit4.JMock;
import org.junit.runner.RunWith;
import org.picocontainer.MutablePicoContainer;

/**
 * @author Michael Rimov 
 * @author Mauro Talevi
 */
@RunWith(JMock.class)
public class Log4jTracingContainerDecoratorTestCase extends AbstractTracingContainerDecoratorTest {

	@Override
	protected MutablePicoContainer createTracingContainerDecorator(
			MutablePicoContainer picoContainer, String name) {
		return new Log4jTracingContainerDecorator(picoContainer, log4jLogger);
	}
	

}
