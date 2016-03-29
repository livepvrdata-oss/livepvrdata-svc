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

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.SimpleLayout

/*
 * This class is used to configure log4j logging to console when deployed via container.
 * 
 * We know we're in a container when this listener executes because this listener isn't
 * configured in the driver version of the app
 */
@WebListener
class AppInit implements ServletContextListener {

	static private boolean done = false
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		synchronized(AppInit) {
			if(!done) {
				done = true
				def log = Logger.rootLogger
				log.removeAllAppenders()
				log.addAppender(new ConsoleAppender(new SimpleLayout()))
				log.level = Level.toLevel(System.getenv('LOG_LEVEL'), Level.WARN)
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
