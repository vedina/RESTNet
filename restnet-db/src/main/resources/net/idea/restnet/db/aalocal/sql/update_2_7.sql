ALTER TABLE `users` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL  ;
ALTER TABLE `user` CHANGE COLUMN `username` `username` VARCHAR(32) NULL DEFAULT NULL COMMENT 'user name'  ;
ALTER TABLE `user_roles` DROP FOREIGN KEY `userfk` ;
ALTER TABLE `user_roles` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL  , 
  ADD CONSTRAINT `userfk`
  FOREIGN KEY (`user_name` )
  REFERENCES `users` (`user_name` )
  ON UPDATE CASCADE;

ALTER TABLE `user_registration` DROP FOREIGN KEY `` ;
ALTER TABLE `user_registration` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL  , 
  ADD CONSTRAINT `fk_regusers`
  FOREIGN KEY (`user_name` )
  REFERENCES `users` (`user_name` )
  ON DELETE CASCADE
  ON UPDATE CASCADE;

insert into version_users (idmajor,idminor,comment) values (2,7,"AMBITDB users");