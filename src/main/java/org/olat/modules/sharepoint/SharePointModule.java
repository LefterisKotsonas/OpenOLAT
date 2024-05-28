/**
 * <a href="https://www.openolat.org">
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
 * frentix GmbH, https://www.frentix.com
 * <p>
 */
package org.olat.modules.sharepoint;

import java.util.ArrayList;
import java.util.List;

import org.olat.core.configuration.AbstractSpringModule;
import org.olat.core.configuration.ConfigOnOff;
import org.olat.core.util.StringHelper;
import org.olat.core.util.coordinate.CoordinatorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 24 mai 2024<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service
public class SharePointModule extends AbstractSpringModule implements ConfigOnOff {
	
	private static final String PROP_ENABLED = "sharepoint.enabled";
	private static final String PROP_EXCLUDE_SITES_AND_DRIVES = "sharepoint.exclude.sites.drives";
	private static final String PROP_EXCLUDE_LABELS = "sharepoint.exclude.labels";
	
	@Value("${sharepoint.enabled:false}")
	private boolean enabled;
	
	@Value("${sharepoint.exclude.sites.drives}")
	private String excludeSitesAndDrives;
	@Value("${sharepoint.exclude.labels}")
	private String excludeLabels;
	
	@Autowired
	public SharePointModule(CoordinatorManager coordinatorManager) {
		super(coordinatorManager);
	}
	
	@Override
	public void init() {
		String enabledObj = getStringPropertyValue(PROP_ENABLED, true);
		if(StringHelper.containsNonWhitespace(enabledObj)) {
			enabled = "true".equals(enabledObj);
		}
		
		excludeSitesAndDrives = getStringPropertyValue(PROP_EXCLUDE_SITES_AND_DRIVES, excludeSitesAndDrives);
		excludeLabels = getStringPropertyValue(PROP_EXCLUDE_LABELS, excludeLabels);
	}

	@Override
	protected void initFromChangedProperties() {
		init();
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		setBooleanProperty(PROP_ENABLED, enabled, true);
	}

	public List<String> getExcludeSitesAndDrives() {
		return toList(excludeSitesAndDrives);
	}

	public void setExcludeSitesAndDrives(List<String> exclusionList) {
		setExcludeSitesAndDrives(toString(exclusionList));
	}

	public void setExcludeSitesAndDrives(String exclusionList) {
		this.excludeSitesAndDrives = exclusionList;
		setStringProperty(PROP_EXCLUDE_SITES_AND_DRIVES, exclusionList, true);
	}

	public List<String> getExcludeLabels() {
		return toList(excludeLabels);
	}

	public void setExcludeLabels(List<String> exclusionList) {
		setExcludeLabels(toString(exclusionList));
	}

	public void setExcludeLabels(String exclusionList) {
		this.excludeLabels = exclusionList;
		setStringProperty(PROP_EXCLUDE_LABELS, exclusionList, true);
	}
	
	private String toString(List<String> list) {
		if(list == null || list.isEmpty()) return "";
		return String.join("|", list);
	}
	
	private List<String> toList(String val) {
		if(StringHelper.containsNonWhitespace(val)) {
			String[] array = val.split("[\n\r|]");
			List<String> list = new ArrayList<>();
			for(String string:array) {
				if(StringHelper.containsNonWhitespace(string)) {
					list.add(string);
				}
			}
			return list;
		}
		return List.of();
	}

}
