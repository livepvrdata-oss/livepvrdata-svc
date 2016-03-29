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
package com.github.livepvrdata

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

import com.github.livepvrdata.web.EventsServlet
import com.github.livepvrdata.web.StatusServlet


final class StandaloneServer {
	static private Server SERVER
	static private volatile boolean shutdownStarted = false
	
	static private void stopServer() {
		if(!shutdownStarted && SERVER?.isRunning()) {
			shutdownStarted = true
			println 'Shutting down embedded Jetty...'
			new Timer(true).runAfter(3000) {
				SERVER.stop()
			}
		}
	}
	
	static private void startServer(int port, String contextPath, def baseResource) {
		SERVER = new Server(port)
		
		ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS)
		sch.contextPath = contextPath
		sch.displayName = 'livepvrdata-svc'
		if(!baseResource) {
			def base = StandaloneServer.class.protectionDomain.codeSource.location.toExternalForm()
			if(base.endsWith('.jar'))
				sch.resourceBase = "jar:${base}!/"
			else
				sch.resourceBase = base
		} else
			sch.resourceBase = new File(baseResource).toURI().toString()
		
		SERVER.handler = sch
		
		ServletHolder holder = new ServletHolder(StatusServlet)
		sch.addServlet(holder, '/query')
		
		holder = new ServletHolder(EventsServlet)
		sch.addServlet(holder, '/events.jsp')
		
//		holder = new ServletHolder(GroovyServlet)
//		sch.addServlet(holder, '*.groovy')
//		
//		holder = new ServletHolder(TemplateServlet)
//		sch.addServlet(holder, '*.html')
//		
//		holder = new ServletHolder(DefaultServlet)
//		sch.addServlet(holder, '/')
		
		SERVER.start()
	}
}
