DROP TABLE IF EXISTS `apps`;
CREATE TABLE `apps` (
  `username` varchar(32) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `token` varchar(64) NOT NULL,
  `tokentype` varchar(16) DEFAULT NULL,
  `referer` varchar(128) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expire` timestamp ,
  `scope` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`username`,`token`),
  UNIQUE KEY `xapp` (`token`),
  KEY `referer` (`referer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



insert into version_users (idmajor,idminor,comment) values (2,8,"AMBITDB users");