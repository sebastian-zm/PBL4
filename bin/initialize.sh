FLYWAY_CLEAN_DISABLED=false ./mvnw flyway:clean

./mvnw flyway:migrate

cat src/main/resources/db/seeds/* | mysql -h db -u root -proot oposiciones

./mvnw spring-boot:run -Dspring-boot.run.main-class=software.sebastian.oposiciones.EmbeddingOneTimeRunner

bin/generar_convocatorias_cli