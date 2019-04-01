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
* <p>
*/ 

package org.olat.core.commons.editor.fileeditor;

import org.olat.core.commons.controllers.linkchooser.CustomLinkTreeModel;
import org.olat.core.commons.editor.htmleditor.HTMLEditorConfig;
import org.olat.core.commons.editor.htmleditor.HTMLEditorController;
import org.olat.core.commons.editor.htmleditor.HTMLReadOnlyController;
import org.olat.core.commons.editor.htmleditor.WysiwygFactory;
import org.olat.core.commons.editor.plaintexteditor.TextEditorController;
import org.olat.core.commons.services.vfs.VFSLeafEditor.Mode;
import org.olat.core.commons.services.vfs.VFSLeafEditorConfigs;
import org.olat.core.commons.services.vfs.VFSLeafEditorConfigs.Config;
import org.olat.core.commons.services.vfs.VFSLeafEditorSecurityCallback;
import org.olat.core.commons.services.vfs.ui.version.VersionCommentController;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.controller.BlankController;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.gui.control.generic.modal.DialogBoxController;
import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.core.util.vfs.VFSLockApplicationType;
import org.olat.core.util.vfs.VFSLockManager;
import org.springframework.beans.factory.annotation.Autowired;

public class FileEditorController extends BasicController {

	private static final OLog log = Tracing.createLoggerFor(FileEditorController.class);

	private VFSLeaf vfsLeaf;
	private Controller editCtrl;
	private DialogBoxController lockedFiledCtr;

	private VersionCommentController unlockCtr;
	private CloseableModalController unlockDialogBox;
	
	@Autowired
	private VFSLockManager vfsLockManager;
	
	protected FileEditorController(UserRequest ureq, WindowControl wControl, VFSLeaf vfsLeaf,
			VFSLeafEditorSecurityCallback secCallback, VFSLeafEditorConfigs configs) {
		super(ureq, wControl);
		this.vfsLeaf = vfsLeaf;
		
		//TODO uh lock, move i18n from modules/bc/
//		if(vfsLockManager.isLockedForMe(vfsLeaf, ureq.getIdentity(), ureq.getUserSession().getRoles())) {
//			List<String> lockedFiles = Collections.singletonList(vfsLeaf.getName());
//			String msg = FolderCommandHelper.renderLockedMessageAsHtml(getTranslator(), lockedFiles);
//			List<String> buttonLabels = Collections.singletonList(getTranslator().translate("ok"));
//			lockedFiledCtr = activateGenericDialog(ureq, getTranslator().translate("lock.title"), msg, buttonLabels, lockedFiledCtr);
//			return null;
//		}
		

		
		// launch plaintext or html editor depending on file type
		boolean isEdit = Mode.EDIT.equals(secCallback.getMode());
		if (vfsLeaf.getName().endsWith(".html") || vfsLeaf.getName().endsWith(".htm")) {
			Config configObj = configs.getConfig(HTMLEditorConfig.TYPE);
			HTMLEditorConfig config = null;
			if (!(configObj instanceof HTMLEditorConfig)) {
				log.error("FileEditor started without configuration! Displayd blank page. File: " + vfsLeaf + ", Identity: "
					+ getIdentity());
				editCtrl = new BlankController(ureq, wControl);
			}
			config = (HTMLEditorConfig) configObj;
			if (isEdit) {
				HTMLEditorController htmlCtrl;
				CustomLinkTreeModel customLinkTreeModel = config.getCustomLinkTreeModel();
				if (customLinkTreeModel != null) {
					htmlCtrl = WysiwygFactory.createWysiwygControllerWithInternalLink(ureq, getWindowControl(),
							config.getVfsContainer(), config.getFilePath(), true, customLinkTreeModel,
							config.getEdusharingProvider());
				} else {
					htmlCtrl = WysiwygFactory.createWysiwygController(ureq, getWindowControl(), config.getVfsContainer(),
							config.getFilePath(), config.getMediaPath(), true, true, config.getEdusharingProvider());
				}
				
				htmlCtrl.setNewFile(false);
				htmlCtrl.getRichTextConfiguration().setAllowCustomMediaFactory(config.isAllowCustomMediaFactory());
				if (config.isDisableMedia()) {
					htmlCtrl.getRichTextConfiguration().disableMedia();
				}
				
				editCtrl = htmlCtrl;
			} else {
				editCtrl = new HTMLReadOnlyController(ureq, getWindowControl(), vfsLeaf.getParentContainer(), vfsLeaf.getName(), secCallback.canClose());
			}
		}
		else {
			editCtrl = new TextEditorController(ureq, getWindowControl(), vfsLeaf, "utf-8", !isEdit);
		}
		listenTo(editCtrl);
		
		VelocityContainer mainVC = createVelocityContainer("file_editor");
		mainVC.put("editor", editCtrl.getInitialComponent());
		putInitialPanel(mainVC);
	}

	@Override
	public void event(UserRequest ureq, Component source, Event event) {
		// nothing to do here
	}

	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if (source == editCtrl) {
			if (event == Event.DONE_EVENT) {
				boolean lock = vfsLockManager.isLocked(vfsLeaf, VFSLockApplicationType.vfs, null);
				if(lock) {
					unlockCtr = new VersionCommentController(ureq,getWindowControl(), true, false);
					listenTo(unlockCtr);
					unlockDialogBox = new CloseableModalController(getWindowControl(), translate("ok"), unlockCtr.getInitialComponent());
					unlockDialogBox.activate();
				} else {
					fireEvent(ureq, Event.DONE_EVENT);
				}
			} else {
				fireEvent(ureq, Event.CANCELLED_EVENT);
			}
		} else if (source == lockedFiledCtr) {
			fireEvent(ureq, Event.CANCELLED_EVENT);
		} else if (source == unlockCtr) {
			if(!unlockCtr.keepLocked()) {
				vfsLockManager.unlock(vfsLeaf, getIdentity(), ureq.getUserSession().getRoles(), VFSLockApplicationType.vfs);
			}
			cleanUpUnlockDialog();
			fireEvent(ureq, Event.DONE_EVENT);
		}
	}

	private void cleanUpUnlockDialog() {
		if(unlockDialogBox != null) {
			unlockDialogBox.deactivate();
			removeAsListenerAndDispose(unlockCtr);
			unlockDialogBox = null;
			unlockCtr = null;
		}
	}
	
	@Override
	protected void doDispose() {
		removeAsListenerAndDispose(editCtrl);
		editCtrl = null;
	}
}