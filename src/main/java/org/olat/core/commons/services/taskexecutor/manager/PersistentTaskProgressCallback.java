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
package org.olat.core.commons.services.taskexecutor.manager;

import org.apache.logging.log4j.Logger;
import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.services.taskexecutor.Task;
import org.olat.core.commons.services.taskexecutor.TaskExecutorManager;
import org.olat.core.logging.Tracing;

/**
 * A default implementation which 
 * Initial date: 15 févr. 2022<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class PersistentTaskProgressCallback extends DefaultTaskProgressCallback {
	
	private static final Logger log = Tracing.createLoggerFor(PersistentTaskProgressCallback.class);
	
	private final Task task;
	private final TaskExecutorManager taskExecutorManager;
	
	public PersistentTaskProgressCallback(Task task) {
		this.task = task;
		taskExecutorManager = CoreSpringFactory.getImpl(TaskExecutorManager.class);
	}
	
	public PersistentTaskProgressCallback(Task task, TaskExecutorManager taskExecutorManager) {
		this.task = task;
		this.taskExecutorManager = taskExecutorManager;
	}

	@Override
	public void setProgress(double progress, String checkpoint) {
		log.info("Progress: {} ({})", progress, checkpoint);//TODO export
		taskExecutorManager.updateProgress(task, Double.valueOf(progress), checkpoint);
	}
}
