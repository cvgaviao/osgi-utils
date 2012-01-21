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

import org.eclipse.equinox.log.ExtendedLogEntry;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class OsgiLogListener implements LogListener {
	
	@Override
	public void logged(LogEntry entry) {
		Logger logger = getLogger(entry);
		Marker marker = getBundleMarker(entry);
		String message = getMessageWithServiceReference(entry);
		logLogEntry(logger, entry.getLevel(), marker, message, entry.getException());
	}
	
	private void logLogEntry(Logger logger, int level, Marker marker, String message, Throwable e) {
		// log it dependent from Log Level
		if (e == null) {
			switch (level) {
			case LogService.LOG_DEBUG:
				logger.debug(marker, message);
				break;
			case LogService.LOG_ERROR:
				logger.error(marker, message);
				break;
			case LogService.LOG_WARNING:
				logger.warn(marker, message);
				break;
			default:
				logger.info(marker, message);
				break;
			}
		}
		else {
			switch (level) {
			case LogService.LOG_DEBUG:
				logger.debug(marker, message, e);
				break;
			case LogService.LOG_ERROR:
				logger.error(marker, message, e);
				break;
			case LogService.LOG_WARNING:
				logger.warn(marker, message, e);
				break;
			default:
				logger.info(marker, message, e);
				break;
			}
		}
	}
	
	private String getMessageWithServiceReference(LogEntry entry) {
		if (entry.getServiceReference() != null) {
			return entry.getMessage() 
					+ LoggingConstants.SERVICE_PREFIX 
					+ entry.getServiceReference().toString()
					+ LoggingConstants.SERVICE_POSTFIX;
		}
		return entry.getMessage();
	}
	
	private Logger getLogger(LogEntry entry) {
		Logger logger;
		if (entry instanceof ExtendedLogEntry && hasLoggerName((ExtendedLogEntry) entry)) {
			ExtendedLogEntry extendedEntry = (ExtendedLogEntry) entry;
			logger = LoggerFactory.getLogger(extendedEntry.getLoggerName());
		}
		else if (hasBundleName(entry))
			logger = LoggerFactory.getLogger(entry.getBundle().getSymbolicName());
		else
			logger = LoggerFactory.getLogger(LoggingConstants.IS_OSGI_LOG_MARKER);
		return logger;
	}


	private boolean hasBundleName(LogEntry entry) {
		return entry.getBundle() != null && entry.getBundle().getSymbolicName() != null;
	}

	private boolean hasLoggerName(ExtendedLogEntry extendedEntry) {
		return extendedEntry.getLoggerName() != null && extendedEntry.getLoggerName().length() > 0;
	}

	private static Marker getBundleMarker(LogEntry le) {
		Marker marker;
		if (le.getBundle() != null && le.getBundle().getSymbolicName() != null)
			marker = MarkerFactory.getMarker(le.getBundle().getSymbolicName());
		else
			marker = MarkerFactory.getMarker(LoggingConstants.NO_BUNDLE);

		if (!marker.contains(MarkerFactory.getMarker(LoggingConstants.IS_BUNDLE_MARKER)))
			marker.add(MarkerFactory.getMarker(LoggingConstants.IS_BUNDLE_MARKER));
		
		if (le instanceof ExtendedLogEntry)
			marker.add(MarkerFactory.getMarker(LoggingConstants.IS_EXTENDED_OSGI_LOG_MARKER));
		
		return marker;
	}

}