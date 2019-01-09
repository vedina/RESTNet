ALTER TABLE `users` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL  ;
ALTER TABLE `user` CHANGE COLUMN `username` `username` VARCHAR(32) NULL DEFAULT NULL COMMENT 'user name'  , CHANGE COLUMN `title` `title` VARCHAR(16) NULL DEFAULT NULL  ;
insert into version_users (idmajor,idminor,comment) values (2,6,"AMBITDB users");