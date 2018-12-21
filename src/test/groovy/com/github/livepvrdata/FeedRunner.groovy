package com.github.livepvrdata;

import com.github.livepvrdata.common.data.req.StatusRequest
import com.github.livepvrdata.common.data.resp.StatusResponse
import com.github.livepvrdata.monitors.EventMonitor

public class FeedRunner {

    public static void main(String[] args) throws IOException {

        //System.out.println(Arrays.toString(F1.getEventsForDate(System.currentTimeMillis())));

        long date = System.currentTimeMillis()
        //long date = new Date(116, 8, 25).getTime();

        MonitorFactory.fetchAll().each {
            def title = it.normalizedTitle.toUpperCase()
            System.out << String.format('\t%s\n\t%s\n', title, '=' * title.size())
            def events = it.getEventsForDate(date)
            if(events.size()) {
                events.each {
                    System.out << String.format('\t%-50s\t%-19s\n', it.description, it.status)
                }
            } else {
                System.out << '\tNo games for this date.\n'
            }
            System.out << '\n'
        }

        System.exit(0)
    }

}
