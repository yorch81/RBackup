package net.yorch.rbackup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
	 * @param hostname String Servidor de Bases de Datos
	 * @param username String Usuario de Bases de Datos
	 * @param password String Password de Bases de Datos
	 * @param dbname String Nombre de Base de Datos o Recurso
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
	 * @return
	 */
	public boolean backup (String filename, String database) {
		String query = "BACKUP DATABASE mydb TO  DISK = 'C:\\DBF\\mydb.bak' WITH COMPRESSION, NOFORMAT, INIT, NAME = N'Full Backup', SKIP, NOREWIND, NOUNLOAD,  STATS = 10";
			
		Statement stmt = null;
        
		if (this.isConnected()){
			try {
				stmt = this.conn.createStatement();
				stmt.execute(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return true;		
	}
}
