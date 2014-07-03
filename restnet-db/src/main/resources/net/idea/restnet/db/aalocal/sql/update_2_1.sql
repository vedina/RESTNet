
-- ----------------------------------------
-- authz support
-- ----------------------------------------
DROP TABLE IF EXISTS `policy`;
CREATE TABLE `policy` (
  `idpolicy` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'ambit_guest',
  `prefix` varchar(255) NOT NULL,
  `resource` varchar(255) NOT NULL,
  `mget` tinyint(1) NOT NULL DEFAULT '0',
  `mput` tinyint(1) NOT NULL DEFAULT '0',
  `mpost` tinyint(1) NOT NULL DEFAULT '0',
  `mdelete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idpolicy`),
  UNIQUE KEY `uri` (`resource`,`prefix`,`role_name`),
  KEY `fkrole1_idx` (`role_name`),
  KEY `get` (`mget`),
  KEY `put` (`mput`),
  KEY `post` (`mpost`),
  KEY `delete` (`mdelete`),
  CONSTRAINT `fkrole1` FOREIGN KEY (`role_name`) REFERENCES `roles` (`role_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into version (idmajor,idminor,comment) values (2,1,"Local AA schema");