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
package com.github.livepvrdata.monitors.mlb

import groovy.json.JsonSlurper
import groovy.util.logging.Log4j

import java.text.SimpleDateFormat

import net.sf.ehcache.Element

import com.github.livepvrdata.AppRuntime
import com.github.livepvrdata.common.data.Event
import com.github.livepvrdata.common.data.resp.StatusResponse
import com.github.livepvrdata.monitors.EventMonitor


@Log4j
class EventMonitorMLB extends EventMonitor {
	static private final TimeZone FEED_TIME_ZONE = TimeZone.getTimeZone('America/New_York')
	static private final String FEED_URL = 'http://mlb.mlb.com/gdcross/components/game/mlb/year_%s/month_%s/day_%s/master_scoreboard.json'
	static final String getNormalizedTitle() { return 'MLB Baseball' }
	
	private final String desc
	private final String[] teams
	private final Date date
	
	EventMonitorMLB(String desc, long date) {
		this.desc = desc
		this.date = new Date(date)
	}
	
	EventMonitorMLB(String[] teams, long date) {
		this.teams = teams
		this.date = new Date(date)
	}

	@Override
	StatusResponse execute() throws IOException {
		def events = getFeed(date)
		if(events.size() > 1) {
			def terms = desc ? EventMonitor.generateSearchTerms(desc) : EventMonitor.findAlternatives(teams)
			events = events.findAll { it.isParticipating(terms as String[]) } 
		}
		if(events.size() > 1)
			events = events.min { Math.abs(date.time - it.startDate) }
		else if(events.size() == 1)
			events = events[0]
		else
			events = null
		if(events)
			return new StatusResponse(getNormalizedTitle(), desc, events.status, true, !events.complete, events.complete)
		throw new IOException("Unable to find event data! [$desc]")
	}
	
	static final String currentFeedDate() {
		SimpleDateFormat fmt = new SimpleDateFormat(EventMonitor.DATE_FMT)
		fmt.setTimeZone(FEED_TIME_ZONE)
		return fmt.format(new Date())
	}
	
	static Event[] getEventsForDate(long date) throws IOException {
		return getFeed(new Date(date));
	}
	
	static final Event[] getFeed(Date date) throws IOException {
		String dateStr = date.format('yyyy|MM|dd', FEED_TIME_ZONE)
		String key = "mlbcom_$dateStr"
		def events = AppRuntime.instance.statusCache.get(key)
		if(events == null) {
			def input = dateStr.split('\\|')
			try {
				def data = new URL(String.format(FEED_URL, input[0], input[1], input[2])).text
				def parsedData = parseFeed(data)
				def element = new Element(key, parsedData)
				if(date.format('yyyyMMdd', FEED_TIME_ZONE).toInteger() > new Date().format('yyyyMMdd', FEED_TIME_ZONE).toInteger())
					element.timeToLive = (int)((date.time - new Date().time - 3600000) / 1000)
				else
					element.timeToLive = 120
				AppRuntime.instance.statusCache.put(element)
				events = parsedData
			} catch(Throwable t) {
				events = new Event[0]
				log.error 'Error feching game data', t
			}
		} else
			events = events.objectValue
		events
	}
	
	static private final Event[] parseFeed(String data) throws IOException {
		def games = []
		try {
			if(data) {
				def json = new JsonSlurper().parseText(data)
				if(json.data.games.game instanceof Map)
					json.data.games.game = [json.data.games.game]
				json.data.games.game.each {
					def tz = it.time_zone_hm_lg.toInteger()
					def pad = tz < 0 ? 3 : 2
					def start = "${it.time_date_hm_lg} ${it.hm_lg_ampm} ${String.format("%0${pad}d", tz)}00"
					def e = new Event(["${it.away_team_city}", "${it.home_team_city}"] as String[], it.status.status.toUpperCase().matches('.*(?:FINAL|POSTPONED|CANCEL{1,2}ED|SUSPENDED|GAME OVER|COMPLETED).*'), it.status.status, Date.parse('yyyy/MM/dd h:mm a Z', start).time)
					games << e
				}
			}
			return games
		} catch(Exception e) {
			throw new IOException(e)
		}
	}
}
