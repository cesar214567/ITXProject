# ITXProject

## Información general

Este proyecto consta de una aplicación que permite almacenar productos con su nombre, numero de ventas hechas, y el stock por cada tamaño de prenda y consultarlas con respecto a ciertos filtros envíados por el usuario.

## Descripción Técnica:

El proyecto ha sido realizado con el siguiente stack tecnológico:
- Spring Boot Webflux: Se ha utilizado la versión de webflux para poder obtener un mejor performance para realizar escalado horizontal de los microservicios ya que este aprovecha el uso de paralelismo.
- Mongodb: Se ha utilizado una base de datos NoSQL debido a su modelo de datos flexibles (facilidad de creación de datos no estructurados como objetos anidados y modificación de los mismos), la escalabilidad horizontal a través de sharding de los diferentes documentos, las consultas basadas en documentos, que permiten hacer consultas complejas sobre datos anidados y la alta disponibilidad y replicación que este posee para un futuro escalado.

## Ejecución del proyecto:

Para la ejecución del proyecto, se necesita realizar la descarga de docker-compose para poder levantar el cluster de mongodb en local. Dentro de la carpeta principal se encuentra un archivo docker.compose.yaml donde se encuentra esta configuración. Para ejecutarlo se necesita correr el siguiente comando:

```
sudo docker-compose up (en linux, en caso sea en windows o mac, revisar la documentación de docker-compose)
```
Ahora, desde la consola es necesario crear la collección y crear su índice para evitar que existan duplicados:
```
mongo
use mydatabase
db.createCollection("products")
show collections    #verificar si es que se creo correctamente
db.products.createIndex({ name: 1 }, { unique: true })
```

Después, para poblar la base de datos, se puede utilizar tanto el endpoint post para crear productos o ejecutar el archivo  

```
mongoimport --version # para revisar si se tiene el comando
mongoimport --uri="mongodb://localhost:27017" --db mydatabase --collection products --file /mydatabase.products.json --jsonArray
```


Una vez ejecutado, se puede correr el proyecto a través de IntellijIdea o ejecutar las pruebas usando el siguiente comando:
```
gradle test
```

## Coleccion de Postman

Además se tiene una colección de postman para poder realizar pruebas manuales/probar los servicios. El archivo se llama ITX.postman_collection.json .