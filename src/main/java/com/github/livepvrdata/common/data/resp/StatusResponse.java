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
package com.github.livepvrdata.common.data.resp;


/**
 * @author dbattams
 *
 */
final public class StatusResponse extends Response {
	private String title;
	private String description;
	private String status;
	private boolean isValid;
	private boolean isActive;
	private boolean isComplete;
	
	/**
	 * @param title
	 * @param description
	 * @param callsign
	 * @param status
	 * @param isValid
	 * @param isActive
	 * @param isComplete
	 * @param end
	 */
	public StatusResponse(String title, String description, String status, boolean isValid, boolean isActive,
			boolean isComplete) {
		super(false);
		this.title = title;
		this.description = description;
		this.status = status;
		this.isValid = isValid;
		this.isActive = isActive;
		this.isComplete = isComplete;
	}

	private StatusResponse() { super(false); } // Needed for Gson construction
	
	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @return the status
	 */
	public final String getStatus() {
		return status;
	}

	/**
	 * @return the isValid
	 */
	public final boolean isValid() {
		return isValid;
	}

	/**
	 * @return the isActive
	 */
	public final boolean isActive() {
		return isActive;
	}

	/**
	 * @return the isComplete
	 */
	public final boolean isComplete() {
		return isComplete;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StatusResponse [title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", status=");
		builder.append(status);
		builder.append(", isValid=");
		builder.append(isValid);
		builder.append(", isActive=");
		builder.append(isActive);
		builder.append(", isComplete=");
		builder.append(isComplete);
		builder.append("]");
		return builder.toString();
	}
}
