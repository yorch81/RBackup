package net.yorch.rbackup;

/**
 * Init Application
 *
 */
public class App {
	public static void main( String[] args ){
		System.out.println( "RBackup MSSQLSERVER" );
		
		RBackup rbackup = new RBackup("localhost", "sa", "password", "MAEAS");
		
		rbackup.backup("C:\\DBF\\mydb.bak", "mydb");
	}
}