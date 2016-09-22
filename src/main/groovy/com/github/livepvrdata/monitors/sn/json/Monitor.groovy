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
abstract class Monitor  extends EventMonitor {
	static final String FEED_URL = 'http://www.sportsnet.ca/wp-content/themes/sportsnet/zones/ajax-scoreboard.php'
	static private final String DATE_FMT = 'EEE MMM dd hh:mm aa zzz yyyy'
	static private final String DATE_FMT_1 = 'EEE MMM dd yyyy hh:mm aa zzz'	
	static private final String DATE_FMT_2 = 'EEE MMM dd, yyyy hh:mm aa zzz'		
	static private final TimeZone FEED_TZ = TimeZone.getTimeZone('America/New_York')

	static final Event[] parseFeed(String league, String data) throws IOException {
		def events = []
		def year = new Date().format('yyyy', FEED_TZ)
		def json = null
		try {		
			json = new JsonSlurper().parseText(data).data."$league"
		} catch(Throwable t) {
			String msg = "Unable to find league " + league
			throw new IOException(msg)
		}
		try {

			def states = json.keySet().findAll { json[it] instanceof Collection }
			states.each { state -> 
				json."$state".each {
					def teams
					def startTime
					if (league == 'epl'){
						teams = [it.visiting_team, it.home_team] as String[]				
						startTime = Date.parse(DATE_FMT, "$it.date $it.time $year")											
					} else if (league == 'wcoh'){
						teams = [it.visiting_team_short, it.home_team_short] as String[]									
						startTime = Date.parse(DATE_FMT_1, "$it.date $it.time $year")											
					} else if (league == 'nhl'){
						teams = [it.visiting_team_city, it.home_team_city] as String[]					
						startTime = Date.parse(DATE_FMT_2, "$it.date $it.time")																	
					} else {
						teams = [it.visiting_team_city, it.home_team_city] as String[]	
						startTime = Date.parse(DATE_FMT, "$it.date $it.time $year")																	
					}										
					def status
					if (state == 'Pre-Game')
						status = "${startTime.format('MMM d H:mm', FEED_TZ)} ET"
					else if(state == 'In-Progress' && (it.period_status) )
						status = "$it.period_status"						
					else
						status = it.game_status
								
					events << new Event(teams, status.toLowerCase().matches('.*(?:final|postponed|cancell?ed).*'), status, startTime.time)
				}
			}
		} catch (Throwable t) {
			// typically no games found on Sportsnet offseason
			events = null
		}
		events
	}
	
	static final Event[] getFeed(String league, String date) throws IOException {

        String key = (date) ?
                String.format("snjson_%s_%s", league, date) :
                String.format("snjson_%s", league)		
		def events = AppRuntime.instance.statusCache.get(key)
		if(events == null) {
			try {
				def  data  = AppRuntime.instance.statusCache.get("sn_all")
				if (data == null) {
					data = new URL(FEED_URL).text
					if (data != null)
					{
						def element1 = new Element("sn_all", data)
						element1.timeToLive = 120
						AppRuntime.instance.statusCache.put(element1)
					}
				} else {
					data = data.objectValue
				}
				
				def parsedData = parseFeed(league, data)
				if (parsedData != null) {
					long today = (new Date()).getTime() + 1800000L
					def shortWait = parsedData.any {it.startDate < today  && it.status != 'Final' }
					def element = new Element(key, parsedData)
					if (shortWait)
						element.timeToLive = 120
					else
						element.timeToLive = 1800					
					AppRuntime.instance.statusCache.put(element)
				}
				events = parsedData				
			} catch(Throwable t) {
				events = new Event[0]
				log.error 'Error feching game data', t
			}
		} else {
			//println events.hitCount
			events = events.objectValue	
				
		}
	}
	
	public static Event[] getEventsForDate(String league, long date) throws IOException {
		def matchFmt = 'yyyyMMdd'
        String today = new Date().format(matchFmt, FEED_TZ)
        String dateStr = new Date(date).format(matchFmt, FEED_TZ)
		
		getFeed(league, dateStr == today ? null : dateStr ).findAll {
			dateStr == new Date(it.startDate).format(matchFmt, FEED_TZ)
		}
	}

	private String desc
	private String[] teams
	private long date
	protected  String league
	

	Monitor(String desc, long date, String sport, String league) {
		this.desc = desc
		this.date = date
		this.league = league
	}

	Monitor(String[] teams, long date, String sport, String league) {
		this.teams = teams
		this.date = date
		this.league = league		
	}
		
	abstract String getTitle()	
	
	@Override
	public StatusResponse execute() throws IOException {
		def searchTerms = !teams ? EventMonitor.generateSearchTerms(this.desc) : EventMonitor.findAlternatives(teams)
		println searchTerms
		def events = getEventsForDate(league, date)
		def e = events.size() == 1 ? events[0] : events.find { it.isParticipating(searchTerms as String[]) }
		if(e)
			new StatusResponse(getNormalizedTitle(), desc, e.status, true, !e.isComplete(), e.isComplete())
		else
			throw new IOException("Unable to find event data! [$desc]")
	}
}
