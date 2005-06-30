package org.nanocontainer.nanowar.nanoweb.example.bookmarker;

import java.io.File;
import java.net.URL;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.w3c.dom.Document;

/**
 * @version $Id$
 */
public class ConstructableAnnotationConfiguration extends AnnotationConfiguration {

	public ConstructableAnnotationConfiguration() throws HibernateException {
		this.configure();
	}

	public ConstructableAnnotationConfiguration(URL url) throws HibernateException {
		this.configure(url);
	}

	public ConstructableAnnotationConfiguration(String resource) throws HibernateException {
		this.configure(resource);
	}

	public ConstructableAnnotationConfiguration(File configFile) throws HibernateException {
		this.configure(configFile);
	}

	public ConstructableAnnotationConfiguration(Document document) throws HibernateException {
		this.configure(document);
	}

}
