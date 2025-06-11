USE oposiciones;
INSERT INTO USUARIO (email, nombre, apodo, passwordHash, permisos) VALUES
("admin@example.com", "admin", "admin", "$2b$12$oPUHybRqS2PyiyUSEi5quugn2teoMszMjWY1/Tu/XSNZ5rjjpvQP2", 3),
("sebastian@sebastian.software", "Sebastián", "Sebastián", "$2b$10$8vmeqW55iwWCxl0jJaUlPOWIIKjhRByqMyIAzuEMbIYGkJrFQ3zIu", 3),
("admin1@example.com", "admin1", "admin1", "$2b$12$Lr61ADPe/pX.bA1Fl1MMvOPoevf6sh8MuAThPSPnO7nJp4hOy.S0C", 3); -- contraseña: admin1

