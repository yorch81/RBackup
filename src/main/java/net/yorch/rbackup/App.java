package net.yorch.rbackup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * App
 * 
 * Application Class
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
 * @category   App
 * @package    net.yorch.rbackup
 * @copyright  Copyright 2015 JAPT
 * @license    http://www.apache.org/licenses/LICENSE-2.0
 * @version    1.0.0, 2015-12-01
 * @author     <a href="mailto:the.yorch@gmail.com">Jorge Alberto Ponce Turrubiates</a>
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
			
			rbackup.dbList();
			
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