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
-- Users
------------------------------------------
CREATE TABLE  `users` (
  `user_name` varchar(16) CHARACTER SET latin1 NOT NULL,
  `user_pass` varchar(32) CHARACTER SET latin1 NOT NULL,
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

------------------------------------------
-- For test purposes
------------------------------------------
insert into roles values ("user");
insert into roles values ("admin");
insert into users (user_name,user_pass) values ("test",md5("test"));
insert into users (user_name,user_pass) values ("admin",md5("admin"));
insert into user_roles values ("test","user");
insert into user_roles values ("admin","admin");