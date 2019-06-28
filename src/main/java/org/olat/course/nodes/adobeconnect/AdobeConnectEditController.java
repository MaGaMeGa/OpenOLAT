/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.course.nodes.adobeconnect;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.tabbedpane.TabbedPane;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.ControllerEventListener;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.tabbable.ActivateableTabbableDefaultController;
import org.olat.course.ICourse;
import org.olat.course.assessment.AssessmentHelper;
import org.olat.course.condition.Condition;
import org.olat.course.condition.ConditionEditController;
import org.olat.course.editor.NodeEditController;
import org.olat.course.nodes.AdobeConnectCourseNode;
import org.olat.course.run.userview.UserCourseEnvironment;

/**
 * 
 * Initial date: 1 mars 2019<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AdobeConnectEditController extends ActivateableTabbableDefaultController implements ControllerEventListener {
	
	public static final String GUEST_ACCESS_ALLOWED = "guestAccessAllowed";
	public static final String MODERATOR_START_MEETING = "moderatorStartMeeting";
	
	private static final String PANE_TAB_ACCESSIBILITY = "pane.tab.accessibility";
	public static final String PANE_TAB_VCCONFIG = "pane.tab.vcconfig";
	private static final String[] paneKeys = { PANE_TAB_VCCONFIG, PANE_TAB_ACCESSIBILITY };
	
	private TabbedPane tabPane;
	
	private ConditionEditController accessibilityCondContr;
	
	private final AdobeConnectCourseNode courseNode;
	
	public AdobeConnectEditController(UserRequest ureq, WindowControl wControl, AdobeConnectCourseNode courseNode,
			ICourse course, UserCourseEnvironment userCourseEnv) {
		super(ureq, wControl);
		this.courseNode = courseNode;
		
		String providerId = courseNode.getModuleConfiguration().getStringValue("vc_provider_id");
		if("wimba".equals(providerId)) {
			showWarning("wimba.not.supported.message");
		}
		
		Condition accessCondition = courseNode.getPreConditionAccess();
		accessibilityCondContr = new ConditionEditController(ureq, wControl, userCourseEnv,
				accessCondition, AssessmentHelper.getAssessableNodes(course.getEditorTreeModel(), courseNode));
		listenTo(accessibilityCondContr);
	}
	
	@Override
	public String[] getPaneKeys() {
		return paneKeys;
	}

	@Override
	public TabbedPane getTabbedPane() {
		return tabPane;
	}

	@Override
	protected void doDispose() {
		//
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		//
	}
	
	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if (source == accessibilityCondContr) {
			if (event == Event.CHANGED_EVENT) {
				Condition cond = accessibilityCondContr.getCondition();
				courseNode.setPreConditionAccess(cond);
				fireEvent(ureq, NodeEditController.NODECONFIG_CHANGED_EVENT);
			}
		}
	}
	
	@Override
	public void addTabs(TabbedPane tabbedPane) {
		tabPane = tabbedPane;
		tabbedPane.addTab(translate(PANE_TAB_ACCESSIBILITY),
				accessibilityCondContr.getWrappedDefaultAccessConditionVC(translate("condition.accessibility.title")));
	}
}