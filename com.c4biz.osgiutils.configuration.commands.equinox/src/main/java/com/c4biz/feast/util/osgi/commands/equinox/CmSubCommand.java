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

import java.io.PrintStream;
import java.util.List;

import org.osgi.service.cm.ConfigurationAdmin;

public interface CmSubCommand {

	public void execute(ConfigurationAdmin configurationAdmin, String cmd, List<String> args,
			String commandLine, PrintStream out, PrintStream err);
}
