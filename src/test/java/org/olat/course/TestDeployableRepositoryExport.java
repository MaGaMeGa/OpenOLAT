/**
* OLAT - Online Learning and Training<br>
* http://www.olat.org
* <p>
* Licensed under the Apache License, Version 2.0 (the "License"); <br>
* you may not use this file except in compliance with the License.<br>
* You may obtain a copy of the License at
* <p>
* http://www.apache.org/licenses/LICENSE-2.0
* <p>
* Unless required by applicable law or agreed to in writing,<br>
* software distributed under the License is distributed on an "AS IS" BASIS, <br>
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
* See the License for the specific language governing permissions and <br>
* limitations under the License.
* <p>
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.
*/
package org.olat.course;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * 
 * Description:<br>
 * Testing the url file download
 * 
 * <P>
 * Initial Date:  22.12.2010 <br>
 * @author guido
 */
@ContextConfiguration(locations={"classpath:org/olat/course/_spring/textContextDeployableRepoExport.xml"})
public class TestDeployableRepositoryExport extends AbstractJUnit4SpringContextTests {
	
	@Test
	public void testZipDownloadNormalCase() {
		DeployableCourseExport bean = (DeployableCourseExport) applicationContext.getBean("normalzip");
		assertNotNull(bean);
		assertEquals(bean.getAccess(),4);
		assertEquals(bean.getVersion(),Float.valueOf(1));
		
		File file = bean.getDeployableCourseZipFile();
		assertEquals(file.getName(),"OpenOLAT-Help-8.1.zip");
		assertNotNull(file);
		assertTrue(file.exists());
	}
	
	@Test
	public void testZipDownloadBadUrl() {
		DeployableCourseExport bean = (DeployableCourseExport) applicationContext.getBean("badurl");
		assertNotNull(bean);
		assertNull(bean.getDeployableCourseZipFile());
	}
	
	@Test
	public void testZipDownloadTextFile() {
		DeployableCourseExport bean = (DeployableCourseExport) applicationContext.getBean("textfile");
		assertNotNull(bean);
		assertNull(bean.getDeployableCourseZipFile());
	}

}
