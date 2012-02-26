package com.c4biz.osgiutils.vaadin.equinox.shiro;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.osgi.service.component.ComponentInstance;

import com.vaadin.Application;

/**
 * Track the component instance and session and hold an Application object.
 * 
 * @author cvgaviao
 */
public class VaadinSession implements HttpSessionBindingListener {

	final ComponentInstance instance;

	final HttpSession session;

	private Application app;

	public Application getApp() {
		return app;
	}

	public VaadinSession(ComponentInstance instance, HttpSession session) {
		this.instance = instance;
		this.session = session;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		app = (Application) instance.getInstance();
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		if (app != null) {
//			app.close();
//			app = null;
		}
		if (instance != null){
//			instance.dispose();
		}
	}
}
