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

public class EclipseCmCommandService implements CommandProvider {

	protected BundleContext bundleContext;

	public EclipseCmCommandService() {
	}

	public EclipseCmCommandService(BundleContext context) {
		this.bundleContext = context;
	}

	public String getHelp() {
		return "\tcm help|list|get... - Access OSGi Configuration Admin service";
	}

	public void _cm(CommandInterpreter ci) {
		List<String> args = new ArrayList<String>();
		String arg = null;
		while ((arg = ci.nextArgument()) != null) {
			args.add(arg);
		}

		new CmCommandProcessor(bundleContext).execute(args, null, System.out,
				System.err);
	}
}
