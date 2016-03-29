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

import com.github.livepvrdata.common.data.req.StatusRequest
import com.github.livepvrdata.monitors.EventMonitor
import com.github.livepvrdata.monitors.espn.json.CBBM
import com.github.livepvrdata.monitors.espn.json.CFB
import com.github.livepvrdata.monitors.espn.json.NBA
import com.github.livepvrdata.monitors.espn.json.NFL
import com.github.livepvrdata.monitors.espn.json.NHL
import com.github.livepvrdata.monitors.mlb.EventMonitorMLB


class MonitorFactory {

	static EventMonitor fetch(StatusRequest req) {
		def monitor = null
		switch(req.type) {
			case ~/MLB Baseball|\d{4} (?:World Series|MLB All-Star Game)/:
				monitor = new EventMonitorMLB(req.details, 1000L * req.start)
				break
			case ~/Hockey LNH|NHL Hockey|\d{4} Stanley Cup Final/:
				monitor = new NHL(req.details, 1000L * req.start)
				break
			case ~/NBA Basketball|\d{4} NBA Finals/:
				monitor = new NBA(req.details, 1000L * req.start)
				break
			case ~/College Baseketball|\d{4} NCAA Basketball Tournament/:
				monitor = new CBBM(req.details, 1000L * req.start)
				break
			case ~/College Football|College Football Playoff National Championship/:
				monitor = new CFB(req.details, 1000L * req.start)
				break
			case ~/NFL Football/:
				monitor = new NFL(req.details, 1000L * req.start)
				break
		}
		monitor
	}
	
	static List fetchAll() {
		[
			CBBM,
			CFB,
			EventMonitorMLB,
			NBA,
			NFL,
			NHL
		]
	}
}
