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
			System.out.println("Archivo de configuración no existe, configure correctamente la aplicación");
			
			Interactive interactive = new Interactive();
			
			interactive.addQuestion("dbtype","Teclee el tipo de Base de Datos: ");
			interactive.addQuestion("hostname","Teclee el nombre del hostname: ");
			interactive.addQuestion("user","Telcle el nombre de usuario: ");
			interactive.addQuestion("password","Teclee el password: ");
			interactive.addQuestion("dbname","Teclee el nombre de la Base de Datos: ");
			interactive.addQuestion("dbport","Teclee el puerto de la Base de Datos: ");
			interactive.addQuestion("port","Teclee el puerto de la aplicacíon: ");
			interactive.addQuestion("basedir","Teclee la dirección de la Base de Datos: ");
			interactive.addQuestion("mdfdir","Teclee la direccion del archivo mdf: ");
			interactive.addQuestion("ldfdir","Teclee la dirección del archivo ldf: ");
			interactive.addQuestion("appuser","Teclee el nombre de usuario de la aplicación:  ");
			interactive.addQuestion("apppassword","Teclee el password de la aplicacíon: ");
			
			interactive.interactive();
			interactive.save("rbackup.properties");
			
			System.out.printf("Archivo de Configuracion generado, reinicie la aplicación");
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
