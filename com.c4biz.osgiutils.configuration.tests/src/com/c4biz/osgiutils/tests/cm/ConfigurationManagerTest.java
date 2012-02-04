package com.c4biz.osgiutils.tests.cm;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.c4biz.osgiutils.assertions.bundles.BundleAssert;
import com.c4biz.osgiutils.assertions.services.ServiceAssert;
import com.c4biz.osgiutils.assertions.services.ServiceUtils;
import com.c4biz.osgiutils.configuration.manager.service.IConfigurationService;

public class ConfigurationManagerTest {

	private static BundleContext bc;

	@BeforeClass
	public static void init() {
		bc = FrameworkUtil.getBundle(ConfigurationManagerTest.class)
				.getBundleContext();
		ServiceAssert.init(bc);
		BundleAssert.init(bc);
	}

	@Test
	public void ensureNeedBundlesWasInstalled() {

		// assert com.c4biz.osgiutils.assertions is ok
		BundleAssert.assertBundleAvailable(
				"Assertions bundle is not available",
				"com.c4biz.osgiutils.assertions");

		// assert org.eclipse.equinox.ds is ok
		BundleAssert.assertBundleAvailable("DS bundle is not available",
				"org.eclipse.equinox.ds");

		// assert org.eclipse.osgi.services is ok
		BundleAssert.assertBundleAvailable("Services bundle is not available",
				"org.eclipse.osgi.services");

		// assert org.eclipse.equinox.log is ok
		BundleAssert.assertBundleAvailable("Log bundle is not available",
				"org.eclipse.equinox.log");

		// assert org.eclipse.equinox.cm is ok
		BundleAssert.assertBundleAvailable("CM bundle is not available",
				"org.eclipse.equinox.cm");

		// assert org.eclipse.equinox.cm is ok
		BundleAssert.assertBundleAvailable("CM Wrapper bundle is not available",
				"com.c4biz.osgiutils.configuration.manager");

		// assert org.eclipse.equinox.cm is ok
		BundleAssert.assertBundleAvailable("CM fragment was not installed",
				"com.c4biz.osgiutils.configuration.manager.test.conf");
	}

	
	@Test
	public void ensureServicesWasRegistered() {
		
		// assert CM service is available
		ServiceAssert.assertServiceAvailable("CM was not available",
				"org.osgi.service.cm.ConfigurationAdmin");

		// assert Log service is available
		ServiceAssert.assertServiceAvailable("LogService was not available",
				"org.osgi.service.log.LogService");

		// assert CM wraper is available
		ServiceAssert
				.assertServiceAvailable("CM Wrapper was not available",
						"com.c4biz.osgiutils.configuration.manager.service.IConfigurationService");

	}
	

		@Test
		public void ensurePidWasRegistered() {

		IConfigurationService cms = ServiceUtils.getService(bc,
				IConfigurationService.class);
		Dictionary<String, Object> props = cms
				.getProperties("org.eclipse.equinox.http.jetty.config.test");

		Assert.assertEquals("PID was not registered properly.", "8088",
				props.get("http.port"));
	}


	@Test
	public void ensureFactoryPidWithDefaultPidWasRegistered() {
		
		IConfigurationService cms = ServiceUtils.getService(bc,
				IConfigurationService.class);
		Dictionary<String, Object> props = cms.getFactoryProperties(
				"org.eclipse.equinox.http.jetty.config", "");
		
		Assert.assertNotNull("Factory properties is null", props);

		// String actual = cms.getFactoryProperty(
		// "org.eclipse.equinox.http.jetty.config", "default", "http.port");
		// Assert.assertEquals("Property was not found in CM store.",
		// "8088", actual);
	}
}
