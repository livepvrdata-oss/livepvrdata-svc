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
package com.github.livepvrdata.monitors.sn.json

import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import net.sf.ehcache.Element

import com.github.livepvrdata.AppRuntime
import com.github.livepvrdata.common.data.Event
import com.github.livepvrdata.common.data.resp.StatusResponse
import com.github.livepvrdata.monitors.EventMonitor

@Log4j
class CFL extends EventMonitor {
	static final String FEED_URL = 'http://www.sportsnet.ca/wp-content/themes/sportsnet/zones/ajax-scoreboard.php'
	static private final String DATE_FMT = 'EEE MMM dd hh:mm aa zzz yyyy'
	static private final TimeZone FEED_TZ = TimeZone.getTimeZone('America/New_York')
	
	static final Event[] parseFeed(String data) throws IOException {
		def events = []
		def year = new Date().format('yyyy', FEED_TZ)
		def json = new JsonSlurper().parseText(data).data.cfl
		def states = json.keySet().findAll { json[it] instanceof Collection }
		states.each { state ->
			json."$state".each {
				def startTime = Date.parse(DATE_FMT, "$it.date $it.time $year")
				def status
				if(state == 'Pre-Game')
					status = "${startTime.format('MMM d H:mm', FEED_TZ)} ET"
				else if(it.clock)
					status = "$it.clock ($it.period)"
				else
					status = it.game_status
				def teams = [it.visiting_team_city, it.home_team_city] as String[]
				events << new Event(teams, status.toLowerCase().matches('.*(?:final|postponed|cancell?ed).*'), status, startTime.time)
			}
		}
		events
	}

	static final Event[] getFeed() throws IOException {
		String key = 'sn_cfl'
		def events = AppRuntime.instance.statusCache.get(key)
		if(events == null) {
			try {
				def data = new URL(FEED_URL).text
				def parsedData = parseFeed(data)
				def element = new Element(key, parsedData)
				element.timeToLive = 120
				AppRuntime.instance.statusCache.put(element)
				events = parsedData
			} catch(Throwable t) {
				events = new Event[0]
				log.error 'Error feching game data', t
			}
		} else
			events = events.objectValue
	}

	static public Event[] getEventsForDate(long date) throws IOException {
		def matchFmt = 'yyyyMMdd'
		getFeed().findAll {
			new Date(date).format(matchFmt, FEED_TZ) == new Date(it.startDate).format(matchFmt, FEED_TZ)
		}
	}
	
	static final String getNormalizedTitle() { 'CFL Football' }
	
	private String desc
	private String[] teams
	private long date
	
	CFL(String desc, long date) {
		this.desc = desc
		this.date = date
	}
	
	CFL(String[] teams, long date) {
		this.teams = teams
		this.date = date
	}

	@Override
	public StatusResponse execute() throws IOException {
		def searchTerms = !teams ? EventMonitor.generateSearchTerms(desc) : EventMonitor.findAlternatives(teams)
		def events = getEventsForDate(date) 
		def e = events.size() == 1 ? events[0] : events.find { it.isParticipating(searchTerms as String[]) }
		if(e)
			new StatusResponse(getNormalizedTitle(), desc, e.status, true, !e.isComplete(), e.isComplete())
		else
			throw new IOException("Unable to find event data! [$desc]")
	}
}
