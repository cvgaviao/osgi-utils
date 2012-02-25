package com.c4biz.osgiutils.vaadin.equinox.shiro;

import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.equinox.http.servlet.ExtendedHttpService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This tracker takes a {@link ComponentFactory} and then creates a
 * {@link ApplicationRegister} class which is then registered as a
 * {@link ManagedService} to receive configuration for that specific
 * application.
 * 
 * @author brindy - initial contribution 
 *         cvgaviao - using ExtendedHttpService that added support to filters
 */
@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class HttpServiceTracker extends ServiceTracker{

	private final ComponentFactory factory;

	private final String alias;
	
	public String getAlias() {
		return alias;
	}

	private final LogService logService;

	private Map<ExtendedHttpService, ApplicationRegister> configs = new IdentityHashMap<ExtendedHttpService, ApplicationRegister>();

	public HttpServiceTracker(BundleContext ctx, ComponentFactory factory,
			String alias, LogService logService) {
		super(ctx, ExtendedHttpService.class.getName(), null);
		this.factory = factory;
		this.alias = alias;
		this.logService = logService;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		ExtendedHttpService http = (ExtendedHttpService) super
				.addingService(reference);


		// register the application
		ApplicationRegister config = new ApplicationRegister(http, factory,
				alias);

		logService.log(LogService.LOG_DEBUG,
				"Application for alias \"" + alias + "\" was created.");

		// save it for later
		configs.put(http, config);

		// register as a managed service so that alternative properties can
		// be provided
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties
				.put(Constants.SERVICE_PID, VaadinApplicationTracker.PREFIX + "." + alias);
		context.registerService(ManagedService.class.getName(), config,
				properties);

		return http;
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		configs.remove(service).kill();
		logService.log(LogService.LOG_DEBUG,
				"Application for alias \"" + alias + "\" was removed.");

		super.removedService(reference, service);
	}

}
