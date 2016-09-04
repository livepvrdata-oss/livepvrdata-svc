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

import com.github.livepvrdata.AppRuntime
import com.github.livepvrdata.common.data.Event

import groovy.json.JsonSlurper
import net.sf.ehcache.Element

class CFB extends Monitor {
    private static final String SPORT = 'football'
    private static final String LEAGUE = 'college-football'
    private static final String CONFERENCES_URL = "http://site.api.espn.com/apis/site/v2/sports/$SPORT/$LEAGUE/scoreboard/conferences";

    static String getNormalizedTitle() { 'College Football' }
    static Event[] getEventsForDate(long date) throws IOException {	Monitor.getEventsForDate(SPORT, LEAGUE, date, fetchGroupIds()) }

    public static String fetchGroupIds() {
        String key = String.format("espnjson_%s_%s_groups", SPORT, LEAGUE)
        def groups = AppRuntime.instance.statusCache.get(key)

        if(groups == null) {
            String data = new URL(CONFERENCES_URL).text
            def json = new JsonSlurper().parseText(data)

            String groupData = json.conferences.collect {
                it.groupId
            }.join(",")

            def element = new Element(key, groupData)
            element.timeToLive = 3600 * 24 * 7 //one week
            AppRuntime.instance.statusCache.put(element)

            return groupData
        } else {
            return groups.objectValue
        }
    }

	CFB(String desc, long date) {
		super(desc, date, SPORT, LEAGUE)
	}

	CFB(String[] teams, long date) {
		super(teams, date, SPORT, LEAGUE)
	}

	@Override
	String getTitle() { getNormalizedTitle() }

    @Override
    public String getGroupIds() {
        fetchGroupIds()
    }
}
