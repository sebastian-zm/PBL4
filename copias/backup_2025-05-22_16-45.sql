-- MySQL dump 10.13  Distrib 8.0.42, for Linux (x86_64)
--
-- Host: db    Database: oposiciones
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ARBOL_ETIQUETAS`
--

DROP TABLE IF EXISTS `ARBOL_ETIQUETAS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ARBOL_ETIQUETAS` (
  `ancestroId` int NOT NULL,
  `descendienteId` int NOT NULL,
  `distancia` int NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ancestroId`,`descendienteId`),
  KEY `idx_arbol_etiquetas_ancestro` (`ancestroId`),
  KEY `idx_arbol_etiquetas_descendiente` (`descendienteId`),
  CONSTRAINT `ARBOL_ETIQUETAS_ibfk_1` FOREIGN KEY (`ancestroId`) REFERENCES `ETIQUETA` (`etiquetaId`),
  CONSTRAINT `ARBOL_ETIQUETAS_ibfk_2` FOREIGN KEY (`descendienteId`) REFERENCES `ETIQUETA` (`etiquetaId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ARBOL_ETIQUETAS`
--

LOCK TABLES `ARBOL_ETIQUETAS` WRITE;
/*!40000 ALTER TABLE `ARBOL_ETIQUETAS` DISABLE KEYS */;
INSERT INTO `ARBOL_ETIQUETAS` VALUES (1,1,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(3,3,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(5,5,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(7,7,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(7,8,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(7,9,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(7,10,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(8,8,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(9,9,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(10,10,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(11,11,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(11,12,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(11,13,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(11,14,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(11,15,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(12,12,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(13,13,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(14,14,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(15,15,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(16,16,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(18,18,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(18,19,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(19,19,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(20,20,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(22,22,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(22,23,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(22,24,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(23,23,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(24,24,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(25,25,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(27,27,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(29,29,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(29,30,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(29,31,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(29,32,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(29,33,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(30,30,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(31,31,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(32,32,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(33,33,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,34,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,35,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,36,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,37,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,38,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,39,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,40,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,41,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(34,42,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(35,35,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(36,36,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(37,37,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(38,38,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(39,39,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(40,40,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(41,41,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(42,42,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(43,43,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(43,44,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(43,45,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(44,44,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(45,45,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(46,46,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(46,47,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(46,48,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(46,49,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(47,47,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(48,48,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(49,49,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(50,50,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,52,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,53,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,54,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,55,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,56,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,57,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,58,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,59,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,60,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(52,61,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(53,53,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(54,54,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(55,55,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(56,56,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(57,57,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(58,58,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(59,59,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(60,60,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(61,61,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(62,62,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(62,63,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(62,64,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(62,65,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(62,66,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(62,67,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(63,63,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(64,64,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(65,65,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(66,66,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(67,67,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(68,68,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(68,69,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(68,70,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(68,71,1,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(69,69,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(70,70,0,'2025-05-20 12:27:12','2025-05-20 12:27:12'),(71,71,0,'2025-05-20 12:27:12','2025-05-20 12:27:12');
/*!40000 ALTER TABLE `ARBOL_ETIQUETAS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CONVOCATORIA`
--

DROP TABLE IF EXISTS `CONVOCATORIA`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CONVOCATORIA` (
  `convocatoriaId` int NOT NULL AUTO_INCREMENT,
  `boeId` varchar(255) NOT NULL,
  `titulo` text,
  `texto` longtext NOT NULL,
  `fechaPublicacion` datetime NOT NULL,
  `enlace` varchar(255) NOT NULL,
  `datosExtra` json DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`convocatoriaId`),
  UNIQUE KEY `boeId` (`boeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CONVOCATORIA`
--

LOCK TABLES `CONVOCATORIA` WRITE;
/*!40000 ALTER TABLE `CONVOCATORIA` DISABLE KEYS */;
/*!40000 ALTER TABLE `CONVOCATORIA` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ETIQUETA`
--

DROP TABLE IF EXISTS `ETIQUETA`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ETIQUETA` (
  `etiquetaId` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `formato` varchar(255) DEFAULT NULL COMMENT 'Formato para el nombre de la etiqueta en embeddings',
  PRIMARY KEY (`etiquetaId`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ETIQUETA`
--

LOCK TABLES `ETIQUETA` WRITE;
/*!40000 ALTER TABLE `ETIQUETA` DISABLE KEYS */;
INSERT INTO `ETIQUETA` VALUES (1,'Ceuta','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(3,'Madrid','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(5,'Murcia','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(7,'Aragón','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(8,'Huesca','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(9,'Teruel','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(10,'Zaragoza','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(11,'Galicia','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(12,'A Coruña','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(13,'Lugo','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(14,'Ourense','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(15,'Pontevedra','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(16,'Melilla','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(18,'Navarra','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(19,' Navarra','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(20,'Asturias','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(22,'Canarias','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(23,'Las Palmas','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(24,'Santa Cruz de Tenerife','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(25,'La Rioja','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(27,'Cantabria','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(29,'Cataluña','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(30,'Barcelona','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(31,'Girona','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(32,'Lleida','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(33,'Tarragona','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(34,'Andalucía','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(35,'Almería','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(36,'Cádiz','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(37,'Córdoba','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(38,'Granada','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(39,'Huelva','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(40,'Jaén','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(41,'Málaga','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(42,'Sevilla','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(43,'Extremadura','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(44,'Badajoz','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(45,'Cáceres','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(46,'País Vasco','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(47,'Araba/Álava','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(48,'Bizkaia','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(49,'Gipuzkoa','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(50,'Islas Baleares','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(52,'Castilla y León','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(53,'Ávila','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(54,'Burgos','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(55,'León','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(56,'Palencia','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(57,'Salamanca','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(58,'Segovia','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(59,'Soria','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(60,'Valladolid','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(61,'Zamora','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(62,'Castilla-La Mancha','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(63,'Albacete','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(64,'Ciudad Real','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(65,'Cuenca','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(66,'Guadalajara','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(67,'Toledo','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(68,'Comunidad Valenciana','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(69,'Alicante','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(70,'Castellón','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL),(71,'Valencia','2025-05-20 12:27:12','2025-05-20 12:27:12',NULL);
/*!40000 ALTER TABLE `ETIQUETA` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ETIQUETADO`
--

DROP TABLE IF EXISTS `ETIQUETADO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ETIQUETADO` (
  `convocatoriaId` int NOT NULL,
  `etiquetaId` int NOT NULL,
  `modeloId` int NOT NULL,
  `valoracion` int DEFAULT NULL,
  `confianza` float DEFAULT NULL,
  `status` int DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`convocatoriaId`,`etiquetaId`,`modeloId`),
  KEY `idx_etiquetado_convocatoria` (`convocatoriaId`),
  KEY `idx_etiquetado_etiqueta` (`etiquetaId`),
  KEY `idx_etiquetado_modelo` (`modeloId`),
  CONSTRAINT `ETIQUETADO_ibfk_1` FOREIGN KEY (`convocatoriaId`) REFERENCES `CONVOCATORIA` (`convocatoriaId`),
  CONSTRAINT `ETIQUETADO_ibfk_2` FOREIGN KEY (`etiquetaId`) REFERENCES `ETIQUETA` (`etiquetaId`),
  CONSTRAINT `ETIQUETADO_ibfk_3` FOREIGN KEY (`modeloId`) REFERENCES `MODELO` (`modeloId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ETIQUETADO`
--

LOCK TABLES `ETIQUETADO` WRITE;
/*!40000 ALTER TABLE `ETIQUETADO` DISABLE KEYS */;
/*!40000 ALTER TABLE `ETIQUETADO` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `FEEDBACK`
--

DROP TABLE IF EXISTS `FEEDBACK`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FEEDBACK` (
  `usuarioId` int NOT NULL,
  `convocatoriaId` int NOT NULL,
  `etiquetaId` int NOT NULL,
  `aprobado` tinyint(1) NOT NULL,
  `fecha` datetime NOT NULL,
  `comentario` varchar(255) DEFAULT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`usuarioId`,`convocatoriaId`,`etiquetaId`),
  KEY `idx_feedback_usuario` (`usuarioId`),
  KEY `idx_feedback_convocatoria_etiqueta` (`convocatoriaId`,`etiquetaId`),
  CONSTRAINT `FEEDBACK_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `USUARIO` (`usuarioId`),
  CONSTRAINT `FEEDBACK_ibfk_2` FOREIGN KEY (`convocatoriaId`, `etiquetaId`) REFERENCES `ETIQUETADO` (`convocatoriaId`, `etiquetaId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FEEDBACK`
--

LOCK TABLES `FEEDBACK` WRITE;
/*!40000 ALTER TABLE `FEEDBACK` DISABLE KEYS */;
/*!40000 ALTER TABLE `FEEDBACK` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODELO`
--

DROP TABLE IF EXISTS `MODELO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MODELO` (
  `modeloId` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`modeloId`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODELO`
--

LOCK TABLES `MODELO` WRITE;
/*!40000 ALTER TABLE `MODELO` DISABLE KEYS */;
INSERT INTO `MODELO` VALUES (1,'text-embedding-3-large','2025-05-20 12:27:07','2025-05-20 12:27:07');
/*!40000 ALTER TABLE `MODELO` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MODELO_EMBEDDING`
--

DROP TABLE IF EXISTS `MODELO_EMBEDDING`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MODELO_EMBEDDING` (
  `modeloId` int NOT NULL,
  `embedding` longblob NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `entidadId` int NOT NULL DEFAULT '0',
  `entidadTipo` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`modeloId`,`entidadId`,`entidadTipo`),
  CONSTRAINT `MODELO_EMBEDDING_ibfk_1` FOREIGN KEY (`modeloId`) REFERENCES `MODELO` (`modeloId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MODELO_EMBEDDING`
--

LOCK TABLES `MODELO_EMBEDDING` WRITE;
/*!40000 ALTER TABLE `MODELO_EMBEDDING` DISABLE KEYS */;
/*!40000 ALTER TABLE `MODELO_EMBEDDING` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SUSCRIPCION`
--

DROP TABLE IF EXISTS `SUSCRIPCION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SUSCRIPCION` (
  `suscripcionId` int NOT NULL AUTO_INCREMENT,
  `usuarioId` int NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`suscripcionId`),
  KEY `idx_suscripcion_usuario` (`usuarioId`),
  CONSTRAINT `SUSCRIPCION_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `USUARIO` (`usuarioId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SUSCRIPCION`
--

LOCK TABLES `SUSCRIPCION` WRITE;
/*!40000 ALTER TABLE `SUSCRIPCION` DISABLE KEYS */;
/*!40000 ALTER TABLE `SUSCRIPCION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SUSCRIPCION_ETIQUETA`
--

DROP TABLE IF EXISTS `SUSCRIPCION_ETIQUETA`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `SUSCRIPCION_ETIQUETA` (
  `suscripcionId` int NOT NULL,
  `etiquetaId` int NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`suscripcionId`,`etiquetaId`),
  KEY `idx_suscripcion_etiqueta_suscripcion` (`suscripcionId`),
  KEY `idx_suscripcion_etiqueta_etiqueta` (`etiquetaId`),
  CONSTRAINT `SUSCRIPCION_ETIQUETA_ibfk_1` FOREIGN KEY (`suscripcionId`) REFERENCES `SUSCRIPCION` (`suscripcionId`),
  CONSTRAINT `SUSCRIPCION_ETIQUETA_ibfk_2` FOREIGN KEY (`etiquetaId`) REFERENCES `ETIQUETA` (`etiquetaId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SUSCRIPCION_ETIQUETA`
--

LOCK TABLES `SUSCRIPCION_ETIQUETA` WRITE;
/*!40000 ALTER TABLE `SUSCRIPCION_ETIQUETA` DISABLE KEYS */;
/*!40000 ALTER TABLE `SUSCRIPCION_ETIQUETA` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `USUARIO`
--

DROP TABLE IF EXISTS `USUARIO`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `USUARIO` (
  `usuarioId` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `passwordHash` varchar(255) NOT NULL,
  `permisos` int NOT NULL,
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`usuarioId`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `USUARIO`
--

LOCK TABLES `USUARIO` WRITE;
/*!40000 ALTER TABLE `USUARIO` DISABLE KEYS */;
INSERT INTO `USUARIO` VALUES (1,'admin','admin@example.com','$2b$12$oPUHybRqS2PyiyUSEi5quugn2teoMszMjWY1/Tu/XSNZ5rjjpvQP2',3,'2025-05-20 12:27:02','2025-05-20 12:27:02'),(2,'Sebastián','sebastian@sebastian.software','$2b$10$8vmeqW55iwWCxl0jJaUlPOWIIKjhRByqMyIAzuEMbIYGkJrFQ3zIu',3,'2025-05-20 12:27:02','2025-05-20 12:27:02'),(3,'admin1','admin1@example.com','$2b$12$Lr61ADPe/pX.bA1Fl1MMvOPoevf6sh8MuAThPSPnO7nJp4hOy.S0C',3,'2025-05-20 12:27:02','2025-05-20 12:27:02');
/*!40000 ALTER TABLE `USUARIO` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'20250509105603','initial schema','SQL','V20250509105603__initial_schema.sql',7809617,'dev','2025-05-20 12:05:34',381,1),(2,'20250509160153','texto convocatoria longtext','SQL','V20250509160153__texto_convocatoria_longtext.sql',1850149679,'dev','2025-05-20 12:05:34',52,1),(3,'20250510235642','move subtree procedure','SQL','V20250510235642__move_subtree_procedure.sql',-1045940560,'dev','2025-05-20 12:05:34',7,1),(4,'20250511104234','modelo embedding','SQL','V20250511104234__modelo_embedding.sql',-1806258971,'dev','2025-05-20 12:05:34',39,1),(5,'20250511140200','titulo text','SQL','V20250511140200__titulo_text.sql',-1870869786,'dev','2025-05-20 12:05:34',49,1),(6,'20250511201526','insertar etiquetas procedimiento','SQL','V20250511201526__insertar_etiquetas_procedimiento.sql',1526544896,'dev','2025-05-20 12:05:34',13,1),(7,'20250514190000','remove descripcion add formato to etiqueta','SQL','V20250514190000__remove_descripcion_add_formato_to_etiqueta.sql',725934683,'dev','2025-05-20 12:05:34',66,1),(8,'20250515000010','polymorphic modelo embedding','SQL','V20250515000010__polymorphic_modelo_embedding.sql',1674358257,'dev','2025-05-20 12:05:34',153,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-22 14:45:54
