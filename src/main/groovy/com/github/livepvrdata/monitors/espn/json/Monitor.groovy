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

import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import net.sf.ehcache.Element

import com.github.livepvrdata.AppRuntime
import com.github.livepvrdata.common.data.Event
import com.github.livepvrdata.common.data.resp.StatusResponse
import com.github.livepvrdata.monitors.EventMonitor

@Log4j
abstract class Monitor extends EventMonitor {
	static final String FEED_URL = 'http://site.api.espn.com/apis/v2/scoreboard/header?sport=%s&league=%s'
	static private final String DATE_FMT = 'yyyy-MM-dd\'T\'HH:mm:ssX'
	static private final TimeZone FEED_TZ = TimeZone.getTimeZone('America/New_York')
	
	static final Event[] parseFeed(String data) throws IOException {
		def json = new JsonSlurper().parseText(data)
		json.sports[0].leagues[0].events.collect {
			def startTime = Date.parse(DATE_FMT, it.date)
			def status
			if(it.status == 'pre')
				status = "${startTime.format('MMM d H:mm', FEED_TZ)} ET"
			else
				status = it.summary			
			def teams = it.competitors.collect { it.name } as String[]
			def lowerStatus = status.toLowerCase()
			new Event(teams, lowerStatus == 'ft' || lowerStatus.matches('.*(?:final|postponed|cancell?ed|full time|suspended).*'), status, startTime.time)
		}
	}

	static final Event[] getFeed(String sport, String league) throws IOException {
		String key = String.format("espnjson_%s_%s", sport, league)
		def events = AppRuntime.instance.statusCache.get(key)
		if(events == null) {
			try {
				def data = new URL(String.format(FEED_URL, sport, league)).text
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
		events
	}

	static public Event[] getEventsForDate(String sport, String league, long date) throws IOException {
		def matchFmt = 'yyyyMMdd'
		getFeed(sport, league).findAll {
			new Date(date).format(matchFmt, FEED_TZ) == new Date(it.startDate).format(matchFmt, FEED_TZ)
		}
	}
	
	private String desc
	private String[] teams
	private long date
	private String sport
	protected String league
	
	Monitor(String desc, long date, String sport, String league) {
		this.desc = desc
		this.sport = sport
		this.league = league
		this.date = date
	}
	
	Monitor(String[] teams, long date, String sport, String league) {
		this.teams = teams
		this.sport = sport
		this.league = league
		this.date = date
	}

	@Override
	public StatusResponse execute() throws IOException {
		def searchTerms = !teams ? EventMonitor.generateSearchTerms(desc) : EventMonitor.findAlternatives(teams)
		def events = getEventsForDate(sport, league, date) 
		def e = events.size() == 1 ? events[0] : events.find { it.isParticipating(searchTerms as String[]) }
		if(e)
			new StatusResponse(title, desc, e.status, true, !e.isComplete(), e.isComplete())
		else
			throw new IOException("Unable to find event data! [$desc]")
	}
	
	abstract String getTitle()
}
