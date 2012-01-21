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
package com.c4biz.osgiutils.configuration.manager.spi;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.service.prefs.PreferencesService;
import com.c4biz.osgiutils.configuration.manager.service.IConfigurationService;

public class SystemConfigurationComponent implements IConfigurationService {

	private ConfigurationAdmin configurationAdmin;
	private PreferencesService preferenceService;
	private LogService logService;
	private BundleContext bundleContext;


	/**
	 * It is possible to configure using a Properties File dropped on eclipse
	 * "dropins" folder, or the Properties cached in OSGi System Preference
	 * workspace. The system should be instructed to use the newer between both
	 * options, or you could choose one.
	 * 
	 * @param context
	 * @param properties
	 */
	protected void activate(ComponentContext context,
			Map<String, Object> properties) {

		bundleContext = context.getBundleContext();
		// get the configuration info from default
		String p_basePath = (String) properties
				.get(IConfigurationService.CONFIGURATION_BASE_DIR_FIELD_NAME);
		String p_includePattern = (String) properties
				.get(IConfigurationService.CONFIGURATION_INCLUDE_PATTERN_FIELD_NAME);
		String p_excludePattern = (String) properties
				.get(IConfigurationService.CONFIGURATION_EXCLUDE_PATTERN_FIELD_NAME);

		List<String> configFiles = scan(p_basePath, asList(p_includePattern),
				asList(p_excludePattern));
		for (String configFile : configFiles) {
			initializeConfigurationStore(
					extractPidFromConfigFileName(configFile),
					dictionaryFromPropertiesFile(configFile));
		}

	}

	protected void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
		getLogService().log(LogService.LOG_DEBUG,
				"Binded ConfigurationAdmin Service.");
	}

	protected void bindPreferenceService(PreferencesService preferenceService) {
		this.preferenceService = preferenceService;
		getLogService().log(LogService.LOG_DEBUG,
				"Binded Preferences Service.");
	}

	protected void deactivate(ComponentContext context,
			Map<String, Object> properties) {

		getLogService().log(LogService.LOG_DEBUG,
				"Binded Preferences Service.");
	}

	public void deleteProperties(String pid) {
		Configuration configuration;
		try {
			configuration = findConfiguration(pid);
			if (configuration != null)
				try {
					configuration.delete();
				} catch (IOException e) {
					getLogService().log(LogService.LOG_ERROR,
							"Error on setup Configuration Service", e);
				}
			else
				getLogService().log(LogService.LOG_DEBUG,
						"no configuration for pid '" + pid + "'");
		} catch (IOException e1) {
			getLogService().log(LogService.LOG_ERROR,
					"no configuration for pid '" + pid + "'", e1);
		}

	}

	private Dictionary<String, Object> dictionaryFromPropertiesFile(
			String configFile) {
		// Read properties file.
		Properties properties = new Properties();
		Dictionary<String, Object> map = new Hashtable<String, Object>();

		try {
			URL configURL = bundleContext.getBundle().getResource(
					configFile);
			if (configURL != null) {
				InputStream input = null;
				try {
					input = configURL.openStream();
					properties.load(input);

					// process your input here or in separate method
					Enumeration<?> list = properties.propertyNames();
					while (list.hasMoreElements()) {
						String propertyName = (String) list.nextElement();
						map.put(propertyName, properties.get(propertyName));
					}

				} finally {
					input.close();
				}
			} else {
				getLogService()
						.log(LogService.LOG_ERROR,
								"Error reading configuration file '"
										+ configFile + "'");
			}

		} catch (IOException e) {
			getLogService().log(LogService.LOG_ERROR,
					"Error reading configuration file '" + configFile + "'", e);
		}
		return map;
	}

	private String extractPidFromConfigFileName(String configFile) {
		String separator = System.getProperty("file.separator");
		String filename;

		// Remove the path up to the filename.
		int lastSeparatorIndex = configFile.lastIndexOf(separator);
		if (lastSeparatorIndex == -1) {
			filename = configFile;
		} else {
			filename = configFile.substring(lastSeparatorIndex + 1);
		}
		
		// Remove qualifier (if it exists)
		int qualifierIndex = filename.lastIndexOf("_");
		if (qualifierIndex != -1)
			return filename.substring(0, qualifierIndex);

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");
		if (extensionIndex == -1)
			return filename;

		return filename.substring(0, extensionIndex);
	}

	protected Configuration findConfiguration(String pid) throws IOException {
		// As ConfigurationAdmin.getConfiguration creates the configuration if
		// it is not yet there, we check its existence first
		try {
			Configuration[] configurations = getConfigurationAdminService()
					.listConfigurations("(service.pid=" + pid + ")");
			if (configurations != null && configurations.length > 0) {
				return configurations[0];
			}
		} catch (InvalidSyntaxException e) {
		}

		return null;
	}

	protected ConfigurationAdmin getConfigurationAdminService() {
		return configurationAdmin;
	}


	protected PreferencesService getPreferenceService() {
		return preferenceService;
	}

	@SuppressWarnings("unchecked")
	public Dictionary<String, Object> getProperties(String pid) {
		try {
			Dictionary<String, Object> allProperties = new Hashtable<String, Object>();
			Configuration[] configurations = getConfigurationAdminService()
					.listConfigurations("(service.pid=" + pid + ")");
			if (configurations != null && configurations.length > 0) {
				for (Configuration configuration : configurations) {
					Enumeration<String> keys = configuration.getProperties()
							.keys();
					while (keys.hasMoreElements()) {
						String object = keys.nextElement();
						Object value = configuration.getProperties()
								.get(object);
						allProperties.put(object, value);
					}
				}
				return allProperties;
			}
		} catch (InvalidSyntaxException e) {
			getLogService().log(LogService.LOG_ERROR,
					"Error on setup Configuration Service", e);
		} catch (IOException e) {
			getLogService().log(LogService.LOG_ERROR,
					"Error on setup Configuration Service", e);
		}

		return null;
	}

	public Object getProperty(String pid, String propertyName) {
		Configuration configuration;
		try {
			configuration = findConfiguration(pid);
			if (configuration != null)
				return configuration.getProperties().get(propertyName);
			else
				getLogService().log(
						LogService.LOG_WARNING,
						"no configuration for pid '" + pid
								+ "' (use 'create' to create one)");
		} catch (IOException e) {
			getLogService().log(LogService.LOG_ERROR,
					"Error on setup Configuration Service", e);
		}
		return null;
	}

	@Override
	public void initializeConfigurationStore(String pid) {
		Configuration configuration;

		try {

			if (configurationAdmin == null)
				throw new RuntimeException(
						"Configuration Admin Manager was not wired !!!");

			configuration = configurationAdmin.getConfiguration(pid, null);
			// Ensure update is called, when properties are null; otherwise
			// configuration will not
			// be returned when listConfigurations is called (see specification
			// 104.15.3.7)
			if (configuration.getProperties() == null) {
				configuration.update(new Hashtable<String, String>());
				getLogService().log(LogService.LOG_DEBUG,
						"Initialized store under pid: '" + pid + "'.");
			} else {
				getLogService().log(LogService.LOG_WARNING,
						"duplicated data !!!");
			}
		} catch (IOException e) {
			getLogService().log(LogService.LOG_ERROR,
					"Error on setup Configuration Service.", e);
		}
	}

	@Override
	public void initializeConfigurationStore(String pid,
			Dictionary<String, Object> properties) {
		Configuration configuration;

		try {

			if (configurationAdmin == null)
				throw new RuntimeException(
						"Configuration Admin Manager was not wired !!!");

			configuration = configurationAdmin.getConfiguration(pid, null);
			// Ensure update is called, when properties are null; otherwise
			// configuration will not
			// be returned when listConfigurations is called (see specification
			// 104.15.3.7)
			if (configuration.getProperties() == null) {
				configuration.update(properties);
				getLogService().log(
						LogService.LOG_DEBUG,
						"Initialized store under PID: '" + pid + "', with this properties: " + properties.toString());
			} else {
				getLogService()
						.log(LogService.LOG_ERROR, "duplicated data !!!");
			}
		} catch (IOException e) {
			getLogService().log(LogService.LOG_ERROR,
					"Error on setup Configuration Service", e);
		}
	}


	@Override
	public void putProperties(String pid, Dictionary<String, Object> properties) {
		Configuration config;
		try {
			config = findConfiguration(pid);
			if (config == null) {
				getLogService().log(
						LogService.LOG_ERROR,
						"no configuration for pid '" + pid
								+ "' (use 'create' to create one)");
				return;
			}

			if (properties != null) {

				config.update(properties);
			}
		} catch (IOException e) {
			getLogService().log(LogService.LOG_ERROR,
					"Error on setup Configuration Service", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void putProperty(String pid, String propertyName, Object value) {
		Configuration config;
		try {
			config = findConfiguration(pid);
			if (config == null) {
				getLogService().log(
						LogService.LOG_ERROR,
						"no configuration for pid '" + pid
								+ "' (use 'create' to create one)");
				return;
			}
			if (value != null) {
				Dictionary<String, Object> properties = config.getProperties();
				if (properties == null)
					properties = new Hashtable<String, Object>();

				properties.put(propertyName, value);
				config.update(properties);
			}
		} catch (IOException e) {
			getLogService().log(
					LogService.LOG_ERROR,
					"no configuration for pid '" + pid
							+ "' (use 'create' to create one)");

		}

	}

	protected List<String> scan(String basedir, List<String> includes,
			List<String> excludes) {

		List<String> scannedItens = new ArrayList<String>();
		BundleContext ctx = FrameworkUtil.getBundle(getClass())
				.getBundleContext();
		BundleWiring wiring = ctx.getBundle().adapt(BundleWiring.class);

		if (includes != null) {
			for (String filePattern : includes) {
				Collection<String> foundIncludedFiles = wiring.listResources(
						basedir, filePattern,
						BundleWiring.LISTRESOURCES_RECURSE);
				if (foundIncludedFiles != null && !foundIncludedFiles.isEmpty()) {
					scannedItens.addAll(foundIncludedFiles);
				}
			}
		}

		if (excludes != null) {
			for (String filePattern : excludes) {
				Collection<String> foundExcludeFiles = wiring.listResources(
						basedir, filePattern,
						BundleWiring.LISTRESOURCES_RECURSE);
				if (foundExcludeFiles != null && !foundExcludeFiles.isEmpty()) {
					scannedItens.removeAll(foundExcludeFiles);
				}
			}
		}

		return scannedItens;
	}

	protected void unbindConfigurationAdmin(
			ConfigurationAdmin configurationAdmin) {
		if (this.configurationAdmin == configurationAdmin)
			this.configurationAdmin = null;
		getLogService().log(LogService.LOG_DEBUG,
				"Unbinded ConfigurationAdminService");
	}

	protected void unbindPreferenceService(PreferencesService preferenceService) {
		if (this.preferenceService == preferenceService)
			this.preferenceService = null;
		getLogService().log(LogService.LOG_DEBUG,
				"Unbinded Preferences Service");
	}
	
	protected void bindLogService(LogService logService) {
		this.logService = logService;
		getLogService().log(LogService.LOG_DEBUG, "Binded LogService.");
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
}
