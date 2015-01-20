package net.yorch.rbackup;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Backup
 * 
 * Abstract Class to Generate Backup
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
 * @category   Backup
 * @package    net.yorch.rbackup
 * @copyright  Copyright 2015 JAPT
 * @license    http://www.apache.org/licenses/LICENSE-2.0
 * @version    1.0.0, 2015-20-01
 * @author     <a href="mailto:the.yorch@gmail.com">Jorge Alberto Ponce Turrubiates</a>
 */
public abstract class Backup {
	/**
     * Connection DB Handler
     *
     * VAR Connection conn DB Connection
     * @access protected
     */
	protected Connection conn = null;
	
	/**
	 * Return true if Connected
	 *
	 * @return boolean
	 */	
	public boolean isConnected() {
		return (this.conn == null ? false : true);
	}
		
	/**
	 * Check if file exists
	 * 
	 * @param filename String Filename of Backup
	 * @return boolean
	 */
	protected boolean fileExists(String filename){
		File bakFile = new File(filename);
	
		return bakFile.exists();
	}
	
	/**
	 * Execute backup in SQL Server
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @return int 0 successful 1 File Exists 2 Not Connected 3 SQL Exception
	 */
	public abstract int backup (String filename, String database);
	
	/**
	 * Return a ResultSet with DataBases List
	 * 
	 * @return ResultSet
	 */
	public abstract ResultSet dbList ();
}
