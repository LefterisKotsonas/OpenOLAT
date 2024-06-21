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
package org.olat.course.duedate.model;

import java.util.Date;

import org.olat.course.duedate.DueDateConfig;

/**
 * 
 * Initial date: 5 Nov 2021<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class DueDateConfigImpl implements DueDateConfig {
	
	private int numOfDays;
	private String relativeToType;
	private Date absoluteDate;
	private Date absoluteStartDate;
	
	@Override
	public int getNumOfDays() {
		return numOfDays;
	}
	
	public void setNumOfDays(int numOfDays) {
		this.numOfDays = numOfDays;
	}
	
	@Override
	public String getRelativeToType() {
		return relativeToType;
	}
	
	public void setRelativeToType(String relativeToType) {
		this.relativeToType = relativeToType;
	}
	
	@Override
	public Date getAbsoluteDate() {
		return absoluteDate;
	}
	
	public void setAbsolutDate(Date absoluteDate) {
		this.absoluteDate = absoluteDate;
	}

	@Override
	public Date getAbsoluteStartDate() {
		return absoluteStartDate;
	}

	public void setAbsoluteStartDate(Date date) {
		this.absoluteStartDate = date;
	}
}
