delimiter $$

CREATE TABLE `apps` (
  `idapp` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `key` varchar(64) DEFAULT NULL,
  `referer` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`idapp`),
  UNIQUE KEY `xapp` (`key`),
  KEY `referer` (`referer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$


delimiter $$

CREATE TABLE `user_apps` (
  `iduser` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idapp` int(10) unsigned NOT NULL,
  PRIMARY KEY (`iduser`,`idapp`),
  KEY `FK_user_app_2` (`idapp`),
  CONSTRAINT `FK_user_app_1` FOREIGN KEY (`iduser`) REFERENCES `user` (`iduser`) ON UPDATE CASCADE,
  CONSTRAINT `FK_user_app_2` FOREIGN KEY (`idapp`) REFERENCES `apps` (`idapp`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8$$

insert into version_users (idmajor,idminor,comment) values (2,8,"AMBITDB users");