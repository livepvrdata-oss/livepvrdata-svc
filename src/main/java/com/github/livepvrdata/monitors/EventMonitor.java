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
package com.github.livepvrdata.monitors;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.github.livepvrdata.DataStore;
import com.github.livepvrdata.common.data.resp.StatusResponse;
import com.github.livepvrdata.dao.EventParticipantMap;

public abstract class EventMonitor {
	static private final Logger LOG = Logger.getLogger(EventMonitor.class);
	
	static public final int DEFAULT_TTL = 120;
	static public final String TEAM_REGEX_STR = "[A-Z][\\w\\s-&'\\.\\(\\)\\d]+";
	static public final String EVENT_REGEX_STR = "(" + TEAM_REGEX_STR + ") (?:at|vs\\.{0,1}|\u00E0|contre) (" + TEAM_REGEX_STR + ")";
	static public final String DATE_FMT = "yyyyMMdd";
	
	static public final String longToStr(long date, TimeZone tz) {
		SimpleDateFormat fmt = new SimpleDateFormat(DATE_FMT);
		fmt.setTimeZone(tz);
		return fmt.format(new Date(date));
	}

	static public List<String> generateSearchTerms(String desc) {
		List<String> result = new ArrayList<String>();
		Pattern p = Pattern.compile(EVENT_REGEX_STR);
		Matcher m = p.matcher(desc);
		if(m.find()) {
			result.add(m.group(1));
			result.add(m.group(2));
		}
		return findAlternatives(result.toArray(new String[result.size()]));
	}

	/**
	 * @param teams
	 * @return
	 */
	static public List<String> findAlternatives(String[] teams) {
		Set<String> allTeams = new HashSet<String>();
		allTeams.addAll(Arrays.asList(teams));
		for(int i = 0; i < teams.length; ++i) {
			String name = teams[i];
			EventParticipantMap alts = DataStore.getInstance().getAlternatives(name);			
			if(alts != null && alts.getNumberOfAlternatives() > 0) {
				for(String alt : alts.getAlternatives())
					allTeams.add(alt);
				LOG.warn("Found alternatives for '" + name + "' " + Arrays.toString(alts.getAlternatives().toArray()));
			} else
				LOG.warn("No alternatives found for '" + name + "'");
		}
		return new ArrayList<String>(allTeams);
	}	
	abstract public StatusResponse execute() throws IOException;
}