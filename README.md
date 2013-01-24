REST application classes

originally a fork from [AMBIT REST](https://ambit.svn.sourceforge.net/svnroot/ambit/trunk/ambit2-all/ambit2-rest) classes.
More information on AMBIT at http://ambit.sourceforge.net .


JAR and WAR artifacts are available via [Maven repository](http://ambit.uni-plovdiv.bg:8083/nexus/index.html#nexus-search;gav~~restnet*~~~)


Test database (as per aalocal-test Maven profile) 

     CREATE DATABASE `aalocal_test` /*!40100 DEFAULT CHARACTER SET utf8 */;
     GRANT ALL ON `aalocal_test`.* TO 'guest'@'localhost';
     GRANT UPDATE,DROP,CREATE,SELECT,INSERT,EXECUTE, DELETE, CREATE ROUTINE, ALTER ROUTINE on `aalocal_test`.* TO 'guest'@'localhost';
