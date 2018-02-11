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


class CBBM extends Monitor {
	static private final String SPORT = 'basketball'
	static private final String LEAGUE = 'mens-college-basketball'
    private static final String CONFERENCES_URL = "http://site.api.espn.com/apis/site/v2/sports/$SPORT/$LEAGUE/scoreboard/conferences?groups=50"

	static String getNormalizedTitle() { 'College Basketball' }
	static Event[] getEventsForDate(long date) throws IOException {	Monitor.getEventsForDate(SPORT, LEAGUE, date, fetchGroupIds()) }

    public static String fetchGroupIds() {
        String key = String.format("espnjson_%s_%s_groups_v2", SPORT, LEAGUE)
        def groups = AppRuntime.instance.statusCache.get(key)

        if(groups == null) {
            String data = new URL(CONFERENCES_URL).text
            def json = new JsonSlurper().parseText(data)

            String groupData = json.conferences.findResults {
                //Don't include "sub-groups", only groups with no parent
                //  The ESPN feed doesn't return the status appropriately when sub-groups
                //  and their parents are both in the request
                it.parentGroupId  ?  null : it.groupId
            }.join(",")

            def element = new Element(key, groupData)
            element.timeToLive = 3600 * 24 * 7 //one week
            AppRuntime.instance.statusCache.put(element)

            return groupData
        } else {
            return groups.objectValue
        }
    }

	CBBM(String desc, long date) {
		super(desc, date, SPORT, LEAGUE)
	}

	CBBM(String[] teams, long date) {
		super(teams, date, SPORT, LEAGUE)
	}

	@Override
	String getTitle() { getNormalizedTitle() }

    @Override
    public String getGroupIds() {
        fetchGroupIds()
    }
}
