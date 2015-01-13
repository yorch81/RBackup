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

	public WebApp() {
		/**
	     * Port Applicacion
	     */		
		setPort(8080);
		
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
	        	response.redirect("/index.html");
	        	
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
	        	
	        	String fs = getFiles(dir);
	        	
	        	return fs;
	        	
	        }
	    });
		
		/**
	     * Path /getfiles
	     * Get Files Structure
	     */	
		post("/rbackup", new Route() {
	        @Override
	        public Object handle(Request request, Response response) {
	        	
	        	RBackup rbackup = new RBackup("localhost", "sa", "password", "MAEAS");
	    		
	    		rbackup.backup("C:/TEMP/Backups/mydb.bak", "mydb");
	    			        	
	        	return "redirect";
	        	
	        }
	    });
		
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
			dir = "C:/TEMP/"; // basedir
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

}
