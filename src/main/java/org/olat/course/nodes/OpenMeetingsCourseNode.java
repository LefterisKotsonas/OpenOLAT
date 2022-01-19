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
 * 12.10.2011 by frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.course.nodes;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.stack.BreadcrumbPanel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.tabbable.TabbableController;
import org.olat.core.id.OLATResourceable;
import org.olat.core.logging.Tracing;
import org.olat.core.util.Util;
import org.olat.core.util.resource.OresHelper;
import org.olat.course.ICourse;
import org.olat.course.condition.ConditionEditController;
import org.olat.course.editor.ConditionAccessEditConfig;
import org.olat.course.editor.CourseEditorEnv;
import org.olat.course.editor.NodeEditController;
import org.olat.course.editor.StatusDescription;
import org.olat.course.groupsandrights.CourseGroupManager;
import org.olat.course.nodes.cp.CPEditController;
import org.olat.course.nodes.openmeetings.OpenMeetingsEditController;
import org.olat.course.nodes.openmeetings.OpenMeetingsPeekViewController;
import org.olat.course.run.navigation.NodeRunConstructionResult;
import org.olat.course.run.userview.CourseNodeSecurityCallback;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.course.run.userview.VisibilityFilter;
import org.olat.modules.openmeetings.manager.OpenMeetingsException;
import org.olat.modules.openmeetings.manager.OpenMeetingsManager;
import org.olat.modules.openmeetings.ui.OpenMeetingsRoomEditController;
import org.olat.modules.openmeetings.ui.OpenMeetingsRunController;
import org.olat.repository.RepositoryEntry;

/**
 * 
 * Initial date: 06.11.2012<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class OpenMeetingsCourseNode extends AbstractAccessableCourseNode {

	private static final long serialVersionUID = 8680935159748506305L;
	private static final Logger log = Tracing.createLoggerFor(OpenMeetingsCourseNode.class);

	public static final String TYPE = "openmeetings";

	// configuration
	public static final String CONF_VC_CONFIGURATION = "vc_configuration";

	private transient CourseGroupManager groupMgr;

	public OpenMeetingsCourseNode() {
		super(TYPE);
	}
	
	@Override
	protected String getDefaultTitleOption() {
		// default is to only display content because the room has its own room title
		return CourseNode.DISPLAY_OPTS_CONTENT;
	}

	@Override
	public TabbableController createEditController(UserRequest ureq, WindowControl wControl, BreadcrumbPanel stackPanel, ICourse course,
			UserCourseEnvironment userCourseEnv) {
		CourseNode chosenNode = course.getEditorTreeModel().getCourseNode(userCourseEnv.getCourseEditorEnv().getCurrentCourseNodeId());
		// create edit controller
		OpenMeetingsEditController childTabCntrllr = new OpenMeetingsEditController(ureq, wControl, this, course);
		
		NodeEditController nodeEditCtr = new NodeEditController(ureq, wControl, stackPanel, course, chosenNode,
				userCourseEnv, childTabCntrllr);
		nodeEditCtr.addControllerListener(childTabCntrllr);
		return nodeEditCtr;
	}

	@Override
	public ConditionAccessEditConfig getAccessEditConfig() {
		return ConditionAccessEditConfig.regular(false);
	}

	@Override
	public NodeRunConstructionResult createNodeRunConstructionResult(UserRequest ureq, WindowControl wControl,
			UserCourseEnvironment userCourseEnv, CourseNodeSecurityCallback nodeSecCallback, String nodecmd, VisibilityFilter visibilityFilter) {
		// check if user is moderator of the virtual classroom
		boolean admin = userCourseEnv.isAdmin();
		boolean moderator = admin || userCourseEnv.isCoach();

		// create run controller
		OLATResourceable ores = OresHelper.clone(userCourseEnv.getCourseEnvironment().getCourseGroupManager().getCourseResource());
		Controller runCtr = new OpenMeetingsRunController(ureq, wControl, null, ores, getIdent(), admin, moderator, userCourseEnv.isCourseReadOnly());
		Controller controller = TitledWrapperHelper.getWrapper(ureq, wControl, runCtr, userCourseEnv, this, "o_openmeetings_icon");
		return new NodeRunConstructionResult(controller);
	}

	@Override
	public Controller createPeekViewRunController(UserRequest ureq, WindowControl wControl, UserCourseEnvironment userCourseEnv, CourseNodeSecurityCallback nodeSecCallback, boolean small) {
		return new OpenMeetingsPeekViewController(ureq, wControl);
	}

	@Override
	public StatusDescription[] isConfigValid(CourseEditorEnv cev) {
		String translatorStr = Util.getPackageName(ConditionEditController.class);
		if (groupMgr == null) {
			groupMgr = cev.getCourseGroupManager();
		}
		List<StatusDescription> statusDescs = isConfigValidWithTranslator(cev, translatorStr, getConditionExpressions());
		return StatusDescriptionHelper.sort(statusDescs);
	}

	@Override
	public RepositoryEntry getReferencedRepositoryEntry() {
		return null;
	}

	@Override
	public StatusDescription isConfigValid() {
		if (oneClickStatusCache != null) { return oneClickStatusCache[0]; }
		
		StatusDescription sd = StatusDescription.NOERROR;
		if(groupMgr != null) {
			OpenMeetingsManager openMeetingsManager = CoreSpringFactory.getImpl(OpenMeetingsManager.class);
			Long roomId = openMeetingsManager.getRoomId(null, groupMgr.getCourseResource(), getIdent());
			if(roomId == null) {
				String shortKey = "error.noroom.short";
				String longKey = "error.noroom.long";
				String[] params = new String[] { getShortTitle() };
				String translPackage = Util.getPackageName(OpenMeetingsRoomEditController.class);
				sd = new StatusDescription(StatusDescription.ERROR, shortKey, longKey, params, translPackage);
				sd.setDescriptionForUnit(getIdent());
				// set which pane is affected by error
				sd.setActivateableViewIdentifier(CPEditController.PANE_TAB_CPCONFIG);
			}
		}
		return sd;
	}
	
	@Override
	public boolean needsReferenceToARepositoryEntry() {
		return false;
	}
	
	@Override
	public void cleanupOnDelete(ICourse course) {
		super.cleanupOnDelete(course);
		// load configuration
		OpenMeetingsManager provider = CoreSpringFactory.getImpl(OpenMeetingsManager.class);
		// remove meeting
		OLATResourceable ores = OresHelper.createOLATResourceableInstance(course.getResourceableTypeName(), course.getResourceableId());
		try {
			provider.deleteAll(null, ores, getIdent());
		} catch (OpenMeetingsException e) {
			log.error("A room could not be deleted for course node: " + getIdent() + " of course:" + course, e);
		}
	}
}