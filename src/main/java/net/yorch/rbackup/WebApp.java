package net.yorch.rbackup;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.setPort;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class WebApp {

	public WebApp(RBackup rbackup, int port) {
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
	        	
	        	String fs = rbackup.getFiles(dir);
	        	
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
	    		rbackup.backup("C:/TEMP/Backups/mydb.bak", "mydb");
	    			        	
	        	return "redirect";
	        	
	        }
	    });
		
	}
}
