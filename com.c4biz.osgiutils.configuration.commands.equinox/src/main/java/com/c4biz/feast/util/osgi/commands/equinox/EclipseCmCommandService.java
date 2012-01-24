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
package com.c4biz.feast.util.osgi.commands.equinox;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;

public class EclipseCmCommandService implements CommandProvider {

	protected BundleContext bundleContext;
	private LogService logService;
	private ConfigurationAdmin configurationAdmin;


	public EclipseCmCommandService() {
	}

	public EclipseCmCommandService(BundleContext context) {
		this.bundleContext = context;
	}

	@Override
	public String getHelp() {
		StringBuffer help = new StringBuffer();
		help.append("\r\n--- Configuration Management Service ---\r\n");
		help.append("\tcm [help|list|get|put|create|creatf] ...");
		help.append("\r\n");
		return help.toString();
	}


	public void _cm(CommandInterpreter ci) {
		List<String> args = new ArrayList<String>();
		String arg = null;
		while ((arg = ci.nextArgument()) != null) {
			args.add(arg);
		}

		new CmCommandProcessor(configurationAdmin).execute(args, null, System.out,
				System.err);
	}

	protected void bindLogService(LogService logService) {
		this.logService = logService;
		getLogService().log(LogService.LOG_DEBUG, "Binded LogService.");
	}

	protected void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
		getLogService().log(LogService.LOG_DEBUG,
				"Binded ConfigurationAdmin Service.");
	}

	protected ConfigurationAdmin getConfigurationAdminService() {
		return configurationAdmin;
	}

	protected LogService getLogService() {
		return logService;
	}

	protected void unbindLogService(LogService logService) {
		if (this.logService == logService) {
			getLogService().log(LogService.LOG_DEBUG, "Unbinded LogService.");
			this.logService = null;
		}
	}
	protected void unbindConfigurationAdmin(
			ConfigurationAdmin configurationAdmin) {
		if (this.configurationAdmin == configurationAdmin)
			this.configurationAdmin = null;
		getLogService().log(LogService.LOG_DEBUG,
				"Unbinded ConfigurationAdminService");
	}

}
