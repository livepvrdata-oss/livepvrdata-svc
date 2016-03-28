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
package com.github.livepvrdata.monitors.espn.json

import com.github.livepvrdata.common.data.Event

class NBA extends Monitor {
	static private final String SPORT = 'basketball'
	static private final String LEAGUE = 'nba'
	static String getNormalizedTitle() { 'NBA Basketball' }
	static Event[] getEventsForDate(long date) throws IOException {	Monitor.getEventsForDate(SPORT, LEAGUE, date) }
	
	NBA(String desc, long date) {
		super(desc, date, SPORT, LEAGUE)
	}

	NBA(String[] teams, long date) {
		super(teams, date, SPORT, LEAGUE)
	}

	@Override
	String getTitle() { getNormalizedTitle() }
}
