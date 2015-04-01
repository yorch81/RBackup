package net.yorch.rbackup;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.setPort;
import static spark.Spark.halt;

import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * WebApp<br>
 * 
 * Web Application<br><br>
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
 * @version    1.0.0, 2015-14-01
 * @author     <a href="mailto:the.yorch@gmail.com">Jorge Alberto Ponce Turrubiates</a>
 */
public class WebApp {    
	/**
     * Application User
     *
     * VAR String appUser Application User
     * @access private
     */
	private String appUser = "";
	
	/**
     * User Password
     *
     * VAR String appPassword User Password
     * @access private
     */
	private String appPassword = "";

	/**
     * MDF Directory
     *
     * VAR String mdfdir MDF Directory
     * @access private
     */
	private String mdfdir = "";
	
	/**
     * LDF Directory
     *
     * VAR String ldfdir LDF Directory
     * @access private
     */
	private String ldfdir = "";
	
	/**
     * Directory Base
     *
     * VAR String basedir Directory Base
     * @access private
     */
	private String basedir = "";
	
	/**
	 * Constructor of WebApp
	 * 
	 * @param rbackup RBackup Instance
	 * @param port int Application Port
	 * @param user String Application User
	 * @param password String Application User Password
	 * @param basedir String Directory Base
	 * @param mdf String MDF Directory
	 * @param ldf String LDF Directory
	 * @see WebApp
	 */
	public WebApp(RBackup rbackup, int port, String user, String password, String basedir, String mdf, String ldf) {
		this.appUser = user;
		this.appPassword = password;
		this.basedir = basedir;
		this.mdfdir = mdf;
		this.ldfdir = ldf;
		
		/**
	     * Port Applicacion
	     */		
		setPort(port);
		
		/**
	     * Public Files Path
	     */	
		Spark.staticFileLocation("/public");
		
		/**
	     * Path /
	     */
		get("/", new Route() {
	        @Override
	        public Object handle(Request request, Response response) {
	        	StringWriter template = null;
	        	
	        	if (existsSession(request)){	        		
	        		template = getRBackupTemplate(rbackup);
	        	}
	        	else{
	        		String LoginError = request.session().attribute("loginerror");
	        		template = getLoginTemplate(LoginError);
	        	}
	        		
	        	if (template == null)
	        		halt(500, "Internal Error");
					
				response.status(200);
					
				// Returns Template
				return template;
	        }
	    });
				
		/**
	     * Path /getfiles
	     * Get Files Structure
	     */
		post("/getfiles", new Route() {
	        @Override
	        public Object handle(Request request, Response response) {
	        	String dir = request.queryParams("dir");
	        	
	        	String fs = getFiles(dir);
	        	
	        	return fs;
	        }
	    });
		
		/**
	     * Path /rbackup
	     * Execute Backup
	     */
		post("/rbackup", new Route() {
	        @Override
	        public Object handle(Request request, Response response) {	 
	        	String fileName = request.queryParams("filename");
	        	String dbName = request.queryParams("dbname");
	        	     	
	    		int result = rbackup.backup(fileName, dbName);
	    			        
	    		if (result == 0) 
	    			response.status(200);
	    		else 
	    			response.status(206);
	    		
	    		return backupMsg(result);
	        }
	    });
		
		/**
	     * Path /restore
	     * Execute Restore DataBase
	     */
		post("/restore", new Route() {
	        @Override
	        public Object handle(Request request, Response response) {	 
	        	String fileName = request.queryParams("filename");
	        	String dbName = request.queryParams("dbname");
	        	     	
	    		int result = rbackup.restore(fileName, dbName, mdfdir, ldfdir);
	    		
	    		if (result == 0) 
	    			response.status(200);
	    		else 
	    			response.status(206);
	    		
	    		return restoreMsg(result);
	        }
	    });
		
		/**
	     * Login User
	     */
	    post("/webauth", new Route() {
	        @Override
	        public Object handle(Request request, Response response) {
	            String user = request.queryParams("txtUser");
	            String password = request.queryParams("txtPassword");
	           	                        	            
	        	if (login(user, password)){
	        		request.session().attribute("appuser", user);
	        		
	        		response.redirect("/");
	        		
	        		return "";
	        	}
	        	else{
	        		request.session().attribute("loginerror", "Could not login with credentials");
	        		
	        		response.status(401);	        		
	        		response.redirect("/");
	        		
	        		return "Could not login with credentials";
	        	}
	        }
	    });
		
	}
	
	/**
	 * Return Login Template
	 * 
	 * @param loginError String Error Login if Exits
	 * @return StringWriter
	 */
	private StringWriter getLoginTemplate(String loginError) {
		Map<String, Object> tempData = new HashMap<String, Object>();
		tempData.put("loginError", loginError);
		
		FMTemplate loginTemp = new FMTemplate("login.ftl", tempData);
		
		StringWriter swLogin = loginTemp.get();
		
		loginTemp = null;
		
		return swLogin;
	}
	
	/**
	 * Return RBackup Template
	 * 
	 * @param rbackup Instance of RBackup
	 * @return StringWriter
	 */
	private StringWriter getRBackupTemplate(RBackup rbackup) {
		Map<String, Object> tempData = new HashMap<String, Object>();
		String listDb = dbAsOption(rbackup.dbList());
		
		tempData.put("listDb", listDb);
		tempData.put("baseDir", this.basedir);
		
		FMTemplate rbackupTemp = new FMTemplate("rbackup.ftl", tempData);
		
		StringWriter swRBackup = rbackupTemp.get();
		
		rbackupTemp = null;
		
		return swRBackup;
	}
	
	/**
	 * Return List DataBases as HTML Option 
	 * 
	 * @param rsDb ResultSet of List of DataBases
	 * @return String
	 */
	private String dbAsOption(ResultSet rsDb) {
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
	 * Checks login of user and password
	 * 
	 * @param user User Application
	 * @param password Password User
	 * @return boolean
	 */
	private boolean login(String user, String password){
		if (user.equals(this.appUser) && password.equals(this.appPassword))
			return true;
		else
			return false;
	}
	
	/**
     * Checks if Session Exists
     * 
     * @return boolean
     */
	private boolean existsSession(Request request){
		boolean retValue = false;
		
		String user = request.session().attribute("appuser");
	
		if (user != null){
			retValue = true;
		}
				
		return retValue;
	}
		
	/**
	 * Check if Directory exists
	 * 
	 * @param dir String Directory
	 * @return boolean
	 */
	public static boolean dirExists(String dir){
		File dirFile = new File(dir);
	
		return dirFile.exists();
	}
	
	/**
	 * Return Files Structure
	 * 
	 * @param dir String Subdirectory
	 * @return String Files Structure
	 */
	private String getFiles(String dir){
		StringBuffer fs = new StringBuffer("");
						
		if (dir == null) {
			return "";
	    }
		
		if (dir.charAt(dir.length()-1) == '\\') {
	    	dir = dir.substring(0, dir.length()-1) + "/";
		} else if (dir.charAt(dir.length()-1) != '/') {
		    dir += "/";
		}
				
		try {
			dir = java.net.URLDecoder.decode(dir, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
			
		if (dir.equals("./")){
			dir = this.basedir;
		}
		
	    if (new File(dir).exists()) {
			String[] files = new File(dir).list(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
					return name.charAt(0) != '.';
			    }
			});
			
			Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
			fs.append("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
			
			// All dirs
			for (String file : files) {
			    if (new File(dir, file).isDirectory()) {
			    	fs.append("<li class=\"directory collapsed\"><a href=\"#\" rel=\"" + dir + file + "/\">"
						+ file + "</a></li>");
			    }
			}
			
			// All files
			for (String file : files) {
			    if (!new File(dir, file).isDirectory()) {
					int dotIndex = file.lastIndexOf('.');
					String ext = dotIndex > 0 ? file.substring(dotIndex + 1) : "";
					fs.append("<li class=\"file ext_" + ext + "\"><a href=\"#\" rel=\"" + dir + file + "\">"
						+ file + "</a></li>");
			    	}
			}
			
			fs.append("</ul>");
	    }
	    
		return fs.toString();
	}
	
	/**
	 * Gets Backup Error Message
	 * 
	 * @param type int Error Type
	 * @return String
	 */
	private String backupMsg(int type) {
		String retValue = "";
		
		if (type == 1) {
			retValue = "File Already Exists";
		} else if (type == 2) {
			retValue = "Not Connected to Server";
        } else if (type == 3) {
        	retValue = "DataBase Server Exception";
        }
		
		return retValue;
	}
	
	/**
	 * Gets Backup Error Message
	 * 
	 * @param type int Error Type
	 * @return String
	 */
	private String restoreMsg(int type) {
		String retValue = "";
		
		if (type == 1) {
			retValue = "File Not Exists";
		} else if (type == 2) {
			retValue = "Not Connected to Server";
        } else if (type == 3) {
        	retValue = "DataBase Already Exists";
        } else if (type == 3) {
        	retValue = "DataBase Server Exception";
        }
		
		return retValue;
	}
}


