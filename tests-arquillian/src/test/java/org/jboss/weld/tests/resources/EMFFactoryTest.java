/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.tests.resources;

import java.net.URL;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.tests.category.Integration;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@Category(Integration.class)
@RunWith(Arquillian.class)
public class EMFFactoryTest {

    public static final Asset PERSISTENCE_XML = new ByteArrayAsset("<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" version=\"1.0\"><persistence-unit name=\"pu1\"><jta-data-source>java:jboss/datasources/ExampleDS</jta-data-source></persistence-unit></persistence>".getBytes());
    public static final Asset EMPTY_BEANS_XML = new ByteArrayAsset("<beans />".getBytes());

    @Deployment(testable = false)
    public static Archive<?> deploy() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(JPAResourceProducerSingletonEJB_StaticField.class, ProducedViaStaticFieldOnEJB.class, EMFConsumer1.class)
                .addClasses(JPAResourceProducerManagedBean_InstanceField.class, ProducedViaInstanceFieldOnManagedBean.class, EMFConsumer2.class)
                .addClasses(JPAResourceProducerManagedBean_StaticField.class, ProducedViaStaticFieldOnManagedBean.class, EMFConsumer3.class)
                .addAsResource(PERSISTENCE_XML, "META-INF/persistence.xml")
                .addAsWebInfResource(EMPTY_BEANS_XML, "beans.xml");
    }

    @ArquillianResource
    private URL url;

    /*
    * description = "WELD-632"
    */
    @Test
    public void testStaticEJBEMFProducerField() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);
        Page page = client.getPage(getPath("emfconsumer1"));

        assertEquals(200, page.getWebResponse().getStatusCode());
    }

    /*
    * description = "WELD-632"
    */
    @Test
    public void testInstanceManagedBeanEMFProducerField() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);
        Page page = client.getPage(getPath("emfconsumer2"));

        assertEquals(200, page.getWebResponse().getStatusCode());
    }

    /*
    * description = "WELD-632"
    */
    @Test
    public void testStaticManagedBeanEMFProducerField() throws Exception {
        WebClient client = new WebClient();
        client.setThrowExceptionOnFailingStatusCode(false);
        Page page = client.getPage(getPath("emfconsumer3"));

        assertEquals(200, page.getWebResponse().getStatusCode());
    }

    protected String getPath(String viewId) {
        return url + viewId;
    }
}
