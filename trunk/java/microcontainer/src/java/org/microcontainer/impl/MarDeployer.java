/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mike Ward                                                *
 *****************************************************************************/

package org.microcontainer.impl;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.io.*;

/**
 * @author Mike Ward
 * Responsible for deploying a MAR to the file system.
 */
public class MarDeployer {
	protected File workingDir = null;
	protected File tempDir = null;

	public MarDeployer() {
		// todo this should be configurable! Pico-tize?
		workingDir = new File("work");
		workingDir.mkdir();
		tempDir = new File("temp");
		tempDir.mkdir();
	}

	public File getWorkingDir() {
		return workingDir;
	}

	public void deploy(String context, URL marURL) throws IOException {
		URLConnection connection = marURL.openConnection();
		File sandboxDir = new File(workingDir, context);
		sandboxDir.mkdir();

		if(connection instanceof JarURLConnection) {
			handleLocalMAR(sandboxDir, (JarURLConnection)connection);
		}
		else if(connection instanceof HttpURLConnection) {
			handleRemoteMAR(sandboxDir, (HttpURLConnection)connection);
		}
		else {
			throw new IOException("Unsupported URLConnection type [" + connection.getClass() + "]");
		}
	}

	protected void handleRemoteMAR(File sandboxDir, HttpURLConnection connection) throws IOException {
		// copy the remote MAR file to the temp dir
		String marFileName = sandboxDir.getName() + ".mar";
		expand(connection.getInputStream(), tempDir, marFileName);
		connection.disconnect();
		URL jarURL = new URL("jar:file:" + tempDir.getCanonicalPath() + "\\" + marFileName + "!/");

		// handle as local
		handleLocalMAR(sandboxDir, (JarURLConnection)jarURL.openConnection());
	}

	protected void handleLocalMAR(File dir, JarURLConnection connection) throws IOException {
		// Expand the MAR into working directory
		connection.setUseCaches(false);
		JarFile jarFile = null;
		InputStream input = null;

		try {
			jarFile = connection.getJarFile();
			Enumeration jarEntries = jarFile.entries();

			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
				String name = jarEntry.getName();

				int last = name.lastIndexOf('/');
				if (last >= 0) {
					new File(dir, name.substring(0, last))
							.mkdirs();
				}
				if (name.endsWith("/")) {
					continue;
				}
				input = jarFile.getInputStream(jarEntry);
				expand(input, dir, name);
				input.close();
				input = null;
			}
		} catch (IOException e) {
			// problem, cleanup directory
			deleteDir(dir);
			throw e;
		} finally {
			if (input != null) {
				input.close();
				input = null;
			}
			if (jarFile != null) {
				jarFile.close();
				jarFile = null;
			}
		}
	}

	protected void deleteDir(File dir) {
		String files[] = dir.list();

        if (files != null) {
			// delete all content dir (recursively)
			for (int i = 0; i < files.length; i++) {
				File file = new File(dir, files[i]);
				if (file.isDirectory()) {
					deleteDir(file);
				} else {
					file.delete();
				}
			}
		}
        dir.delete();
	}

	protected void expand(InputStream input, File docBase, String name) throws IOException {
		File file = new File(docBase, name);
		BufferedOutputStream bos = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			byte buffer[] = new byte[2048];
			while (true) {
				int count = input.read(buffer);
				if (count <= 0) {
					break;
				}
				bos.write(buffer, 0, count);
			}
		} finally {
			bos.close();
		}
	}
}
