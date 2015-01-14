package net.yorch.rbackup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Application Class
 *
 */
public class App {
	public static void main( String[] args ){
		Properties config = new Properties();
		try {
			if (args.length == 1){
				config.load(new FileInputStream(args[0]));
	    	}
	    	else{
	    		config.load(new FileInputStream("rbackup.properties"));
	    	}
			
			String hostname = config.getProperty("hostname");
			String user = config.getProperty("user");
			String password = config.getProperty("password");
			String dbname = config.getProperty("dbname");
			String port = config.getProperty("port");
			String basedir = config.getProperty("basedir");
			String appUser = config.getProperty("appuser");
			String appPassword = config.getProperty("apppassword");
			
			/**
		     * RBackup Application
		     */	
			RBackup rbackup = new RBackup(hostname, user, password, dbname);
			
			if (WebApp.basedirExists(basedir)){
				if (rbackup.isConnected()){
					new WebApp(rbackup, Integer.parseInt(port), appUser, appPassword, basedir);
				}
				else
					System.out.println("Could not connect to SQL Server");
			}
			else
				System.out.println("Directory Base not Exists");			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}