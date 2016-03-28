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

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import com.github.livepvrdata.AppRuntime
import com.github.livepvrdata.common.data.req.StatusRequest
import com.github.livepvrdata.common.data.resp.StatusResponse
import com.github.livepvrdata.monitors.espn.json.NHL
import com.github.livepvrdata.monitors.mlb.EventMonitorMLB


class StatusServlet extends HttpServlet {

	static private StatusResponse findStatus(StatusRequest req) {
		def monitor = null
		switch(req.type) {
			case ~/MLB Baseball/:
				monitor = new EventMonitorMLB(req.details, 1000L * req.start)
				break
			case ~/NHL Hockey/:
				monitor = new NHL(req.details, 1000L * req.start)
				break
		}
		monitor?.execute()
	}
	
	@Override
	void doGet(HttpServletRequest req, HttpServletResponse resp) {
		resp.contentType = 'text/plain'
		def input = AppRuntime.instance.gson.fromJson(req.getParameter('q'), StatusRequest)
		resp.outputStream.withStream {
			it << AppRuntime.instance.gson.toJson(findStatus(input))
		}
	}
	
	@Override
	void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp)
	}
}
