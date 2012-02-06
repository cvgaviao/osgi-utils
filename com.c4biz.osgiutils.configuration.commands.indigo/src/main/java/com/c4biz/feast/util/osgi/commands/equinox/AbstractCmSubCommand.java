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
import java.io.PrintStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public abstract class AbstractCmSubCommand implements CmSubCommand {
	
	protected String pid;
	protected ConfigurationAdmin configurationAdmin;
	protected PrintStream out;
	protected PrintStream err;

	
	
	/**
	 * @param  commandLine     the full command line as provided by the framework, can be null.
	 */
	public void execute(ConfigurationAdmin configurationAdmin, String cmd, List<String> args, String commandLine, PrintStream out, PrintStream err) {

		this.out = out;
		this.err = err;
		
		if (configurationAdmin == null) {
			error("no ConfigurationAdmin service");
			return;
		}

		try {
			int usedArgs = 1; // the first arg is the configurationAdmin command itself.
			if (args.size() >= 2) {
				pid = args.get(1);
				usedArgs++;
			}
			if (pid == null && needsPid()) {
				error("Missing argument: pid");
				return;
			}
			doCommand(configurationAdmin, cmd, args.subList(usedArgs, args.size()), commandLine);
		} catch (IOException e) {
			error("Configuration Admin service could not access persistent storage: " + e);
		}
	}
	
	protected boolean needsPid() {
		// Default is yes (because most commands do)
		return true;
	}

	protected void out(String line) {
		out.println(line);
	}
	
	protected void error(String line) {
		err.println(line);
	}
	
	public Configuration findConfiguration(String pid) throws IOException
	{
	    // As ConfigurationAdmin.getConfiguration creates the configuration if
        // it is not yet there, we check its existance first
        try {
            Configuration[] configurations = configurationAdmin.listConfigurations("(service.pid=" + pid + ")");
            if (configurations != null && configurations.length > 0) {
                return configurations[0];
            }
        } catch (InvalidSyntaxException e) {}
        
        return null;
	}
	
	public void print(Configuration configuration, boolean verbose)
	{
	    out.println("");
		out.println("Configuration for service (pid) \"" + configuration.getPid() + "\""); 
		out.println("(bundle location = " + configuration.getBundleLocation() + ")");
		print(configuration.getProperties(), verbose);
	}

	private void print(Dictionary<String, Object> properties, boolean verbose) {
	    int maxKeyLength = 0;
	    Enumeration<String> keys = properties.keys();
	    while (keys.hasMoreElements()) {
	        String key =  keys.nextElement();
	        if (key.length() > maxKeyLength) {
	            maxKeyLength = key.length();
	        }
	    }
	    int maxValueLength = 0;
        Enumeration<Object> values = properties.elements();
        while (values.hasMoreElements()) {
            Object value = values.nextElement();
            String stringValue = "";
            if (value != null)
                stringValue = value.toString();
            if (stringValue.length() > maxValueLength) {
                maxValueLength = stringValue.length();
            }
        }
	    String format = "%-" + (maxKeyLength + 2) + "s %-" + (maxValueLength + 6) + "s";
	    if (verbose)
	        format += " %s";
	    out.println();
        out.println(String.format(format, "key", "value", "type"));
        out.println(String.format(format, "------", "------", "------"));
        keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Object value = properties.get(key);
            out.println(String.format(format, key, value != null? value.toString(): "<null>", value != null? value.getClass().getName(): ""));
        }
        out.println("");
    }

    protected abstract void doCommand(ConfigurationAdmin configurationAdmin, String cmd, List<String> args, String commandLine)
		throws IOException;
}
