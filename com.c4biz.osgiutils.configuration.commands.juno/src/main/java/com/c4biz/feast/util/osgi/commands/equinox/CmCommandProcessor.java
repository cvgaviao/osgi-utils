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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.cm.ConfigurationAdmin;

public class CmCommandProcessor {

	private ConfigurationAdmin configAdminService;
	private Map<String, CmSubCommand> commands;

	public CmCommandProcessor(ConfigurationAdmin configurationAdmin) {
		this.configAdminService = configurationAdmin;
		commands = new HashMap<String, CmSubCommand>();
		commands.put("help", new Help());
		commands.put("list", new ListCommand());
		commands.put("get", new GetCommand());
		commands.put("getv", new GetCommand());
		commands.put("put", new PutCommand());
		commands.put("puts", new PutCommand());
		commands.put("del", new DeleteCommand());
		commands.put("create", new CreateCommand());
		commands.put("createf", new CreateFactoryCommand());
	}

	public void execute(List<String> args, String commandLine, PrintStream out,
			PrintStream err) {

		if (args.size() >= 1) {
			String cmd = (String) args.get(0);
			if (commands.containsKey(cmd)) {
				((CmSubCommand) commands.get(cmd)).execute(configAdminService, cmd, args,
						commandLine, out, err);
			} else {
				new Help().execute(configAdminService, null, args, null, out, err);
			}
		} else {
			new Help().execute(configAdminService, null, args, null, out, err);
		}
	}

	static public class Help implements CmSubCommand {

		public void execute(ConfigurationAdmin configurationAdmin, String cmd,
				List<String> args, String cmdLine, PrintStream out,
				PrintStream err) {

			err.println("Usage:");
			err.println(" cm help                  print this help message");
			err.println(" cm list                  list all known configurations");
			err.println(" cm get <pid>             show configuration for service <pid>");
			err.println(" cm getv <pid>            verbose get (shows value types also)");
			err.println(" cm put <pid> key value   set string value for service <pid>");
			err.println(" cm puts <pid> key value  set \"simple\" value for service <pid>: value is \"true\", \"false\",");
			err.println("                          a char in single quotes, an int, or a number, with appended: ");
			err.println("                          i (Integer), l (Long), f (Float), d (Double), b (Byte), s (Short)");
			err.println(" cm del <pid>             deletes configuration for service <pid>");
			err.println(" cm create <pid> [<loc>]  creates configuration for service <pid> (with optional bundle location)");
			err.println(" cm createf <factoryPid> [<loc>] creates configuration for service factory <factoryPid> (with optional bundle location)");
		}
	}

}
