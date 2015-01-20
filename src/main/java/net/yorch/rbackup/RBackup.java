package net.yorch.rbackup;

import java.sql.ResultSet;

/**
 * RBackup
 * 
 * RBackup Application
 * 
 * Copyright 2015 Jorge Alberto Ponce Turrubiates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @category   RBackup
 * @package    net.yorch.rbackup
 * @copyright  Copyright 2015 JAPT
 * @license    http://www.apache.org/licenses/LICENSE-2.0
 * @version    1.0.0, 2015-12-01
 * @author     <a href="mailto:the.yorch@gmail.com">Jorge Alberto Ponce Turrubiates</a>
 */
public class RBackup {
	/**
	 * DataBase Types
	 */
	public static final int MSSQLSERVER=1;
    public static final int MYSQL=2;
	
    /**
     * instance of Backup
     */
    private Backup backup = null;
    
	/**
	 * Constructor of Class
	 *
	 * @param type int DataBase Type
	 * @param hostname String DataBase Server
	 * @param username String DataBase User
	 * @param password String User Password
	 * @param dbname String DataBase Name
	 * @param basedir String Directory Base
	 * @return Instance 
	 * @see RBackup
	 */
	public RBackup(int type, String hostname, String username, String password, String dbname) {
		if (type == RBackup.MSSQLSERVER)
			backup = new bakSQLServer(hostname, username, password, dbname);
		else
			backup = new bakMySQL(hostname, username, password, dbname);
	}	

	/**
	 * Execute backup in SQL Server
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @return int 0 successful 1 File Exists 2 Not Connected 3 SQL Exception
	 */
	public int backup(String filename, String database) {
		return backup.backup(filename, database);
	}
	
	/**
	 * Return a ResultSet with DataBases List
	 * 
	 * @return ResultSet
	 */
	public ResultSet dbList() {
		return backup.dbList();
	}
	
	/**
	 * Return true if Connected
	 *
	 * @return boolean
	 */	
	public boolean isConnected() {
		return backup.isConnected();
	}
}
