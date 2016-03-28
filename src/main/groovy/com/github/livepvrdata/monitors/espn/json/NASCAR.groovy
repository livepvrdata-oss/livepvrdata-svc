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
import com.github.livepvrdata.common.data.resp.StatusResponse


class NASCAR extends MotorSportMonitor {
	static private final String SPORT = 'racing'
	static private final String LEAGUE = 'sprint'
	static String getNormalizedTitle() { 'NASCAR Racing' }
	static Event[] getEventsForDate(long date) throws IOException {
		def events = []
		['sprint', 'xfinity', 'truck'].each {
			events.addAll(MotorSportMonitor.getEventsForDate(SPORT, it, date))
		}
		events
	}
	
	private String series
	
	NASCAR(String desc, long date) {
		super(desc, date, SPORT, LEAGUE)
		int i = desc.indexOf(':')
		this.series = i > 0 ? desc.substring(0, i) : 'Sprint Cup Series'
	}

	NASCAR(String[] teams, long date) {
		super(teams, date, SPORT, LEAGUE)
		int i = teams[0].indexOf(':')
		this.series = i > 0 ? teams[0].substring(0, i) : 'Sprint Cup Series'
	}

	@Override
	StatusResponse execute() throws IOException {
		def series = this.series
		switch(series) {
			case 'XFINITY Series': series = 'xfinity'; break
			case ~/.*Truck.*/: series = 'truck'; break
			default: series = 'sprint'
		}
		league = series
		super.execute()
	}
	
	@Override
	String getTitle() { getNormalizedTitle() }
}
