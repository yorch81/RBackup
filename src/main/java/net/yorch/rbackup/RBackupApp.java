package net.yorch.rbackup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import net.yorch.Interactive;

/**
 * RBackupApp<br>
 * 
 * Application Class<br><br>
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
 * @version    1.0.0, 2015-12-01
 * @author     <a href="mailto:the.yorch@gmail.com">Jorge Alberto Ponce Turrubiates</a>
 */
public class RBackupApp {

	/**
	 * Main Method
	 * 
	 * @param args String Properties File Name
	 */
	public static void main(String[] args) {
		Properties config = new Properties();
		
		try {
			if (args.length == 1){
				config.load(new FileInputStream(args[0]));
	    	}
	    	else{
	    		config.load(new FileInputStream("rbackup.properties"));
	    	}
			
			String dbType = config.getProperty("dbtype");
			String hostname = config.getProperty("hostname");
			String user = config.getProperty("user");
			String password = config.getProperty("password");
			String dbname = config.getProperty("dbname");
			int dbport = Integer.parseInt(config.getProperty("dbport"));
			String port = config.getProperty("port");
			String basedir = config.getProperty("basedir");
			String mdfdir = config.getProperty("mdfdir");
			String ldfdir = config.getProperty("ldfdir");
			String appUser = config.getProperty("appuser");
			String appPassword = config.getProperty("apppassword");
			
			/**
		     * RBackup Application
		     */	
			RBackup rbackup = null;
			
			if (dbType.equals("MSSQLSERVER"))
				rbackup = new RBackup(RBackup.MSSQLSERVER, hostname, user, password, dbname, dbport);
			else
				rbackup = new RBackup(RBackup.MYSQL, hostname, user, password, dbname, dbport);
						
			if (! WebApp.dirExists(basedir)){
				System.out.println("Directory Base not Exists");
			}
			else if(! WebApp.dirExists(mdfdir)){
				System.out.println("MDF Directory not Exists");
			}
			else if(! WebApp.dirExists(ldfdir)){
				System.out.println("LDF Directory not Exists");
			}
			else{
				if (rbackup.isConnected()){
					new WebApp(rbackup, Integer.parseInt(port), appUser, appPassword, basedir, mdfdir, ldfdir);
				}
				else
					System.out.println("Could not connect to DataBase Server");
			}
							
		} catch (FileNotFoundException e) {
			System.out.println("Configuration File does not Exists, please configure");
			
			Interactive interactive = new Interactive();
			
			interactive.addQuestion("dbtype","Type Database (MSSQLSERVER/MYSQL):");
			interactive.addQuestion("hostname","Type Database Server:");
			interactive.addQuestion("user","Type Database User:");
			interactive.addQuestion("password","Type User password:");
			interactive.addQuestion("dbname","Type Database Name:");
			interactive.addQuestion("dbport","Type Database Port:");
			interactive.addQuestion("port","Type Application Web Port:");
			interactive.addQuestion("basedir","Type Base Directory:");
			interactive.addQuestion("mdfdir","Type MDF Directory:");
			interactive.addQuestion("ldfdir","Type LDF Directory:");
			interactive.addQuestion("appuser","Type Application User:");
			interactive.addQuestion("apppassword","Type Application Password:");
			
			interactive.interactive();
			interactive.save("rbackup.properties");
			
			System.out.printf("File generated, please restart Application");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
