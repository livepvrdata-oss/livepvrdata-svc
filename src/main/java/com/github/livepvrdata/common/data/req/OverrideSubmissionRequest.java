/*
 Copyright 2016 Battams, Derek
 
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
 
		http://www.apache.org/licenses/LICENSE-2.0
 
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.github.livepvrdata.common.data.req;

import java.util.HashSet;
import java.util.Set;

public class OverrideSubmissionRequest implements Request {
	
	static private Set<String> createSet(String s) {
		Set<String> set = new HashSet<String>();
		set.add(s);
		return set;
	}
	
	private String epgName;
	private Set<String> feedNames;
	
	@SuppressWarnings("unused")
	private OverrideSubmissionRequest() {}

	public OverrideSubmissionRequest(String epgName, Set<String> feedNames) {
		this.epgName = epgName;
		this.feedNames = feedNames;
	}
	
	public OverrideSubmissionRequest(String epgName, String feedName) {	
		this(epgName, createSet(feedName));
	}
	
	/**
	 * @return the epgName
	 */
	public String getEpgName() {
		return epgName;
	}

	/**
	 * @return the feedNames
	 */
	public Set<String> getFeedNames() {
		return feedNames;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OverrideSubmissionRequest [epgName=");
		builder.append(epgName);
		builder.append(", feedNames=");
		builder.append(feedNames);
		builder.append("]");
		return builder.toString();
	}
}
