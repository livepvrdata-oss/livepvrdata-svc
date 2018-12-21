package com.github.livepvrdata.monitors.espn.json;

import com.github.livepvrdata.AppRuntime
import com.github.livepvrdata.common.data.Event

import groovy.json.JsonSlurper
import net.sf.ehcache.Element

public final class EspnMonitorUtils {

  private static final String CONFERENCES_URL = "http://site.api.espn.com/apis/site/v2/sports/%s/%s/scoreboard/conferences%s"

  private EspnMonitorUtils() {}

  public static String fetchGroupIds(String sport, String league, String urlExtra = "") {
    String conferencesUrl = String.format(CONFERENCES_URL, sport, league, urlExtra)
    String key = String.format("espnjson_%s_%s_groups_v2", sport, league)
    def groups = AppRuntime.instance.statusCache.get(key)

    if(groups == null) {
        String data = new URL(conferencesUrl).text
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
}
