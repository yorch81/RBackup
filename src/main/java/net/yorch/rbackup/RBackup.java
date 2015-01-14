package net.yorch.rbackup;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * RBackup Application
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
     * Connection DB Handler
     *
     * VAR Connection conn DB Connection
     * @access private
     */
	private Connection conn = null;
	
	/**
	 * Constructor of Class
	 *
	 * @param hostname String DataBase Server
	 * @param username String DataBase User
	 * @param password String User Password
	 * @param dbname String DataBase Name
	 * @param basedir String Directory Base
	 * @return Instance 
	 * @see RBackup
	 */
	public RBackup(String hostname, String username, String password, String dbname) {
		String selectMethod = "Direct";
		String portNumber = "1433";
		String connectionUrl = "jdbc:sqlserver://" + hostname + ":" + portNumber + ";databaseName=" + dbname + ";user=" + username + ";password=" + password + ";selectMethod=" + selectMethod + ";"; 
        
        try {
	        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	        
	        this.conn = DriverManager.getConnection(connectionUrl);
        }
	    catch (Exception e){
	    	this.conn = null;
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Return true if Connected
	 *
	 * @return boolean
	 */	
	public boolean isConnected() {
		return (this.conn == null ? false : true);
	}
	
	/**
	 * Execute backup in SQL Server
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @return int 0 successful 1 File Exists 2 Not Connected 3 SQL Exception
	 */
	public int backup (String filename, String database) {
		int retValue = 0;
		
		if (this.fileExists(filename))
			retValue = 1;
		else{
			StringBuffer query = new StringBuffer("BACKUP DATABASE ");
			query.append(database);
			query.append(" TO  DISK = '");
			query.append(filename);
			query.append("' WITH COMPRESSION, NOFORMAT, INIT, NAME = N'Full Backup', SKIP, NOREWIND, NOUNLOAD,  STATS = 10");
					
			Statement stmt = null;
	        
			if (this.isConnected()){
				try {
					stmt = this.conn.createStatement();
					stmt.execute(query.toString());
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
	 * Check if file exists
	 * 
	 * @param filename String Filename of Backup
	 * @return boolean
	 */
	private boolean fileExists(String filename){
		File bakFile = new File(filename);
	
		return bakFile.exists();
	}
}
