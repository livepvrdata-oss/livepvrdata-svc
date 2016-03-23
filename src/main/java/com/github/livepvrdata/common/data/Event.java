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
package com.github.livepvrdata.common.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;


public final class Event implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String[] participants;
	private String status;
	private boolean isComplete;
	private long startDate;
	
	@SuppressWarnings("unused")
	private Event() {} // For serialization
	
	public Event(String[] participants, boolean isComplete, String status, long startDate) {
		this.participants = participants;
		this.isComplete = isComplete;
		this.status = status;
		this.startDate = startDate;
	}
	
	public long getStartDate() { return startDate; }
	public boolean isComplete() { return isComplete; }
	public String[] getParticipants() { return participants; }
	public String getStatus() { return status; }
	public boolean isParticipating(String name) {
		for(String p : participants)
			if(p.equals(name))
				return true;
		return false;
	}
	public boolean isParticipating(String[] names) {
		int matches = 0;
		for(String name : names)
			if(isParticipating(name) && ++matches == 2)
				break;
		return matches == 2;
	}
	public String getDescription() { return participants.length > 1 ? participants[0] + " vs. " + participants[1] : participants[0]; }
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Event [participants=");
		builder.append(Arrays.toString(participants));
		builder.append(", status=");
		builder.append(status);
		builder.append(", isComplete=");
		builder.append(isComplete);
		builder.append(", startDate=");
		builder.append(new Date(startDate));
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(participants);
		result = prime * result + (int) (startDate ^ (startDate >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (!Arrays.equals(participants, other.participants))
			return false;
		if (startDate != other.startDate)
			return false;
		return true;
	}
}
