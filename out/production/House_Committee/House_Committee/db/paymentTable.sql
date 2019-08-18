CREATE TABLE `payments` (
  `paymentId` int(11) NOT NULL,
  `paymentSum` decimal(10,0) DEFAULT NULL,
  `idTenants` int(11) DEFAULT NULL,
  `paymentDate` date DEFAULT NULL,
  PRIMARY KEY (`paymentId`),
  KEY `fk_tenant_payment_idx` (`idTenants`),
  CONSTRAINT `fk_tenant_payment` FOREIGN KEY (`idTenants`) REFERENCES `tenants` (`idTenants`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
