/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.swing;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServerImpl;
import org.nanocontainer.swing.action.RegisterComponentAction;
import org.nanocontainer.swing.action.AddContainerAction;
import org.nanocontainer.swing.action.UnregisterComponentAction;
import org.nanocontainer.guimodel.ContainerModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class TreePanelDemo {
	public static void main(String[] args) {
		// Creation of the dummy container
		DefaultPicoContainer container1 = new DefaultPicoContainer();
		DefaultPicoContainer container2 = new DefaultPicoContainer(container1);
        container1.registerComponentInstance(container2);
		DefaultPicoContainer container3 = new DefaultPicoContainer(container2);
        container2.registerComponentInstance(container3);

		container1.registerComponentImplementation(DefaultWebServerConfig.class);
		container1.registerComponentImplementation(WebServerImpl.class);
		container2.registerComponentImplementation(WebServerImpl.class);
		container3.registerComponentImplementation(WebServerImpl.class);

        JToolBar toolBar = new JToolBar();
        ContainerTree tree = new ContainerTree(new ContainerModel(container1), IconHelper.getIcon(IconHelper.DEFAULT_COMPONENT_ICON, false));
        toolBar.add(new RegisterComponentAction("blah", tree, TreePanelDemo.class.getClassLoader()));
        final AddContainerAction addContainerAction = new AddContainerAction("blah", tree);
        toolBar.add(addContainerAction);
        toolBar.add(new UnregisterComponentAction("blah", tree));

        ContainerTreePanel panel = new ContainerTreePanel(tree, toolBar);

		JFrame frame = new JFrame();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.show();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});
	}
}
