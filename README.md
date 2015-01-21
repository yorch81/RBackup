# RBackup #

## Description ##
Web Tools for Remote Backup DataBases SQL Server and MySQL

## Requirements ##
* [Java](https://www.java.com/es/download/)
* [Spark](http://www.sparkjava.com/)
* [FreeMarker](http://freemarker.org/)
* [JQuery](http://jquery.com/)
* [Bootstrap](http://getbootstrap.com/)
* [SQL Server](http://www.microsoft.com/es-es/server-cloud/products/sql-server/)
* [MySQL](http://www.mysql.com/)
* [JQuery File Tree](https://github.com/daverogers/jQueryFileTree)

## Developer Documentation ##
JavaDoc.

## Installation ##
Generate and execute jar.

Create configuration file with the next structure:

dbtype=MSSQLSERVER (or MYSQL)
hostname=localhost (or localhost\INSTANCE_OF_SQL_SERVER)
user= (User of SQL Server or MYSQL)
password= (Password of User)
dbname= (DataBase Name)
port=8080 (Web Tool Port)
basedir= (Directory Base)
appuser= (Application User)
apppassword= (Application Password)

## Notes ##
The executable file of mysqldump must be accesible on Operating System PATH

## References ##
http://www.sparkjava.com/

http://freemarker.org/







