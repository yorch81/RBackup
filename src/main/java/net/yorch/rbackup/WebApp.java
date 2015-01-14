package net.yorch.rbackup;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.setPort;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

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
	 */
	public WebApp(RBackup rbackup, int port, String user, String password, String basedir) {
		this.appUser = user;
		this.appPassword = password;
		this.basedir = basedir;
		
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
	        	if (existsSession(request)){
	        		response.redirect("/rbackup.html");
	        	}
	        	else
	        		response.redirect("/login.html");
	        	
	        	
	        	return "redirect";
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
	        	
	        	String fs = getFiles(dir, request);
	        	
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
	        	String currentDir = request.session().attribute("currentdir");
	        	
	        	if (currentDir != null){
	        		currentDir = currentDir + "mydb.bak";
	        	}
	        	
	    		rbackup.backup(currentDir, "mydb");
	    			        	
	        	return "redirect";
	        	
	        }
	    });
		
		/**
	     * Valida Usuario y Password
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
	        		response.status(401);
	        		response.redirect("/");
	        		
	        		return "Could not login with credentials";
	        	}
	        }
	    });
		
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
	 * Check if Directory Base exists
	 * 
	 * @param basedir String Directory Base
	 * @return boolean
	 */
	public static boolean basedirExists(String basedir){
		File dirFile = new File(basedir);
	
		return dirFile.exists();
	}
	
	/**
	 * Return Files Structure
	 * 
	 * @param dir String Subdirectory
	 * @param request Request request
	 * @return String Files Structure
	 */
	private String getFiles(String dir, Request request){
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
		
		// Create currentdir Session
		request.session().attribute("currentdir", dir);
		
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

}


