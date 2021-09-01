LOCK TABLES 
    user_roles WRITE,
    user_registration WRITE,    
    users WRITE;
    
ALTER TABLE `user_roles` DROP FOREIGN KEY `userfk` ;
ALTER TABLE `user_roles` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL  , 
  ADD CONSTRAINT `userfk`
  FOREIGN KEY (`user_name` )
  REFERENCES `users` (`user_name` )
  ON UPDATE CASCADE;
  
ALTER TABLE `user_registration` DROP FOREIGN KEY `` ;
ALTER TABLE `user_registration` CHANGE COLUMN `user_name` `user_name` VARCHAR(32) NOT NULL  , 
  ADD CONSTRAINT `userregfk`
  FOREIGN KEY (`user_name` )
  REFERENCES `users` (`user_name` )
  ON UPDATE CASCADE;  

UNLOCK TABLES; 
insert into version_users (idmajor,idminor,comment) values (2,10,"AMBITDB users");

