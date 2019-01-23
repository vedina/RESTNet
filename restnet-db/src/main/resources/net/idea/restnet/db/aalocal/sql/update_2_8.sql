DROP TABLE IF EXISTS `apps`;

CREATE TABLE `apps` (
  `username` varchar(32) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `token` varchar(64) NOT NULL,
  `tokentype` varchar(16) DEFAULT NULL,
  `referer` varchar(128) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expire` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `scope` varchar(128) DEFAULT NULL,
  `enabled` tinyint(4) DEFAULT '1',
  PRIMARY KEY (`username`,`token`),
  UNIQUE KEY `xapp` (`token`),
  KEY `referer` (`referer`),
  KEY `xenabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 insert into roles values ("ambit_apps");
 
-- ALTER TABLE `apps` ADD COLUMN `enabled` TINYINT NULL DEFAULT 1  AFTER `scope` ;
-- ALTER TABLE `apps` ADD INDEX `xenabled` (`enabled` ASC) ;



insert into version_users (idmajor,idminor,comment) values (2,8,"AMBITDB users");