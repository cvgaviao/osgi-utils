/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Information:
 * The originial source (org.vaadin.osgi.Activator) was written by Chris Brind
 * 
 * Contribution:
 * Cristiano Gavião - Modified for integrate the Shiro security layer and other changes.
 */

package com.c4biz.osgiutils.vaadin.equinox.shiro;

import org.eclipse.equinox.http.jetty.JettyConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.log.LogService;

import com.vaadin.Application;

/**
 * Activator for this bundle which opens a service vaadinAppTracker which looks for
 * {@link ComponentFactory} registrations with the name
 * <code>com.vaadin.Application/*</code> where * is the alias under which to
 * register the Vaadin application.
 * 
 * <p/>
 * In order to turn production mode on a configuration must be provided for the
 * application. The PID to use is
 * <code>com.vaadin.Application.<em>alias</em></code>.
 * 
 * <p/>
 * An easy way to provide this configuration is to use FileInstall and create a
 * file of the same name as the PID but with the extension .cfg. e.g.
 * <code>com.vaadin.Application/guessit</code> would require a file called
 * <code>com.vaadin.Application.guessit.cfg</code>. The contents of this file
 * would contain the property <code>productionMode=true</code> and any other
 * parameters that would normally passed to the Vaadin servlet as init
 * parameters.
 * 
 * @author brindy (with help from Neil Bartlett) 
 *         cvgaviao - Integration with Shiro Security Framework.
 */
public class Activator implements BundleActivator {

	private VaadinApplicationTracker vaadinAppTracker;
	private LogService logService;
	private Bundle jettyBundle;
	private Bundle vaadinBundle;

	protected static BundleContext bundleContext;

	protected void bindLogService(BundleContext context) {
		ServiceReference<LogService> ref = context
				.getServiceReference(LogService.class);
		logService = context.getService(ref);

		logService.log(LogService.LOG_DEBUG, "Binded LogService.");
	}

	protected LogService getLogService() {
		return logService;
	}

	@Override
	public void start(BundleContext context) throws Exception {

		bundleContext = context;

		// bind the log service
		bindLogService(context);

		// start the jetty with data from CM
		startJetty(context);

		// start the Vaadin bundle
		startVaadin(context);

		// start the application tracker that will be waiting for a Vaadin application get registered.
		vaadinAppTracker = new VaadinApplicationTracker(context, logService);
		vaadinAppTracker.open();
	}

	private void startJetty(BundleContext context) {

		jettyBundle = FrameworkUtil.getBundle(JettyConstants.class);
		if (jettyBundle == null) {
			getLogService()
					.log(LogService.LOG_ERROR,
							"Bundle org.eclipse.equinox.http.jetty is not in target platform");
		}

	}

	private void startVaadin(BundleContext context) {

		vaadinBundle = FrameworkUtil.getBundle(Application.class);
		if (vaadinBundle == null) {
			getLogService().log(LogService.LOG_ERROR,
					"Bundle com.vaadin is not in target platform");
		}

		// vaadin bundle doesn't have auto-start, so need to start it
		try {
			vaadinBundle.start();
		} catch (BundleException e) {
			getLogService().log(LogService.LOG_ERROR,
					"Bundle com.vaadin had error on start up.", e);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		vaadinAppTracker.close();

		vaadinBundle.stop();

		jettyBundle.stop();

		vaadinAppTracker = null;
		vaadinBundle = null;
		jettyBundle = null;
		logService = null;
	}

}
