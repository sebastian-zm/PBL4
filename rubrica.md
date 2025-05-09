# Rúbrica

## RGI219 (Front-end)

**Nivel 0:**
- No cumple los estándares W3C o la validación no está documentada.
- No funciona correctamente en Chrome o Firefox, o la validación no está documentada.
- La parte front-end no tiene suficiente complejidad para demostrar que se han comprendido bien los conceptos aprendidos.

**Nivel 1:**
- Cumplir los estándares W3C:
  - Validar HTML y CSS utilizando la herramienta validadora W3C.
  - CUIDADO, no validar el código Thymeleaf, debéis validar el HTML después de que el servidor lo haya ejecutado.
  - Documentar los errores recibidos.
  - Documentar cómo se han solucionado.
- Validar que funciona con diferentes navegadores:
  - Al menos Chrome y Firefox.
  - ¿Todos los elementos funcionan en diferentes navegadores (móvil y escritorio)?
  - Escribir qué partes y cómo se han validado.

**Nivel 2:**
- Utilizar frameworks o librerías front-end:
  - (por ejemplo, Bootstrap, SemanticUI, MaterialUI, Leaflet, APIs...)
- Desarrollar una aplicación web completamente "responsive":
  - Adaptarse a diferentes pantallas.
  - Desarrollar con enfoque "Mobile First Design".
  - Documentar los logros y pruebas realizadas.

**Nivel 3:**
- Proporcionar una apariencia de alta calidad:
  - Utilizar iconos (por ejemplo, poder ver a primera vista qué se debe hacer).
  - Ser visualmente coherente y seguir una imagen de marca.
  - Sin problemas de "responsive".
  - Cargas suaves (sin golpes).
  - Limpieza de código.
  - Animaciones cuando añaden valor.
  - Personalizar los frameworks frontend para dar vuestro estilo personal (por ejemplo, ¿usáis Bootstrap directamente o le habéis hecho modificaciones?)
  - ...

## RGI218 (Back-end)

**Nivel 0:**
- No utiliza Spring framework u otro framework contrastado con el profesor.
- No utiliza ORM.
- No utiliza Thymeleaf-spring-layouts o Tiles u otra herramienta de layout contrastada con el profesor.
- La parte back-end no tiene suficiente complejidad para demostrar que se ha comprendido bien su funcionamiento.

**Nivel 1:**
- Utilizar Spring framework:
  - Spring Boot
  - Spring MVC
- Utilizar un ORM:
  - (por ejemplo, Spring JPA/Hibernate)
- Utilizar un framework de plantillas avanzado:
  - (por ejemplo, Thymeleaf-spring-layouts, Tiles...).
  - ¡CUIDADO! No es suficiente con usar include y replace.

**Nivel 2:**
- Resolver cuestiones de seguridad:
  - Almacenar contraseñas de usuarios encriptadas (por ejemplo, Bcrypt).
  - Permisos (por ejemplo, Spring Security).
  - HTTPS (por ejemplo, Let's Encrypt).
- Actualizar el contenido de la aplicación sin recargar la página:
  - (Ajax, Web Sockets)
- Gestionar adecuadamente relaciones '1 to N' y 'N to N' utilizando Hibernate.

**Nivel 3:**
- Complejidad (se evaluará la complejidad de la aplicación):
  - ¿Qué complejidad tiene la base de datos (número de tablas, relaciones, 1toN, NtoN...)?
  - ¿El código es limpio? (Dividido en paquetes, coherente en diferentes clases, funcionalidades bien distribuidas, baja complejidad de código...)
  - ¿La aplicación web solo tiene funcionalidades CRUD o tiene lógicas más complejas?
  - ¿Qué complejidad tienen los web sockets? (¿Gestiona permisos? ¿Se gestionan "salas"? ¿Se han implementado solo ejemplos que aparecen en los tutoriales o algo más significativo?)
