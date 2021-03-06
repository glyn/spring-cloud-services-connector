/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.spring.cloud.service.eureka;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for
 * {@link io.pivotal.spring.cloud.service.eureka.EurekaInstanceAutoConfiguration}
 *
 * @author Chris Schaefer
 * @author Will Tran
 */
public class EurekaInstanceAutoConfigurationTest {
	private static final String ROUTE_REGISTRATION_METHOD = "route";
	private static final String DIRECT_REGISTRATION_METHOD = "direct";
	private static final String HOSTNAME = "www.route.com";
	private static final String IP = "1.2.3.4";
	private static final int PORT = 54321;
	private static final String INSTANCE_ID = UUID.randomUUID().toString();
	private static final String APPNAME = "test-app";
	private static final String APPNAME_INVALID_AS_HOSTNAME = "My.1st-test_app+";
	private static final String SANITISED_APPNAME_INVALID_AS_HOSTNAME = "My.1st-test-app-";
	private EurekaInstanceAutoConfiguration eurekaInstanceAutoConfiguration;

	@Before
	public void setup() {
		eurekaInstanceAutoConfiguration = new EurekaInstanceAutoConfiguration();
		eurekaInstanceAutoConfiguration.setHostname(HOSTNAME);
		eurekaInstanceAutoConfiguration.setInstanceId(INSTANCE_ID);
		eurekaInstanceAutoConfiguration.setIp(IP);
		eurekaInstanceAutoConfiguration.setPort(PORT);
		eurekaInstanceAutoConfiguration.setAppname(APPNAME);
	}

	@Test
	public void testRouteRegistration() {
		eurekaInstanceAutoConfiguration.setRegistrationMethod(ROUTE_REGISTRATION_METHOD);
		testDefaultRegistration();
	}

	@Test
	public void testDefaultRegistration() {
		EurekaInstanceConfigBean eurekaInstanceConfigBean = eurekaInstanceAutoConfiguration.eurekaInstanceConfigBean();
		assertEquals(HOSTNAME + ":" + INSTANCE_ID, eurekaInstanceConfigBean.getInstanceId());
		assertEquals(HOSTNAME, eurekaInstanceConfigBean.getHostname());
		assertEquals(80, eurekaInstanceConfigBean.getNonSecurePort());
		assertEquals(443, eurekaInstanceConfigBean.getSecurePort());
		assertTrue(eurekaInstanceConfigBean.getSecurePortEnabled());
		assertEquals(INSTANCE_ID, eurekaInstanceConfigBean.getMetadataMap().get("instanceId"));
		assertEquals(APPNAME, eurekaInstanceConfigBean.getVirtualHostName());
		assertEquals(APPNAME, eurekaInstanceConfigBean.getSecureVirtualHostName());
	}

	@Test
	public void testDefaultRegistrationSanitisesVirtualHostNames() {
		eurekaInstanceAutoConfiguration.setAppname(APPNAME_INVALID_AS_HOSTNAME);
		EurekaInstanceConfigBean eurekaInstanceConfigBean = eurekaInstanceAutoConfiguration.eurekaInstanceConfigBean();
		assertEquals(SANITISED_APPNAME_INVALID_AS_HOSTNAME, eurekaInstanceConfigBean.getVirtualHostName());
		assertEquals(SANITISED_APPNAME_INVALID_AS_HOSTNAME, eurekaInstanceConfigBean.getSecureVirtualHostName());
	}

	@Test
	public void testProvidedVirtualHostNameIsNotOverridden() {
		eurekaInstanceAutoConfiguration.setProvidedVirtualHostname("provided-virtual-hostname");
		EurekaInstanceConfigBean eurekaInstanceConfigBean = eurekaInstanceAutoConfiguration.eurekaInstanceConfigBean();
		assertEquals("provided-virtual-hostname", eurekaInstanceConfigBean.getVirtualHostName());
		assertEquals(APPNAME, eurekaInstanceConfigBean.getSecureVirtualHostName());
	}

	@Test
	public void testProvidedSecureVirtualHostNameIsNotOverridden() {
		eurekaInstanceAutoConfiguration.setProvidedSecureVirtualHostname("provided-secure-virtual-hostname");
		EurekaInstanceConfigBean eurekaInstanceConfigBean = eurekaInstanceAutoConfiguration.eurekaInstanceConfigBean();
		assertEquals(APPNAME, eurekaInstanceConfigBean.getVirtualHostName());
		assertEquals("provided-secure-virtual-hostname", eurekaInstanceConfigBean.getSecureVirtualHostName());
	}

	@Test
	public void testDirectRegistration() {
		eurekaInstanceAutoConfiguration.setRegistrationMethod(DIRECT_REGISTRATION_METHOD);
		EurekaInstanceConfigBean eurekaInstanceConfigBean = eurekaInstanceAutoConfiguration.eurekaInstanceConfigBean();
		assertEquals(IP + ":" + INSTANCE_ID, eurekaInstanceConfigBean.getInstanceId());
		assertEquals(IP, eurekaInstanceConfigBean.getHostname());
		assertEquals(PORT, eurekaInstanceConfigBean.getNonSecurePort());
		assertFalse(eurekaInstanceConfigBean.getSecurePortEnabled());
		assertEquals(APPNAME, eurekaInstanceConfigBean.getVirtualHostName());
		assertEquals(APPNAME, eurekaInstanceConfigBean.getSecureVirtualHostName());
	}

	@Test
	public void testDirectRegistrationSanitisesVirtualHostNames() {
		eurekaInstanceAutoConfiguration.setRegistrationMethod(DIRECT_REGISTRATION_METHOD);
		eurekaInstanceAutoConfiguration.setAppname(APPNAME_INVALID_AS_HOSTNAME);
		EurekaInstanceConfigBean eurekaInstanceConfigBean = eurekaInstanceAutoConfiguration.eurekaInstanceConfigBean();
		assertEquals(SANITISED_APPNAME_INVALID_AS_HOSTNAME, eurekaInstanceConfigBean.getVirtualHostName());
		assertEquals(SANITISED_APPNAME_INVALID_AS_HOSTNAME, eurekaInstanceConfigBean.getSecureVirtualHostName());
	}
}
