package com.c4biz.osgiutils.vaadin6.shiro;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletCotextListenerServletAdaptor implements Servlet {
	private ServletConfig config;
	private ServletContextListener listener;
	private Servlet delegate;

	public ServletCotextListenerServletAdaptor(ServletContextListener listener,
			Servlet delegate) {
		this.listener = listener;
		this.delegate = delegate;
	}

	public void init(ServletConfig config) throws ServletException {
		this.config = config;
		listener.contextInitialized(new ServletContextEvent(config
				.getServletContext()));
		delegate.init(config);
	}

	public void service(ServletRequest req, ServletResponse resp)
			throws ServletException, IOException {
		delegate.service(req, resp);
	}

	public void destroy() {
		delegate.destroy();
		listener.contextDestroyed(new ServletContextEvent(config
				.getServletContext()));
		config = null;
	}

	public ServletConfig getServletConfig() {
		return config;
	}

	public String getServletInfo() {
		return "";
	}
}
