java -classpath .;%root%\libs\deegree2.jar;%root%\libs\ojdbc14_10g.jar;%root%\libs\log4j-1.2.9.jar;%root%\libs\jaxen-1.1-beta-8.jar org.deegree.tools.security.DRMAccess -driver oracle.jdbc.driver.OracleDriver -logon jdbc:oracle:thin:@localhost:1521:latlon -user security -pw security -action assignRights -constraints -;e:/temp/complexfilter.xml -soName {http://www.deegree.org/app}:WPVS -soType Featuretype -role anonymous_role -rights GetFeature,GetFeature_Response

pause