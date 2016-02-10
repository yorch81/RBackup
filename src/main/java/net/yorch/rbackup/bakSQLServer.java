package net.yorch.rbackup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * bakSQLServer<br>
 * 
 * Implementation of SQL Server Backups<br><br>
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
public class bakSQLServer extends Backup {
	/**
	 * Connection Pool
	 * 
	 * VAR BasicDataSource pool Connection Pool
	 */
	private BasicDataSource pool = new BasicDataSource();
	
	/**
	 * Constructor of Class
	 *
	 * @param hostname String DataBase Server
	 * @param username String DataBase User
	 * @param password String User Password
	 * @param dbname String DataBase Name
	 */
	public bakSQLServer(String hostname, String username, String password, String dbname) {
		String portNumber = "1433"; 
		Connection conn = null;
		
        try {
        	// Init Pool
        	pool.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    		pool.setUrl("jdbc:sqlserver://" + hostname + ":" + portNumber);
    		pool.setUsername(username);
    		pool.setPassword(password);
    		pool.setDefaultCatalog(dbname);
	        
    		pool.setValidationQuery("select 1");
    		
    		conn = pool.getConnection();
    		
    		connected = true;
    		
    		conn.close();
        }
	    catch (Exception e){
	    	connected = false;
	        e.printStackTrace();
	    } finally {
	    	if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		Connection conn = null;
		
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
					conn = pool.getConnection();
					
					stmt = conn.createStatement();
					stmt.execute(query.toString());
					
					conn.close();
				} catch (SQLException e) {
					retValue = 3;
					e.printStackTrace();
				} finally {
			    	if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
	 * @return String
	 */
	@Override
	public String dbList() {
		Statement stmt = null;
        String query = "SELECT UPPER(sdb.name) AS description FROM master..sysdatabases sdb WHERE sdb.name NOT IN ('master','model','msdb','pubs','northwind','tempdb') ORDER BY sdb.name";
        ResultSet rs = null;
        Connection conn = null;
        String retValue = "";
        
		if (this.isConnected()){
			try {
				conn = pool.getConnection();
				
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query);
				
				retValue = dbAsOption(rs);
				
				conn.close();
			} catch (SQLException e) {
				rs = null;
				e.printStackTrace();
			} finally {
		    	if (conn != null)
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
		}
		
		return retValue;
	}

	/**
	 * Execute Restore SQL Server
	 * 
	 * @param filename String Filename of Backup
	 * @param database String DataBase name
	 * @param mdfDir String MDF Files Directory
	 * @param ldfDir String LDF Files Directory
	 * @return int 0 successful 1 File Not Exists 2 Not Connected 3 Database already exists 4 SQL Exception
	 */
	@Override
	public int restore(String filename, String database, String mdfDir, String ldfDir) {
		int retValue = 0;
		Connection conn = null;
		
		if (! this.fileExists(filename))
			retValue = 1;
		else{					
			Statement stmt = null;
	        
			if (this.isConnected()){
				if (dbExists(database)) {
					retValue = 3;
				}
				else {
					try {
						StringBuffer query = new StringBuffer("RESTORE DATABASE ");
						query.append(database);
						query.append(" FROM  DISK = N'");
						query.append(filename);
						query.append("' WITH  FILE = 1,");
						
						// Gets Logical Names
						String command = "RESTORE FILELISTONLY FROM DISK = N'" + filename + "'";
						
						conn = pool.getConnection();
						stmt = conn.createStatement();
						ResultSet rs = stmt.executeQuery(command);
						
						while(rs.next()){
							query.append(" MOVE N'");
							query.append(rs.getString("LogicalName"));
							query.append("' TO N'");
							
							if (rs.getString("Type").equals("D")){
								query.append(mdfDir);
								query.append(database);
								query.append(".mdf',");
							}
							else{
								query.append(ldfDir);
								query.append(database);
								query.append(".ldf',");
							}
						}
						
						command = query.toString().replace("/", "\\") + " NOUNLOAD,  REPLACE,  STATS = 10";
						
						stmt = conn.createStatement();
						stmt.execute(command);
						
						conn.close();
					} catch (SQLException e) {
						retValue = 4;
						e.printStackTrace();
					} finally {
				    	if (conn != null)
							try {
								conn.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    }
				}
			}
			else
				retValue = 2;
		}
		
		return retValue;
	}

	/**
	 * Checks if Database Exists
	 * 
	 * @param dbName String Database Name
	 * @return boolean
	 */
	private boolean dbExists(String dbName) {
		boolean retValue = false;
		
		Statement stmt = null;
        String query = "SELECT COUNT(*) AS TOTAL FROM master..sysdatabases sdb WHERE sdb.name = '" + dbName + "'";
        ResultSet rs = null;
        Connection conn = null;
        
		if (this.isConnected()){
			try {
				conn = pool.getConnection();
				
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				rs = stmt.executeQuery(query);
				
				rs.first();
				
				if (rs.getInt("TOTAL") > 0)
					retValue = true;
				
				conn.close();
			} catch (SQLException e) {
				rs = null;
				e.printStackTrace();
			} finally {
		    	if (conn != null)
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
		}
		
		return retValue;
	
	}
}
