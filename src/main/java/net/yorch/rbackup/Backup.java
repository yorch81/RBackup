package net.yorch.rbackup;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Backup<br>
 * 
 * Abstract Class to Generate Backup<br><br>
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
 * @version    1.0.0, 2015-20-01
 * @author     <a href="mailto:the.yorch@gmail.com">Jorge Alberto Ponce Turrubiates</a>
 */
public abstract class Backup {
	/**
     * Connection Flag
     *
     * VAR boolean connected Connection Flag
     */
	protected boolean connected = false;
	
	/**
	 * Return true if connected else false
	 * 
	 * @return boolean
	 */
	protected boolean isConnected() {
		return connected;
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
	 * Return List DataBases as HTML Option 
	 * 
	 * @param rsDb ResultSet of List of DataBases
	 * @return String
	 */
	protected String dbAsOption(ResultSet rsDb) {
		StringBuilder html = new StringBuilder("");
		
		try {
			while(rsDb.next()){
				html.append("<option value=\"");
				html.append(rsDb.getString("description"));
		
				html.append("\">");
				html.append(rsDb.getString("description"));
				html.append("</option>\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return html.toString();
	}
	
	/**
	 * Execute Backup
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @return int 0 successful 1 File Exists 2 Not Connected 3 SQL Exception
	 */
	public abstract int backup (String filename, String database);
	
	/**
	 * Return a ResultSet with DataBases List
	 * 
	 * @return String
	 */
	public abstract String dbList ();
	
	/**
	 * Execute Restore
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @param mdfDir String MDF Files Directory
	 * @param ldfDir String LDF Files Directory
	 * @return int 0 successful 1 File Exists 2 Not Connected 3 SQL Exception
	 */
	public abstract int restore (String filename, String database, String mdfDir, String ldfDir);
}
