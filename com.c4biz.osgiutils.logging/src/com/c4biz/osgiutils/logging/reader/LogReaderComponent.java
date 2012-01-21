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
package com.c4biz.osgiutils.logging.reader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogReaderService;

/**
 * Currently only tracks LogReaderService
 */
public class LogReaderComponent {

	private Map<LogReaderService, OsgiLogListener> logReaderMap = Collections
			.synchronizedMap(new HashMap<LogReaderService, OsgiLogListener>());
	
	public void activate(ComponentContext context) {}

	public void deactivate(ComponentContext context) {}
	
	public void addLogReaderService(LogReaderService aLogReaderService) {
		OsgiLogListener listener = new OsgiLogListener();
		logReaderMap.put(aLogReaderService, listener);
		aLogReaderService.addLogListener(listener);
	}
	
	public void removeLogReaderService(LogReaderService aLogReaderService) {
		OsgiLogListener listener = logReaderMap.get(aLogReaderService);
		if (listener != null) {
			aLogReaderService.removeLogListener(listener);
		}
		logReaderMap.remove(aLogReaderService);
	}
	
}