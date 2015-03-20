package net.yorch.rbackup;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * bakMySQL<br>
 * 
 * Implementation of MySQL Backups
 * NOTE: The executable file of mysqldump must be accesible on Operating System PATH<br><br>
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
public class bakMySQL extends Backup {
	/**
	 * MySQL User
	 */
	private String mysqlUser = "";
	
	/**
	 * MySQL Password
	 */
	private String mysqlPassword = "";
		
	/**
	 * Constructor of Class
	 *
	 * @param hostname String DataBase Server
	 * @param username String DataBase User
	 * @param password String User Password
	 * @param dbname String DataBase Name
	 */
	public bakMySQL(String hostname, String username, String password, String dbname) {
		String connectionUrl = "jdbc:mysql://" + hostname + "/" + dbname; 
        
		this.mysqlUser = username;
        this.mysqlPassword = password;
        
        try {
	        Class.forName("com.mysql.jdbc.Driver");
	        
	        this.conn = DriverManager.getConnection(connectionUrl, username, password);
        }
	    catch (Exception e){
	    	this.conn = null;
	        e.printStackTrace();
	    }
	}

	/**
	 * Execute backup in SQL Server
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @return int 0 successful 1 File Exists 2 Not Connected 3 SQL Exception
	 */
	@Override
	public int backup(String filename, String database) {
		int retValue = 0;
		
		if (this.fileExists(filename))
			retValue = 1;
		else{			
			Statement stmt = null;
	        
			if (this.isConnected()){
				try {
					stmt = this.conn.createStatement();
					stmt.execute("FLUSH TABLES WITH READ LOCK;");
					
					if (executeMySQLDump(filename, database) != 0){
						retValue = 2;
					}
											
					stmt.execute("UNLOCK TABLES;");
				} catch (SQLException e) {
					retValue = 3;
					e.printStackTrace();
				}
			}
			else
				retValue = 2;
		}
		
		return retValue;	
	}

	/**
	 * Return a ResultSet with DataBases List
	 * 
	 * @return ResultSet
	 */
	@Override
	public ResultSet dbList() {
		Statement stmt = null;
        String query = "SELECT SCHEMA_NAME AS description FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME NOT IN ('information_schema', 'mysql', 'performance_schema', 'test') ORDER BY SCHEMA_NAME";
        ResultSet rs = null;
        
		if (this.isConnected()){
			try {
				stmt = this.conn.createStatement();
				rs = stmt.executeQuery(query);
			} catch (SQLException e) {
				rs = null;
				e.printStackTrace();
			}
		}
		
		return rs;
	}
	 
	/**
	 * Execute MySQL Dump Command
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @return int 
	 */
	private int executeMySQLDump(String filename, String database){
		StringBuffer command = new StringBuffer("mysqldump");
		
		if (System.getProperty("os.name").contains("Windows"))
			command.append(".exe");
			
		command.append(" --routines --add-drop-table --add-drop-database -u ");
		command.append(this.mysqlUser);
		command.append(" -p");
		command.append(this.mysqlPassword);
		command.append(" ");
		command.append(database);
		command.append(" > ");
		command.append(filename);
		
		String[] aCommand; 
		
		if (System.getProperty("os.name").contains("Windows"))
			aCommand = new String[]{"cmd.exe","/c",command.toString()};
		else
			aCommand = new String[]{"/bin/bash","-c",command.toString()};
		
		int status = 1;
		
		try {
			Process mysqldumpProc = Runtime.getRuntime().exec(aCommand);
			status = mysqldumpProc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}

	/**
	 * Execute Restore MySQL
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @param mdfDir String No uses
	 * @param ldfDir String No uses
	 * @return int 0 successful 1 File Not Exists 2 Not Connected 3 SQL Exception
	 */
	@Override
	public int restore(String filename, String database, String mdfDir, String ldfDir) {
		int retValue = 0;
		
		if (! this.fileExists(filename))
			retValue = 1;
		else{			
			Statement stmt = null;
	        
			if (this.isConnected()){
				try {
					stmt = this.conn.createStatement();
					stmt.execute("CREATE SCHEMA IF NOT EXISTS " + database + ";");
					stmt.execute("SET FOREIGN_KEY_CHECKS=0;");
									
					if (executeMySQLRestore(filename, database) != 0){
						retValue = 2;
					}
											
					stmt.execute("SET FOREIGN_KEY_CHECKS=1;;");
				} catch (SQLException e) {
					retValue = 3;
					e.printStackTrace();
				}
			}
			else
				retValue = 2;
		}
		
		return retValue;
	}
	
	/**
	 * Execute MySQL Restore Command
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @return int 
	 */
	private int executeMySQLRestore(String filename, String database){
		StringBuffer command = new StringBuffer("mysql");
		
		if (System.getProperty("os.name").contains("Windows"))
			command.append(".exe");
			
		command.append(" -u ");
		command.append(this.mysqlUser);
		command.append(" -p");
		command.append(this.mysqlPassword);
		command.append(" ");
		command.append(database);
		command.append(" < ");
		command.append(filename);
		
		String[] aCommand; 
		
		if (System.getProperty("os.name").contains("Windows"))
			aCommand = new String[]{"cmd.exe","/c",command.toString()};
		else
			aCommand = new String[]{"/bin/bash","-c",command.toString()};
		
		int status = 1;
		
		try {
			Process mysqldumpProc = Runtime.getRuntime().exec(aCommand);
			status = mysqldumpProc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}
}
