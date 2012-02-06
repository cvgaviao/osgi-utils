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

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class ListCommand extends AbstractCmSubCommand {

	@Override
	protected boolean needsPid() {
		return false;
	}

	@Override
	protected void doCommand(ConfigurationAdmin configurationAdmin, String cmd,
			List<String> args, String commandLine) throws IOException {

		try {
			Configuration[] configurations;
			configurations = configurationAdmin.listConfigurations(null);
			out("Configuration list:");
			if (configurations == null || configurations.length == 0) {
				out("   (none)");
				return;
			}
			int maxPidLength = 0;
			for (Configuration c : configurations) {
				if (c.getPid().length() > maxPidLength) {
					maxPidLength = c.getPid().length();
				}
			}
			int tabPosition = maxPidLength + 4;
			for (Configuration c : configurations) {
				String pid = c.getPid();
				String bundleLocation = c.getBundleLocation();
				String tab = "";
				for (int i = tabPosition; i >= pid.length(); i--)
					tab += " ";
				out("* " + pid
						+ (bundleLocation != null ? tab + bundleLocation : ""));
				Enumeration<?> properties = c.getProperties().keys();
				while (properties.hasMoreElements()) {
					String key = (String) properties.nextElement();
					String value = (String) c.getProperties().get(key);
					if (!key.equals("service.pid"))
						out(tab + key + "=" + value);
				}
			}
		} catch (InvalidSyntaxException e) {
			// impossible
		}
	}
}
