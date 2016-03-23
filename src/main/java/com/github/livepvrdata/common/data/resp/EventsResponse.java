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

import java.util.Arrays;

import com.github.livepvrdata.common.data.Event;

public final class EventsResponse extends Response {
	
	private Event[] events;

	private EventsResponse() { super(false); }
	
	public EventsResponse(Event[] events) { 
		super(false);
		this.events = events;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventsResponse [events=");
		builder.append(Arrays.toString(events));
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the events
	 */
	public Event[] getEvents() {
		return events;
	}
}