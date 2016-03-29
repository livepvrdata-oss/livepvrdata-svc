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

import com.github.livepvrdata.AppRuntime
import com.github.livepvrdata.MonitorFactory
import com.github.livepvrdata.common.data.req.StatusRequest
import com.github.livepvrdata.common.data.resp.StatusResponse


@WebServlet(urlPatterns=['/query'])
class StatusServlet extends HttpServlet {

	@Override
	void doGet(HttpServletRequest req, HttpServletResponse resp) {
		resp.contentType = 'text/plain'
		def input = AppRuntime.instance.gson.fromJson(req.getParameter('q'), StatusRequest)
		resp.outputStream.withStream {
			it << AppRuntime.instance.gson.toJson(MonitorFactory.fetch(input)?.execute())
		}
	}
	
	@Override
	void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp)
	}
}
