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

import groovy.sql.Sql
import groovy.util.logging.Log4j

import java.sql.SQLException

import org.apache.commons.io.FilenameUtils

import com.github.livepvrdata.dao.EventParticipantMap;
import com.mchange.v2.c3p0.ComboPooledDataSource


@Log4j
class DataStore {
		
	static private DataStore INSTANCE = null
	synchronized static DataStore getInstance() {
		if(!INSTANCE)
			INSTANCE = new DataStore()
		INSTANCE
	}

	static File getAppRoot() { return new File(System.getProperty('livepvrdata-svc.root') ?: new File(new File(System.getProperty('user.home')), '.livepvrdata-svc').absolutePath) }
	static private final String DB_NAME = "${Boolean.parseBoolean(System.getProperty('livepvrdata-svc.testing')) ? 'memory:' : ''}/${FilenameUtils.separatorsToUnix(FilenameUtils.getPath("${getAppRoot().absolutePath}/throwaway"))}livepvrdata"
	static private final String JDBC_DRIVER_CLS = 'org.apache.derby.jdbc.EmbeddedDriver'
	static private final String JDBC_CONN_STR = "jdbc:derby:${DB_NAME}"

	static private final ComboPooledDataSource DATA_SRC = new ComboPooledDataSource()
	
	static {
		Runtime.runtime.addShutdownHook {
			try {
				DATA_SRC.close()
			} catch(Throwable t) {
				log.warn 'Error closing data source!', t
			}
		}
		
		def jdbcStr = "${JDBC_CONN_STR};create=true"
		log.info "Connecting to database: $jdbcStr"
		def sql = Sql.newInstance(jdbcStr, JDBC_DRIVER_CLS)
		try {
			if(!sql.connection.warnings) {
				createTables(sql)
				setDbVersion(sql)
				loadMaps(sql)
				log.info 'New database created'
			}
		} finally {
			try {
				sql.close()
			} catch(Throwable t) {
				log.warn 'SQLError creating database', t
			}
		}
		
		DATA_SRC.driverClass = JDBC_DRIVER_CLS
		DATA_SRC.jdbcUrl = JDBC_CONN_STR
		DATA_SRC.user = ''
		DATA_SRC.password = ''
	}
	
	static private void loadMaps(Sql sql) {
		['mlb', 'nhl', 'nba', 'nfl'].each {
			def is = DataStore.class.getResourceAsStream("/maps/${it}.map")
			is.withStream {
				it.eachLine {
					it = it.trim()
					if(it.size() && !it.startsWith('#')) {
						def data = it.split('=')
						def epg = data[0]
						def alts = data[1].split('\\|')
						sql.withTransaction {
							def keys = sql.executeInsert("INSERT INTO epg (name) VALUES ($epg)")
							alts.each {
								sql.executeInsert("INSERT INTO alts (id, name) VALUES (${keys[0][0]}, $it)")
							}
						}
					}
				}
			}
		}
	}
	
	static private void setDbVersion(Sql sql) {
		def qry = "INSERT INTO settings (name, value) VALUES ('dbVersion', '0')"
		if(log.isTraceEnabled())
			log.trace qry
		sql.execute qry
	}
	
	static private void createTables(Sql sql) {
		sql.withTransaction {
			sql.execute '''
				CREATE TABLE epg (
					id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1) PRIMARY KEY,
					name VARCHAR(512) NOT NULL UNIQUE
				)
			'''

			sql.execute '''
				CREATE TABLE alts (
					id BIGINT NOT NULL,
					name VARCHAR(512) NOT NULL,
					CONSTRAINT EPG_FK FOREIGN KEY (id) REFERENCES epg(id) ON DELETE CASCADE,
					PRIMARY KEY (id, name)
				)
			'''

			sql.execute '''
				CREATE TABLE settings (
					name VARCHAR(512) NOT NULL PRIMARY KEY,
					value VARCHAR(4096)
				)
			'''
		}
	}

	EventParticipantMap getAlternatives(String epgName) {
		def sql = new Sql(DATA_SRC.connection)
		def qry = "SELECT a.name FROM epg AS e LEFT OUTER JOIN alts AS a ON (e.id = a.id) WHERE e.name = $epgName"
		try {
			if(log.isTraceEnabled()) {
				def params = sql.getParameters(qry)
				def qryStr = sql.asSql(qry, params)
				log.trace "$qryStr $params"
			}
			def alts = new HashSet()
			sql.eachRow(qry) {
				alts << it[0]
			}
			alts << epgName
			new EventParticipantMap(epgName, alts)
		} finally {
			sql.close()
		}
	}
	
	String getSetting(String name, String defaultValue = null) {
		def sql = new Sql(DATA_SRC.connection)
		def qry = "SELECT value FROM settings WHERE name = $name"
		try {
			if(log.isTraceEnabled()) {
				def params = sql.getParameters(qry)
				def qryStr = sql.asSql(qry, params)
				log.trace "$qryStr $params"
			}
			return sql.firstRow(qry)?.value ?: defaultValue
		} finally {
			sql.close()
		}
	}
	
	void setSetting(String name, String value) {
		def sql = new Sql(DATA_SRC.connection)
		def delQry = "DELETE FROM settings WHERE name = $name"
		def insQry = "INSERT INTO settings (name, value) VALUES ($name, $value)"
		try {
			if(log.isTraceEnabled()) {
				def params = sql.getParameters(delQry)
				def qryStr = sql.asSql(delQry, params)
				log.trace "$qryStr $params"
				params = sql.getParameters(insQry)
				qryStr = sql.asSql(insQry, params)
				log.trace "$qryStr $params"
			}
			sql.withTransaction {
				sql.execute delQry
				if(sql.executeUpdate(insQry) != 1)
					throw new RuntimeException('DBError writing setting value')
			}
		} finally {
			sql.close()
		}
	}	
}
