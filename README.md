# Financial App - Backend REST para entidad financiera

## 1. Descripción del proyecto

Financial App es una aplicación backend desarrollada en Java con Spring Boot, orientada a la administración de clientes, productos financieros y transacciones para una entidad financiera.
El sistema permite registrar clientes, crear cuentas de ahorro o cuentas corrientes, realizar movimientos financieros y consultar estados de cuenta. Además, implementa reglas de negocio como validación de mayoría de edad, generación automática de números de cuenta, control de saldo y restricciones para cancelar cuentas o eliminar clientes con productos vinculados.
Este proyecto fue desarrollado como solución a una prueba técnica que solicita construir un backend REST usando Java, una base de datos SQL y una arquitectura por capas.

## 2. Tecnologías utilizadas

- Java 17.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- PostgreSQL.
- Maven.
- JUnit 5.
- Mockito.
- MockMvc.
- Lombok.
- Visual Studio Code.
- Postman.

## 3. Arquitectura del proyecto

El proyecto utiliza una arquitectura por capas, separando responsabilidades para mantener un código más organizado y fácil de mantener.

src/main/java/financial_app
│
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
└── service

Descripción de capas:

Capa	                Responsabilidad
controller	          Expone los endpoints REST.
service	              Contiene la lógica de negocio.
repository	          Se comunica con la base de datos usando JPA.
entity	              Representa las tablas de la base de datos.
dto	                  Recibe y organiza datos enviados en las peticiones.
enums	                Define valores constantes como tipos de cuenta, estados y tipos de transacción.
exception            	Maneja errores personalizados y respuestas de validación.

## 4. Módulos principales

El sistema está dividido en tres módulos principales:

Clientes.
Cuentas.
Transacciones financieras.

## 5. Módulo de clientes

Este módulo permite administrar la información de los clientes de la entidad financiera.

Funcionalidades:
Crear cliente.
Consultar todos los clientes.
Consultar cliente por ID.
Actualizar cliente.
Eliminar cliente.

Reglas de negocio:
Un cliente no puede ser menor de edad.
La fecha de creación se calcula automáticamente.
La fecha de modificación se actualiza automáticamente cuando se edita el cliente.
El correo electrónico debe tener un formato válido.
El nombre y el apellido deben tener mínimo 2 caracteres.
No se puede eliminar un cliente si tiene cuentas vinculadas.

Endpoint base
/api/clients

Ejemplo para crear cliente
POST http://localhost:8080/api/clients
{
  "identificationType": "CC",
  "identificationNumber": "1075289632",
  "names": "Emerson",
  "lastName": "Triviño",
  "email": "emerson@example.com",
  "birthDate": "2000-05-15"
}
## 6. Módulo de productos financieros / cuentas

Este módulo permite crear y administrar productos financieros asociados a clientes.

Tipos de cuenta
El sistema permite únicamente dos tipos de cuenta:

SAVINGS /
CHECKING

Tipo	          Descripción
SAVINGS	       Cuenta de ahorros.
CHECKING     	Cuenta corriente.

Estados de cuenta
ACTIVE
INACTIVE
CANCELLED

Estado   	     Descripción
ACTIVE	       Cuenta activa.
INACTIVE     	Cuenta inactiva.
CANCELLED   	Cuenta cancelada.

Reglas de negocio:
Una cuenta debe estar vinculada a un cliente existente.
El número de cuenta se genera automáticamente.
El número de cuenta debe tener 10 dígitos.
Las cuentas de ahorro inician con 53.
Las cuentas corrientes inician con 33.
Al crear una cuenta, queda activa por defecto.
Una cuenta de ahorros no puede tener saldo negativo.
Solo se puede cancelar una cuenta si su saldo es igual a cero.
El saldo se actualiza cuando se realiza una transacción exitosa.

Endpoint base
/api/accounts

Ejemplo para crear cuenta de ahorros:
POST http://localhost:8080/api/accounts
{
  "accountType": "SAVINGS",
  "balance": 100000,
  "gmfExempt": false,
  "clientId": 1
}
Ejemplo para crear cuenta corriente:
{
  "accountType": "CHECKING",
  "balance": 0,
  "gmfExempt": true,
  "clientId": 1
}
Cambiar estado de una cuenta:
PATCH http://localhost:8080/api/accounts/1/status?status=INACTIVE
Cancelar cuenta
PATCH http://localhost:8080/api/accounts/1/status?status=CANCELLED

solo se puede cancelar si el saldo es igual a 0.

## 7. Módulo de transacciones

Este módulo permite realizar movimientos financieros sobre las cuentas.

Tipos de transacción
DEPOSIT
WITHDRAWAL
TRANSFER

Tipo	          Descripción
DEPOSIT	       Consignación.
WITHDRAWAL	   Retiro.
TRANSFER	     Transferencia entre cuentas.

Reglas de negocio: 

Solo se pueden realizar transacciones sobre cuentas activas.
Las consignaciones requieren una cuenta destino.
Los retiros requieren una cuenta origen.
Las transferencias requieren cuenta origen y cuenta destino.
No se puede transferir a la misma cuenta.
Las transferencias solo se realizan entre cuentas existentes.
En una transferencia, se descuenta saldo de la cuenta origen y se suma a la cuenta destino.
En una cuenta de ahorros no se permite que el saldo quede negativo.

Endpoint base
/api/transactions

Ejemplo de consignación:
POST http://localhost:8080/api/transactions
{
  "transactionType": "DEPOSIT",
  "amount": 50000,
  "targetAccountId": 1,
  "description": "Consignación inicial"
}
Ejemplo de retiro:
{
  "transactionType": "WITHDRAWAL",
  "amount": 20000,
  "sourceAccountId": 1,
  "description": "Retiro en oficina"
}
Ejemplo de transferencia:
{
  "transactionType": "TRANSFER",
  "amount": 10000,
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "description": "Transferencia entre cuentas"
}

## 8. Consulta de estado de cuenta

El sistema permite consultar la información de una cuenta junto con sus movimientos financieros.

Endpoint
GET http://localhost:8080/api/accounts/1/statement

Ejemplo de respuesta:
{
  "accountId": 1,
  "accountNumber": "5312345678",
  "accountType": "SAVINGS",
  "status": "ACTIVE",
  "balance": 80000,
  "transactions": 
    {
      "id": 1,
      "transactionType": "DEPOSIT",
      "amount": 50000,
      "description": "Consignación inicial"
    }
  
}

## 9. Configuración de base de datos

El proyecto utiliza PostgreSQL.

CREATE DATABASE financial_app_db;
Configurar conexión 
archivo:

src/main/resources/application.properties

configuración:
spring.application.name=financial-app
spring.datasource.url=jdbc:postgresql://localhost:5432/financial_app_db
spring.datasource.username=postgres
spring.datasource.password=emerson
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
server.port=8080

## 10. Cómo ejecutar los tests

Para correr las pruebas unitarias:

mvn test

## 11. Pruebas implementadas

El proyecto incluye pruebas unitarias para las capas Service y Controller.

Tests de servicios:
ClientServiceTest
AccountServiceTest
TransactionServiceTest

Tests de controladores:
ClientControllerTest
AccountControllerTest
TransactionControllerTest

Resultado esperado:
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS

Esto indica que todas las pruebas fueron ejecutadas correctamente.

## 12. Endpoints principales
Clientes:
Método	                        Endpoint	           Descripción
POST	                         /api/clients          	Crear cliente.
GET                          	/api/clients	          Listar clientes.
GET	                         /api/clients/{id}	      Consultar cliente por ID.
PUT	                         /api/clients/{id}	      Actualizar cliente.
DELETE	                    /api/clients/{id}       	Eliminar cliente.

Cuentas:
Método	                                  Endpoint	                      Descripción
POST	                                   /api/accounts	                  Crear cuenta.
GET	                                    /api/accounts	                    Listar cuentas.
GET	                                   /api/accounts/{id}                	Consultar cuenta por ID.
PATCH                                  	/api/accounts/{id}/status	        Cambiar estado de cuenta.
GET                                   	/api/accounts/{id}/statement	    Consultar estado de cuenta.
DELETE	                               /api/accounts/{id}	                Eliminar cuenta.

Transacciones:
Método                                    	Endpoint	                                Descripción
POST	                                     /api/transactions	                       Crear transacción.
GET	                                      /api/transactions                         	Listar transacciones.
GET	                                     /api/transactions/{id}	                     Consultar transacción por ID.
GET                                      	/api/transactions/account/{accountId}	      Consultar transacciones de una cuenta.


## 13. Decisiones técnicas
Use esta arquitectura por capas porque permite separar responsabilidades, los controladores reciben las peticiones, los servicios manejan la lógica de negocio y los repositorios se encargan del acceso a datos.

Uso de DTOs:
Se utilizan DTOs para recibir los datos de entrada en las peticiones REST. Esto evita exponer directamente toda la estructura interna de las entidades.

Uso de enums:
Se utilizan enums para controlar valores permitidos como tipos de cuenta, estados de cuenta y tipos de transacción. Esto reduce errores y facilita la validación.

Uso de excepciones personalizadas:
Se implementaron excepciones personalizadas para manejar errores de negocio, como clientes menores de edad, cuentas inexistentes o movimientos inválidos.

Uso de pruebas unitarias:
Se implementaron pruebas unitarias para validar el comportamiento de servicios y controladores, asegurando que las reglas principales del sistema funcionen correctamente.

## 14. Autor
Desarrollado por:

emerson triviño trujillo

## 15. Estado del proyecto

Proyecto finalizado con los requerimientos principales implementados:

CRUD de clientes.
Gestión de cuentas.
Transacciones financieras.
Consulta de estados de cuenta.
Validaciones de negocio.
Persistencia en PostgreSQL.
Pruebas unitarias.
Arquitectura por capas.








