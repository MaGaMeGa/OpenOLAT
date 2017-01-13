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
package org.olat.course.nodes.pf.ui;

import java.util.Date;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.IntegerElement;
import org.olat.core.gui.components.form.flexible.elements.SelectionElement;
import org.olat.core.gui.components.form.flexible.elements.SpacerElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.form.flexible.impl.elements.JSDateChooser;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.course.nodes.PFCourseNode;
/**
*
* @author Fabian Kiefer, fabian.kiefer@frentix.com, http://www.frentix.com
*
*/
public class PFEditFormController extends FormBasicController {
	
	private SelectionElement studentDropBox, teacherDropBox, alterFiles, limitFileCount, timeFrame;
	private IntegerElement fileCount;
	private JSDateChooser dateStart, dateEnd;
	private SpacerElement spacerEl;
	
	private PFCourseNode pfNode;

	public PFEditFormController(UserRequest ureq, WindowControl wControl, PFCourseNode pfNode) {
		super(ureq, wControl);
		this.pfNode = pfNode;

		initForm(ureq);
	}
	
	
	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {

		studentDropBox = uifactory.addCheckboxesHorizontal("participant.drop", formLayout, new String[]{"xx"}, new String[]{null});
		studentDropBox.addActionListener(FormEvent.ONCLICK);
				
		String[] alterfile = new String[]{ translate("alter.file") };
		alterFiles = uifactory.addCheckboxesHorizontal("alter.file", "blank.label", formLayout, new String[]{"xx"}, alterfile);
		alterFiles.addActionListener(FormEvent.ONCLICK);
		
		String[] timeframe = new String[]{ translate("time.frame") };
		timeFrame = uifactory.addCheckboxesHorizontal("time.frame", "blank.label", formLayout, new String[]{"xx"}, timeframe);
		timeFrame.addActionListener(FormEvent.ONCLICK);
		timeFrame.showLabel(Boolean.FALSE);
			
		dateStart = new JSDateChooser("dateStart", getLocale());
		dateStart.setLabel("date.start", null);
		dateStart.setDateChooserTimeEnabled(true);
		dateStart.setValidDateCheck("valid.date");
		dateStart.setMandatory(true);
		formLayout.add(dateStart);
		
		dateEnd = new JSDateChooser("dateEnd", getLocale());
		dateEnd.setLabel("date.end", null);
		dateEnd.setDateChooserTimeEnabled(true);
		dateEnd.setValidDateCheck("valid.date");
		dateEnd.setMandatory(true);
		formLayout.add(dateEnd);
		
		String[] limitcount = new String[]{ translate("limit.count") };
		limitFileCount = uifactory.addCheckboxesHorizontal("limit.count", "blank.label", formLayout, new String[]{"xx"}, limitcount);
		limitFileCount.addActionListener(FormEvent.ONCLICK);
		limitFileCount.showLabel(Boolean.FALSE);
		fileCount = uifactory.addIntegerElement("file.count", 3, formLayout);
		fileCount.showLabel(Boolean.FALSE);
		fileCount.setHelpTextKey("limit.count.coach.info", null);

		spacerEl = uifactory.addSpacerElement("spacer1", formLayout, false);
		
		teacherDropBox = uifactory.addCheckboxesHorizontal("coach.drop", formLayout, new String[]{"xx"}, new String[]{null});
		
		// Create submit button
		final FormLayoutContainer buttonLayout = FormLayoutContainer.createButtonLayout("buttonLayout", getTranslator());
		formLayout.add(buttonLayout);
		uifactory.addFormSubmitButton("save", buttonLayout)
			.setElementCssClass("o_sel_node_editor_submit");
		
		applyModuleConfig();
		
		
	}
	
	private void applyModuleConfig () {
		boolean hasStudentBox = pfNode.hasParticipantBoxConfigured();
		studentDropBox.select("xx", hasStudentBox);
		alterFiles.select("xx", pfNode.hasAlterFileConfigured());
		alterFiles.setVisible(hasStudentBox);
		spacerEl.setVisible(hasStudentBox);
		boolean hasLimitCount = pfNode.hasLimitCountConfigured();
		limitFileCount.select("xx", hasLimitCount);
		limitFileCount.setVisible(hasStudentBox);
		fileCount.setIntValue(pfNode.getLimitCount());
		fileCount.setVisible(hasLimitCount);
		boolean hasTimeFrame = pfNode.hasDropboxTimeFrameConfigured();
		timeFrame.select("xx", hasTimeFrame);
		timeFrame.setVisible(hasStudentBox);
		dateStart.setDate(pfNode.getDateStart());
		dateStart.setVisible(hasTimeFrame);		
		dateEnd.setDate(pfNode.getDateEnd());
		dateEnd.setVisible(hasTimeFrame);
		teacherDropBox.select("xx", pfNode.hasCoachBoxConfigured());		
	}
	
	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		//this is to navigate through it all
		if (source == studentDropBox) {
			activateSettings();
		} else if (source == limitFileCount) {
			activateFileCount();
			if (limitFileCount.isSelected(0)) {
				showInfo("limit.count.coach.info");
			}
		} else if (source == timeFrame) {
			activateTimeFrame();
		}
	}
	
	private void activateSettings () {
		boolean studentDropBoxEnabled = studentDropBox.isSelected(0);
		boolean fileCountEnabled = limitFileCount.isSelected(0);
		boolean timeFrameEnabled = timeFrame.isSelected(0);
		alterFiles.setVisible(studentDropBoxEnabled);
		limitFileCount.setVisible(studentDropBoxEnabled);
		timeFrame.setVisible(studentDropBoxEnabled);
		fileCount.setVisible(studentDropBoxEnabled && fileCountEnabled);
		dateStart.setVisible(studentDropBoxEnabled && timeFrameEnabled);
		dateEnd.setVisible(studentDropBoxEnabled && timeFrameEnabled);
	}
	
	private void activateFileCount () {
		boolean fileCountEnabled = limitFileCount.isSelected(0);
		fileCount.setVisible(fileCountEnabled);
	}
	
	private void activateTimeFrame () {
		boolean timeFrameEnabled = timeFrame.isSelected(0);
		dateStart.setVisible(timeFrameEnabled);
		dateEnd.setVisible(timeFrameEnabled);
	}
	
	private boolean checkTimeFrameValid () {
		if (timeFrame.isSelected(0)){
			return dateStart.getDate().before(dateEnd.getDate()) && dateEnd.getDate().after(new Date());							
		}
		return true;
	}
	
	

	@Override
	protected boolean validateFormLogic(UserRequest ureq) {
		boolean allOk = true;
		dateEnd.clearError();
		dateStart.clearError();
		
		if(timeFrame.isSelected(0)) {
			if(dateEnd.getDate() == null) {
				dateEnd.setErrorKey("form.legende.mandatory", null);
				allOk &= false;
			}
			if(dateStart.getDate() == null) {
				dateStart.setErrorKey("form.legende.mandatory", null);
				allOk &= false;
			}
		}
		return allOk & super.validateFormLogic(ureq);
	}


	@Override
	protected void formOK(UserRequest ureq) {
		if (!checkTimeFrameValid()) {
			dateEnd.setErrorKey("timeframe.error", null);
		} else if (1 > fileCount.getIntValue()) {
			fileCount.setErrorKey("filecount.error", null);
		} else if (!(studentDropBox.isSelected(0) || teacherDropBox.isSelected(0))) {
			showError("folderselection.error");
		} else {
			pfNode.updateModuleConfig(studentDropBox.isSelected(0), 
					teacherDropBox.isSelected(0), 
					alterFiles.isSelected(0), 
					limitFileCount.isSelected(0), 
					fileCount.getIntValue(),
					timeFrame.isSelected(0), 
					dateStart.getDate(), 
					dateEnd.getDate());
			fireEvent(ureq, Event.DONE_EVENT);
		}
	}

	@Override
	protected void doDispose() {

	}

}
