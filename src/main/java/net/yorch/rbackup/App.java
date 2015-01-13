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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String hostname = config.getProperty("hostname");
		String user = config.getProperty("user");
		String password = config.getProperty("password");
		String dbname = config.getProperty("dbname");
		String port = config.getProperty("port");
		String basedir = config.getProperty("basedir");
		
		/**
	     * RBackup Application
	     */	
		RBackup rbackup = new RBackup(hostname, user, password, dbname, basedir);
		
		if (rbackup.isConnected()){
			if (rbackup.basedirExists()){
				new WebApp(rbackup, Integer.parseInt(port));
			}
			else
				System.out.println("Directory Base Not Exists");
		}
		else
			System.out.println("Could not connect to SQL Server");
	}
}