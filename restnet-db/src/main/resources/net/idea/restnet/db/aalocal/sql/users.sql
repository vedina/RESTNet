-- Database: ambit_users
-- CREATE DATABASE `aalocal_test`  DEFAULT CHARACTER SET utf8 ;
-- ------------------------------------------------------
--
-- Table structure for table `user`
--
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `iduser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(32) DEFAULT NULL COMMENT 'OpenAM user name',
  `title` varchar(16) DEFAULT NULL,
  `firstname` varchar(45) NOT NULL,
  `lastname` varchar(45) NOT NULL,
  `institute` varchar(128) DEFAULT NULL,
  `weblog` varchar(45) DEFAULT NULL,
  `homepage` varchar(45) DEFAULT NULL,
  `address` varchar(128) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `keywords` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '""',
  `reviewer` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'true if wants to become a reviewer',
  PRIMARY KEY (`iduser`),
  UNIQUE KEY `Index_2` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_name` varchar(32) NOT NULL,
  `user_pass` varchar(32) NOT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `roles`
--
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `role_name` varchar(40) NOT NULL DEFAULT 'ambit_guest',
  PRIMARY KEY (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `user_roles`
--
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `user_name` varchar(16) NOT NULL,
  `role_name` varchar(40) NOT NULL,
  PRIMARY KEY (`user_name`,`role_name`),
  KEY `urolefk_idx` (`role_name`),
  CONSTRAINT `urolefk` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `userfk` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `user_registration`
--
DROP TABLE IF EXISTS `user_registration`;
CREATE TABLE `user_registration` (
  `user_name` varchar(16) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `confirmed` timestamp NOT NULL DEFAULT '2019-01-01 00:00:00',
  `code` varchar(45) NOT NULL,
  `status` enum('disabled','commenced','confirmed') NOT NULL DEFAULT 'disabled',
  PRIMARY KEY (`user_name`),
  UNIQUE KEY `kur2` (`code`) USING BTREE,
  CONSTRAINT `` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `policy`
--
DROP TABLE IF EXISTS `policy`;
CREATE TABLE `policy` (
  `idpolicy` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(40) NOT NULL DEFAULT 'ambit_guest',
  `prefix` varchar(255) NOT NULL,
  `resource` varchar(255) NOT NULL,
  `level` smallint(6) DEFAULT '1',
  `mget` tinyint(1) NOT NULL DEFAULT '0',
  `mput` tinyint(1) NOT NULL DEFAULT '0',
  `mpost` tinyint(1) NOT NULL DEFAULT '0',
  `mdelete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idpolicy`),
  UNIQUE KEY `uri` (`prefix`,`resource`,`role_name`),
  KEY `fkrole1_idx` (`role_name`),
  KEY `get` (`mget`),
  KEY `put` (`mput`),
  KEY `post` (`mpost`),
  KEY `delete` (`mdelete`),
  KEY `fk_resource` (`resource`),
  CONSTRAINT `fkrole1` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Table structure for table `organisation`
--

DROP TABLE IF EXISTS `organisation`;
CREATE TABLE `organisation` (
  `idorganisation` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `ldapgroup` varchar(128) DEFAULT NULL,
  `cluster` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`idorganisation`),
  UNIQUE KEY `xorg2` (`name`),
  KEY `korg2` (`cluster`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------
-- Bundle specific policies
-- -----------------------------------
DROP TABLE IF EXISTS `policy_bundle`;
CREATE TABLE `policy_bundle` (
  `idpolicy` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(40) NOT NULL DEFAULT 'ambit_guest',
  `prefix` varchar(255) NOT NULL,
  `resource` varbinary(16) NOT NULL,
  `level` smallint(6) DEFAULT '2',
  `mget` tinyint(1) NOT NULL DEFAULT '0',
  `mput` tinyint(1) NOT NULL DEFAULT '0',
  `mpost` tinyint(1) NOT NULL DEFAULT '0',
  `mdelete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idpolicy`),
  UNIQUE KEY `uri` (`prefix`,`resource`,`role_name`),
  KEY `fkrole1_idx` (`role_name`),
  KEY `get` (`mget`),
  KEY `put` (`mput`),
  KEY `post` (`mpost`),
  KEY `delete` (`mdelete`),
  KEY `fk_resource` (`resource`),
  CONSTRAINT `fkbrole1` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `project`
--
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `idproject` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `ldapgroup` varchar(128) DEFAULT NULL,
  `cluster` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`idproject`),
  UNIQUE KEY `xprj2` (`name`),
  KEY `kprj2` (`cluster`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `user_organisation`
--
DROP TABLE IF EXISTS `user_organisation`;
CREATE TABLE `user_organisation` (
  `iduser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idorganisation` int(10) unsigned NOT NULL,
  `priority` int(2) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`iduser`,`idorganisation`),
  KEY `FK_user_organisation_2` (`idorganisation`),
  KEY `kprjuo` (`iduser`,`priority`),
  CONSTRAINT `FK_user_organisation_1` FOREIGN KEY (`iduser`) REFERENCES `user` (`iduser`) ON UPDATE CASCADE,
  CONSTRAINT `FK_user_organisation_2` FOREIGN KEY (`idorganisation`) REFERENCES `organisation` (`idorganisation`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `user_project`
--
DROP TABLE IF EXISTS `user_project`;
CREATE TABLE `user_project` (
  `iduser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idproject` int(10) unsigned NOT NULL,
  `priority` int(2) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`iduser`,`idproject`),
  KEY `FK_user_project_2` (`idproject`),
  KEY `kup2` (`iduser`,`priority`),
  CONSTRAINT `FK_user_project_1` FOREIGN KEY (`iduser`) REFERENCES `user` (`iduser`) ON UPDATE CASCADE,
  CONSTRAINT `FK_user_project_2` FOREIGN KEY (`idproject`) REFERENCES `project` (`idproject`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `apps`;
CREATE TABLE `apps` (
  `username` varchar(32) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `token` varchar(64) NOT NULL,
  `tokentype` varchar(16) DEFAULT NULL,
  `referer` varchar(128) DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expire` timestamp  NOT NULL DEFAULT '2019-01-01 00:00:00',
  `scope` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`username`,`token`),
  UNIQUE KEY `xapp` (`token`),
  KEY `referer` (`referer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table structure for table `version`
--

DROP TABLE IF EXISTS `version_users`;
CREATE TABLE `version_users` (
  `idmajor` int(5) unsigned NOT NULL,
  `idminor` int(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `comment` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idmajor`,`idminor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;



insert into version_users (idmajor,idminor,comment) values (2,9,"AMBITDB users");

-- -----------------------------------------------------
-- Default users
-- -----------------------------------------------------
insert ignore into users values("admin",MD5("admin"));
insert ignore into users values("guest",MD5("guest"));
insert ignore into roles values("ambit_admin");
insert ignore into roles values("ambit_user");
insert ignore into roles value("ambit_datasetmgr");
insert ignore into roles value("ambit_modeller");
insert ignore into roles value("ambit_model_user");

insert ignore into user_roles values("admin","ambit_admin");
insert ignore into user_roles values("admin","ambit_user");

insert ignore into user_registration
SELECT user_name,now(),now(),concat("SYSTEM_",user_name),'confirmed' FROM users;

insert ignore into user values (null,'admin','','Admin','Administrator','AMBIT','http://ambit.sf.net','http://ambit.sf.net','','','admin',1);
insert ignore into user values (null,'guest','','Guest','Guest','AMBIT','http://ambit.sf.net','http://ambit.sf.net','','','guest',1);

insert ignore into policy values(null,"ambit_admin","/ambit2","/admin",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/user",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/algorithm",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/model",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/substance",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/dataset",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/bundle",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/dir",1,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/dir/bd",2,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/ui/updatesubstance1",2,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/ui/updatesubstancei5",2,1,1,1,1);
insert ignore into policy values(null,"ambit_admin","/ambit2","/ui/uploadsubstance",2,1,1,1,1);

insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/dataset",1,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/algorithm",1,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/model",1,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/substance",1,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/ui/updatesubstance1",2,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/ui/updatesubstancei5",2,1,1,1,1);
insert ignore into policy values(null,"ambit_datasetmgr","/ambit2","/ui/uploadsubstance",2,1,1,1,1);

insert ignore into policy values(null,"ambit_user","/ambit2","/dataset",1,1,0,0,0);
insert ignore into policy values(null,"ambit_user","/ambit2","/algorithm",1,1,0,0,0);
insert ignore into policy values(null,"ambit_user","/ambit2","/model",1,1,0,0,0);
insert ignore into policy values(null,"ambit_user","/ambit2","/substance",1,1,0,0,0);
insert ignore into policy values(null,"ambit_user","/ambit2","/bundle",1,1,1,1,1);

DROP PROCEDURE IF EXISTS `deleteUser`;
DELIMITER $$

CREATE PROCEDURE deleteUser(IN uname VARCHAR(16))
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL

BEGIN

delete FROM user_registration where user_name=uname;
delete FROM user_roles where user_name=uname;
delete FROM users where user_name=uname;
delete o FROM user_organisation o, user u where o.iduser=u.iduser and username=uname;
delete o FROM user_project o, user u where o.iduser=u.iduser and username=uname;
delete FROM user where username=uname;

END $$
DELIMITER ; 


drop view if exists ulist;
create view  ulist as
select `u`.`username` AS `username`,`u`.`firstname` AS `firstname`,`u`.`lastname` AS `lastname`,`u`.`institute` AS `institute`,`u`.`email` AS `email`,`u`.`keywords` AS `keywords`,group_concat(`r`.`role_name` separator ',') AS `role`,`n`.`created` AS `created`,`n`.`confirmed` AS `confirmed`,`n`.`status` AS `status` from ((`user` `u` join `user_roles` `r` on((`u`.`username` = `r`.`user_name`))) join `user_registration` `n` on((`u`.`username` = `n`.`user_name`))) 
where role_name != "ambit_user"
group by `u`.`username` order by `n`.`created` desc,`u`.`institute`,`u`.`username`;
