------------------------------------------
-- Users
------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE  `users` (
  `user_name` varchar(16) CHARACTER SET latin1 NOT NULL,
  `user_pass` varchar(32) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

------------------------------------------
-- Roles
------------------------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE  `roles` (
  `role_name` varchar(16) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

------------------------------------------
-- Roles assigned to users
------------------------------------------

DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE  `user_roles` (
  `user_name` varchar(16) CHARACTER SET latin1 NOT NULL,
  `role_name` varchar(16) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`user_name`,`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

------------------------------------------
-- Registration status and confirmation codes
------------------------------------------
DROP TABLE IF EXISTS `user_registration`;
CREATE TABLE  `user_registration` (
  `user_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `confirmed` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `code` varchar(45) NOT NULL,
  `status` enum('disabled','commenced','confirmed') NOT NULL DEFAULT 'disabled',
  PRIMARY KEY (`user_name`),
  UNIQUE KEY `Index_2` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

------------------------------------------
-- Version
------------------------------------------
CREATE TABLE  `version` (
  `idmajor` int(5) unsigned NOT NULL,
  `idminor` int(5) unsigned NOT NULL,
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `comment` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`idmajor`,`idminor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

insert into version (idmajor,idminor,comment) values (2,0,"Local AA schema");