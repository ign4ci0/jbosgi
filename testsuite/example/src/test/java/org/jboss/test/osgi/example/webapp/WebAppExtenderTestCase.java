/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.osgi.example.webapp;

//$Id:$

import static org.junit.Assert.assertEquals;

import org.jboss.osgi.spi.capability.HttpServiceCapability;
import org.jboss.osgi.testing.OSGiRuntime;
import org.jboss.osgi.testing.OSGiTestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A test that deployes a WAR bundle 
 * 
 * Due to the nature of asynchronous event processing by the
 * extender pattern, we cannot assume that the endpoint is
 * available immediately
 * 
 * @author thomas.diesler@jboss.com
 * @since 06-Oct-2009
 */
public class WebAppExtenderTestCase extends AbstractWebAppTestCase
{
   private static OSGiRuntime runtime;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      OSGiTestHelper osgiTestHelper = new OSGiTestHelper();

      runtime = osgiTestHelper.getDefaultRuntime();
      runtime.addCapability(new HttpServiceCapability());
      
      if (runtime.getBundle("org.ops4j.pax.web.pax-web-extender-war", null) == null)
         runtime.installBundle("bundles/pax-web-extender-war.jar").start();
      
      runtime.installBundle("example-webapp.war").start();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      runtime.shutdown();
      runtime = null;
   }

   @Test
   public void testResourceAccess() throws Exception
   {
      String line = getHttpResponse("/message.txt", 5000);
      assertEquals("Hello from Resource", line);
   }

   @Test
   public void testServletAccess() throws Exception
   {
      String line = getHttpResponse("/servlet?test=plain", 5000);
      assertEquals("Hello from Servlet", line);
   }

   @Test
   public void testServletInitProps() throws Exception
   {
      String line = getHttpResponse("/servlet?test=initProp", 5000);
      assertEquals("initProp=SomeValue", line);
   }
}