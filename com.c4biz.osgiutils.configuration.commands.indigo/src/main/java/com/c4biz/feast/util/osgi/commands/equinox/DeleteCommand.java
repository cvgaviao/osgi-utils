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
import java.util.List;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class DeleteCommand extends AbstractCmSubCommand {

	@Override
	protected void doCommand(ConfigurationAdmin configurationAdmin, String cmd,
			List<String> args, String commandLine) throws IOException {

		Configuration configuration = findConfiguration(pid);
		if (configuration != null)
			configuration.delete();
		else
			out.println("no configuration for pid '" + pid + "'");
	}
}
