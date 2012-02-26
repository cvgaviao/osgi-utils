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
 */
package com.c4biz.osgiutils.vaadin.equinox.shiro;

import java.util.Dictionary;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.ComponentFactory;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

/**
 * Used to create instances of applications that have been registered with the
 * container via a component factory.
 * 
 * @author brindy
 */
@SuppressWarnings("rawtypes")
class VaadinOSGiServlet extends AbstractApplicationServlet {

	private static final long serialVersionUID = 1L;

	private final ComponentFactory factory;
	
	private final Dictionary properties;
	
	private Class<? extends Application> klazz;

	public VaadinOSGiServlet(ComponentFactory factory, Dictionary properties) {
		this.factory = factory;
		this.properties = properties;
	}

	@Override
	protected Class<? extends Application> getApplicationClass()
			throws ClassNotFoundException {
		
		return klazz;
	}
	
	@Override
	protected Application getNewApplication(HttpServletRequest request)
			throws ServletException {
		
		// create a vaadin session for each application
		final VaadinSession vaadinSession = new VaadinSession(
				factory.newInstance(properties), request.getSession());

		vaadinSession.session.setAttribute(VaadinOSGiServlet.class.getName(),
				vaadinSession);
	    klazz = vaadinSession.getApp().getClass();
		
		return (Application) vaadinSession.getApp();
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
}
