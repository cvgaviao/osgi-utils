/*******************************************************************************
 * Copyright (c) 2011 - 2012, Cristiano Gavião - C4Biz
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Cristiano Gavião - initial API and implementation
 *******************************************************************************/
package com.c4biz.osgiutils.vaadin6.shiro;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

import com.vaadin.Application;

public class WebResourcesHttpContext implements HttpContext {

	// the caller bundle
	private Bundle bundle;

	/**
	 * Creates the http context with the bundle
	 * 
	 * @param bundle
	 */
	public WebResourcesHttpContext(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public boolean handleSecurity(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		// security is handled by shiro
		// TODO study a poosibility to put shiro here

		return true;
	}

	@Override
	public URL getResource(final String name) {

		if (name.startsWith("/VAADIN/")) {
			return Application.class.getResource("/" + name);
		} else {
			return bundle.getResource(name);
		}

	}

	@Override
	public String getMimeType(final String name) {
		if (name.endsWith("js")) {
			return "text/javascript";
		} else if (name.endsWith("css")) {
			return "text/css";
		} else if (name.endsWith("html")) {
			return "text/html";
		} else if (name.endsWith("png")) {
			return "image/png";
		} else if (name.endsWith("gif")) {
			return "image/gif";
		} else {
			System.out.println("OUPAAAAAA : " + name);
			return null;
		}

	}
}
