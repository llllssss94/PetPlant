drop table if exists plant_log;
drop table if exists plant;
drop table if exists user;

CREATE TABLE `plant` (
  `id` varchar(50) NOT NULL,
  `owner_email` varchar(50) NOT NULL,
  `species` varchar(50) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `selected` int(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `fk_owner_email` (`owner_email`),
  CONSTRAINT `fk_owner_email` FOREIGN KEY (`owner_email`) REFERENCES `user` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `plant_log` (
  `plant_id` varchar(50) NOT NULL,
  `recorded_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `illumination_level` float DEFAULT NULL,
  `temperature_level` float DEFAULT NULL,
  `moisture_level` float DEFAULT NULL,
  PRIMARY KEY (`plant_id`,`recorded_date`),
  CONSTRAINT `plant_log_ibfk_1` FOREIGN KEY (`plant_id`) REFERENCES `plant` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `password` varchar(30) NOT NULL,
  `kakaotalk_id` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `constr_ID` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci