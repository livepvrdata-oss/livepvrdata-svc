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
package com.github.livepvrdata.dao;

import java.util.Set;

final public class EventParticipantMap {

	private String name;
	private Set<String> alternatives;
		
	public EventParticipantMap(String name, Set<String> alts) {
		if(name == null || name.length() == 0)
			throw new IllegalArgumentException("Map key cannot be null nor zero-length!");
		this.name = name;
		this.alternatives = alts;
	}
	
	public String getName() { return name; }
	public Set<String> getAlternatives() { return alternatives; }
	public int getNumberOfAlternatives() { return alternatives.size(); }
}