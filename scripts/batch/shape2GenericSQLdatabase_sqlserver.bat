C:\Programme\Java\jdk1.5.0_06\bin\java -Xms100m -Xmx300m -classpath .;..\..\classes;..\..\lib\sqlserver\msbase.jar;..\..\lib\sqlserver\mssqlserver.jar;..\..\lib\sqlserver\msutil.jar;..\..\lib\jts\jts-1.8.jar;..\..\lib\xml\jaxen-1.1-beta-8.jar;..\..\lib\log4j\log4j-1.2.9.jar org.deegree.tools.shape.GenericSQLShapeImporter -driver com.microsoft.jdbc.sqlserver.SQLServerDriver -url jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=testwfs -user www2 -password www2 -indexName fln -table Flaechennutzung -shapeFile D:\java\projekte\huis\produktionssystem\daten\flaechennutzung -maxTreeDepth 5 -idType INTEGER
pause