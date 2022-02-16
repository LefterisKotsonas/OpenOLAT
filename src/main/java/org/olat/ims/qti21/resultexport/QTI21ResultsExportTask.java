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
package org.olat.ims.qti21.resultexport;

import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.olat.basesecurity.BaseSecurity;
import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.services.export.AbstractExportTask;
import org.olat.core.commons.services.export.ExportManager;
import org.olat.core.commons.services.taskexecutor.LongRunnable;
import org.olat.core.commons.services.taskexecutor.TaskExecutorManager;
import org.olat.core.commons.services.taskexecutor.TaskStatus;
import org.olat.core.commons.services.taskexecutor.manager.PersistentTaskProgressCallback;
import org.olat.core.commons.services.vfs.VFSRepositoryService;
import org.olat.core.gui.util.WindowControlMocker;
import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.logging.Tracing;
import org.olat.core.util.StringHelper;
import org.olat.core.util.resource.OresHelper;
import org.olat.core.util.vfs.VFSContainer;
import org.olat.core.util.vfs.VFSLeaf;
import org.olat.course.CourseFactory;
import org.olat.course.ICourse;
import org.olat.course.nodes.CourseNode;
import org.olat.course.nodes.QTICourseNode;

/**
 * 
 * Initial date: 1 févr. 2022<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class QTI21ResultsExportTask extends AbstractExportTask implements LongRunnable {

	private static final long serialVersionUID = -432616692413096169L;
	private static final Logger log = Tracing.createLoggerFor(QTI21ResultsExportTask.class);
	
	private transient QTI21ResultsExport export;
	private transient VFSLeaf exportZip;
	
	private boolean withPdfs;
	private boolean withNonParticipants;
	
	private String title;
	private String filename;
	private String description;
	private List<Long> identitiesKeys;
	
	private Locale locale;
	private String courseNodeIdent;
	private OLATResourceable courseRes;
	
	public QTI21ResultsExportTask(OLATResourceable courseRes, CourseNode courseNode,
			List<Identity> identities, String title, String description, String filename,
			boolean withNonParticipants, boolean withPdfs, Locale locale) {
		this.courseRes = OresHelper.clone(courseRes);
		this.courseNodeIdent = courseNode.getIdent();
		identitiesKeys = identities.stream()
				.map(Identity::getKey)
				.collect(Collectors.toList());
		this.withNonParticipants = withNonParticipants;
		this.withPdfs = withPdfs;
		this.title = title;
		this.locale = locale;
		this.filename = filename;
		this.description = description;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public VFSLeaf getExportZip() {
		return exportZip;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public void run() {
		log.info("Export results: {}", title);//TODO export
		
		ICourse course = CourseFactory.loadCourse(courseRes.getResourceableId());
		QTICourseNode courseNode = (QTICourseNode)course.getRunStructure().getNode(courseNodeIdent);

		VFSRepositoryService vfsRepositoryService = CoreSpringFactory.getImpl(VFSRepositoryService.class);
		ExportManager exportManager = CoreSpringFactory.getImpl(ExportManager.class);
		VFSContainer subFolder = exportManager
				.getExportContainer(course.getCourseEnvironment().getCourseGroupManager().getCourseEntry(), courseNodeIdent);
		
		BaseSecurity securityManager = CoreSpringFactory.getImpl(BaseSecurity.class);
		List<Identity> identities = securityManager.loadIdentityByKeys(identitiesKeys);
		
		TaskExecutorManager taskExecutorManager = CoreSpringFactory.getImpl(TaskExecutorManager.class);
		PersistentTaskProgressCallback progress = new PersistentTaskProgressCallback(task, taskExecutorManager);
		if(!StringHelper.containsNonWhitespace(task.getCheckpoint())) {
			progress.setProgress(0.0d, null);
		}
		
		export = new QTI21ResultsExport(course.getCourseEnvironment(),
				identities, withNonParticipants, withPdfs, courseNode, "", locale,
				task.getCreator(), new WindowControlMocker());
		
		String vfsName = filename + "_" + task.getKey() + ".zip";
		exportZip = subFolder.createChildLeaf(vfsName);
		if(exportZip == null) {
			exportZip = (VFSLeaf)subFolder.resolve(vfsName);
		}
		
		fillMetadata(exportZip, title, description);

		try(OutputStream out=exportZip.getOutputStream(true);
				ZipOutputStream zout = new ZipOutputStream(out)) {
			if(StringHelper.containsNonWhitespace(task.getCheckpoint())) {
				export.setStartPoint(task.getCheckpoint());
			}
			export.exportExcelResults(zout);
			export.exportTestResults(zout, progress);
		} catch(Exception e) {
			log.error("", e);
		}
		
		// Reload the metadata
		if(!export.isCancelled()) {
			vfsRepositoryService.getMetadataFor(exportZip);
		} else {
			TaskStatus status = taskExecutorManager.getStatus(task);
			if(status == TaskStatus.cancelled) {
				exportZip.deleteSilently();
			}
		}
	}
}
