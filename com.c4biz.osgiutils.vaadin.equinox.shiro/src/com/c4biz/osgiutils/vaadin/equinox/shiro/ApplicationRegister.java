package com.c4biz.osgiutils.vaadin.equinox.shiro;

import java.util.Dictionary;

import javax.servlet.Filter;

import org.apache.shiro.web.servlet.IniShiroFilter;
import org.eclipse.equinox.http.servlet.ExtendedHttpService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.http.HttpContext;

import com.vaadin.Application;

/**
 * This class is responsible for registering the {@link ComponentFactory} as a
 * vaadin {@link Application}. It is a {@link ManagedService} so that it can
 * receive properties which are then passed in to the {@link VaadinOSGiServlet}
 * as init parameters, e.g. to enable production mode.
 * 
 * @author brindy cvgaviao
 */
@SuppressWarnings("deprecation")
public class ApplicationRegister implements ManagedService {

	private final ExtendedHttpService http;

	private final ComponentFactory factory;

	private final String alias;

	private VaadinOSGiServlet servlet;

	private Filter filter;

	private final String RESOURCE_BASE = "/VAADIN";

	public ApplicationRegister(ExtendedHttpService http,
			ComponentFactory factory, String alias) {
		super();
		this.http = http;
		this.factory = factory;
		this.alias = alias;
	}

	public void kill() {
		if (filter != null)
			http.unregisterFilter(filter);
		if (alias != null) {
			try {
				http.unregister(alias);
				http.unregister(RESOURCE_BASE);
			} catch (java.lang.IllegalArgumentException e) {
				// ignore in case alias was not found exception
			}
		}
		if (servlet != null) {
			servlet.destroy();
			servlet = null;
		}
	}

	@Override
	public void updated(@SuppressWarnings("rawtypes") Dictionary properties)
			throws ConfigurationException {
		kill();

		try {
			servlet = new VaadinOSGiServlet(factory, properties);

			HttpContext defaultContext = new WebResourcesHttpContext(
					Activator.bundleContext.getBundle());
			http.registerFilter("/", getSecurityFilter(), properties,
					defaultContext);
			http.registerResources(RESOURCE_BASE, RESOURCE_BASE, defaultContext);
			http.registerServlet(alias, servlet, properties, defaultContext);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Filter getSecurityFilter() {
		if (filter == null) {
			filter = new IniShiroFilter();
		}
		return filter;
	}
}
