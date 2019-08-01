ALTER TABLE `user_registration` CHANGE COLUMN `confirmed` `confirmed` TIMESTAMP NOT NULL DEFAULT '2019-01-01 00:00:00'  ;
ALTER TABLE `apps` CHANGE COLUMN `expire` `expire` TIMESTAMP NOT NULL DEFAULT '2019-01-01 00:00:00'  ;

insert into version_users (idmajor,idminor,comment) values (2,9,"AMBITDB users");

