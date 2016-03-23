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

import java.util.Arrays;

public class StatusRequest implements Request {

	private String type;
	private String details;
	private String[] teams;
	private long start;
	
	@SuppressWarnings("unused")
	private StatusRequest() {}
	
	/**
	 * @param type
	 * @param teams
	 * @param start
	 */
	public StatusRequest(String type, String[] teams, long start) {
		this.type = type;
		this.teams = teams;
		this.start = start;
	}
	/**
	 * @param type
	 * @param details
	 * @param start
	 */
	public StatusRequest(String type, String details, long start) {
		this.type = type;
		this.details = details;
		this.start = start;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}
	/**
	 * @return the teams
	 */
	public String[] getTeams() {
		return teams;
	}
	/**
	 * @return the start
	 */
	public long getStart() {
		return start;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StatusRequest [type=");
		builder.append(type);
		builder.append(", details=");
		builder.append(details);
		builder.append(", teams=");
		builder.append(Arrays.toString(teams));
		builder.append(", start=");
		builder.append(start);
		builder.append("]");
		return builder.toString();
	}
}
