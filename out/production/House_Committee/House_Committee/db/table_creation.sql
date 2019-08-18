use house_committee;

CREATE TABLE `users` (
  `userId` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(45) NOT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `hashedPassword` varchar(64) DEFAULT NULL,
  `registrationDate` datetime DEFAULT NULL,
  `lastLogin` datetime DEFAULT NULL,
  `buildingNumber` int(11) DEFAULT NULL,
  `apartmentNumber` int(11) DEFAULT NULL,
  `role` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `userName_UNIQUE` (`userName`)
) ENGINE=InnoDB AUTO_INCREMENT=223 DEFAULT CHARSET=utf8;

CREATE TABLE `committees` (
  `idCommittee` int(11) NOT NULL AUTO_INCREMENT,
  `seniority` int(11) DEFAULT NULL,
  `userId` int(11) DEFAULT NULL,
  PRIMARY KEY (`idCommittee`),
  UNIQUE KEY `userId_UNIQUE` (`userId`),
  CONSTRAINT `fk_committees_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;

CREATE TABLE `tenants` (
  `idTenants` int(11) NOT NULL AUTO_INCREMENT,
  `monthlyPayment` float DEFAULT NULL,
  `userId` int(11) NOT NULL,
  PRIMARY KEY (`idTenants`),
  UNIQUE KEY `idTenants_UNIQUE` (`idTenants`),
  UNIQUE KEY `userId_UNIQUE` (`userId`),
  KEY `fk_user_tenant_idx` (`userId`),
  CONSTRAINT `fk_user_tenant` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;

CREATE TABLE `payments` (
  `paymentId` int(11) NOT NULL AUTO_INCREMENT,
  `paymentSum` decimal(10,0) DEFAULT NULL,
  `idTenants` int(11) DEFAULT NULL,
  `paymentDate` date DEFAULT NULL,
  PRIMARY KEY (`paymentId`),
  KEY `fk_tenant_payment_idx` (`idTenants`),
  CONSTRAINT `fk_tenant_payment` FOREIGN KEY (`idTenants`) REFERENCES `tenants` (`idTenants`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=133 DEFAULT CHARSET=utf8;
INSERT INTO `users` VALUES (82,'atta','gordon','ariel','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-17 00:00:00','2019-06-17 00:00:00',22,12,'Tenant'),(92,'aa','myy','arr','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-17 00:00:00','2019-06-17 00:00:00',22,13,'Tenant'),(102,'aass','ss','aa','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-17 19:22:54','2019-06-17 19:22:54',22,15,'Tenant'),(122,'sjjsjsjs','asasa','aajj','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-18 02:54:38','2019-06-18 02:54:38',22,11,'Committee'),(132,'djdnfnd','dhhd','dhhd','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-18 02:59:01','2019-06-18 02:59:01',22,23,'Committee'),(142,'agsdg','fjfj','fjfj','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-18 03:09:01','2019-06-18 03:09:01',22,22,'Committee'),(152,'att','uwuwu','uwuwu','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-18 03:35:38','2019-06-18 03:35:38',22,19,'Tenant'),(162,'djjd','kfkf','fkkf','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-18 03:37:52','2019-06-18 03:37:52',22,139,'Committee'),(172,'lsls','slls','ksskks','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-24 08:59:22','2019-06-24 08:59:22',21,22,NULL),(182,'slslls','sksk','lslsla','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-24 09:06:12','2019-06-24 09:06:12',11,11,NULL),(192,'kskksd','dlld','dkdk','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-24 09:12:30','2019-06-24 09:12:30',22,12320,NULL),(202,'oiii','owo','wllw','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-24 09:15:28','2019-06-24 09:15:28',22,22,NULL),(212,'dldk','2k','dk','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-24 09:17:48','2019-06-24 09:17:48',22,22,'Tenant'),(222,'mnm','ffk','llf','961b6dd3ede3cb8ecbaacbd68de040cd78eb2ed5889130cceb4c49268ea4d506','2019-06-24 09:18:55','2019-06-24 09:18:55',22,22,'Tenant');


INSERT INTO `committees` VALUES (2,12,122),(12,13,132),(22,12,142),(32,12,162),(42,11,172),(52,11,182);

INSERT INTO `tenants` VALUES (2,1343,82),(12,1560,92),(22,1504,102),(42,630,152),(52,210,192),(62,840,202),(72,840,222);
INSERT INTO `payments` VALUES (12,2343,2,'2019-06-18'),(22,2333,12,'2019-06-18'),(32,2311,22,'2019-06-18'),(42,2343,2,'2019-07-18'),(52,2323,12,'2019-07-18'),(62,1211,22,'2019-07-18'),(72,1000,42,'2019-06-25'),(82,3000,42,'2019-05-25'),(92,1000,42,'2019-04-25'),(102,1000,42,'2019-03-25'),(112,44,2,'2019-06-18'),(122,42,42,'2019-06-18'),(132,2992,42,'2018-12-22');
