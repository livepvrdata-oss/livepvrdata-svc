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

import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.SimpleLayout


class Driver {
	static {
		System.setErr(new PrintStream(new FileOutputStream(new File('livepvrdata-svc.stderr.log'))))
		def log = Logger.rootLogger
		log.removeAllAppenders()
		log.addAppender(new ConsoleAppender(new SimpleLayout()))
		log.level = Level.DEBUG
	}
	
	static main(args) {
		def opts = parseCmdLine(args)
		if(!opts.h) {
			StandaloneServer.startServer(opts.p ? opts.p.toInteger() : 8080, opts.c ?: '/', opts.r)
			Runtime.runtime.addShutdownHook {
				StandaloneServer.stopServer()
			}
		}
	}
	
	static private def parseCmdLine(def args) {
		def cli = new CliBuilder(usage: 'livepvrdata-svc [options]')
		cli.p(args: 1, argName: 'port', 'Port number the embedded Jetty server will listen on. [8080]')
		cli.c(args: 1, argName: 'context', 'Context path to deploy app at. [<root> i.e. /]')
		cli.r(args: 1, argName: 'resource_base', 'Base directory used for resource discovery (usually only changed for testing purposes).')
		cli.h('Display this help and exit.')
		def opts = cli.parse(args)
		if(opts.h)
			cli.usage()
		return opts
	}
}
