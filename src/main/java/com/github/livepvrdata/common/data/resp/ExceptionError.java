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
package com.github.livepvrdata.common.data.resp;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionError extends Response implements ErrorResponse {

	private String message;
	private String stackTrace;
	
	private ExceptionError() { super(true); }
	public ExceptionError(String msg, Throwable t) {
		super(true);
		if(msg == null || msg.length() == 0)
			message = "[No message provided]";
		else
			message = msg;
		if(t != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			stackTrace = sw.toString();
		} else
			stackTrace = null;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getStrackTrace() {
		return stackTrace;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExceptionError [message=");
		builder.append(message);
		builder.append(", stackTrace=");
		builder.append(stackTrace);
		builder.append("]");
		return builder.toString();
	}	
}