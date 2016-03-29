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
package com.github.livepvrdata.web

import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.github.livepvrdata.MonitorFactory

@WebServlet(urlPatterns=['/events.jsp', '/events.groovy', '/events.html'])
class EventsServlet extends HttpServlet {

	@Override
	void doGet(HttpServletRequest req, HttpServletResponse resp) {
		def date = req.getParameter('date')
		if(!date || !date.matches('\\d{8}'))
			date = System.currentTimeMillis()
		else
			date = Date.parse('yyyyMMdd', date).time
		resp.contentType = 'text/plain'
		resp.outputStream.withStream { out ->
			MonitorFactory.fetchAll().each {
				def title = it.normalizedTitle.toUpperCase()
				def sb = new StringBuilder()
				(1..title.size()).each { sb << '=' }
				out << String.format('\t%s\n\t%s\n', title, sb)
				def events = it.getEventsForDate(date)
				if(events.size()) {
					it.getEventsForDate(date).each {
						out << String.format('\t%-50s\t%-19s\n', it.description, it.status)
					}
				} else {
					out << '\tNo games for this date.\n'
				}
				out << '\n'
			}
		}
	}
}
