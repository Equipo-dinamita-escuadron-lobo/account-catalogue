# Account Catalogue Microservice

## Descripción

Este microservicio forma parte del sistema ContApp y se encarga de la gestión del catálogo de cuentas. Proporciona funcionalidades para crear, actualizar, eliminar y consultar cuentas bancarias y catálogos contables, integrándose con otros servicios a través de Spring Cloud.

## Tecnologías Utilizadas

- **Lenguaje**: Java 17
- **Framework**: Spring Boot 3.4.7
- **Microservicios**: Spring Cloud 2024.0.1
- **Base de Datos**: PostgreSQL (producción), H2 (pruebas)
- **Herramientas**:
  - Lombok para reducción de boilerplate
  - MapStruct para mapeo de objetos
  - Swagger/OpenAPI para documentación de API
  - JWT para autenticación
  - Jacoco para cobertura de pruebas
  - Doxygen para documentación técnica
  - Docker para contenedorización

## Requisitos Previos

- JDK 17 o superior
- Apache Maven 3.6+
- Docker y Docker Compose (opcional, para ejecución en contenedores)

## Instalación

1. Clona el repositorio:
   ```bash
   git clone <url-del-repositorio>
   cd account-catalogue
   ```

2. Instala las dependencias:
   ```bash
   mvn clean install
   ```

## Configuración

El microservicio utiliza perfiles de Spring para diferentes entornos:

- `dev`: Desarrollo (application-dev.yml)
- `test`: Pruebas (application-test.properties)
- `prod`: Producción (application.yml)

Configura las variables de entorno necesarias en los archivos de configuración, como conexiones a base de datos, JWT secrets, etc.

## Ejecución

### Ejecución Local

```bash
mvn spring-boot:run
```

El servicio estará disponible en `http://localhost:8080`.

### Ejecución con Docker

```bash
docker-compose up --build
```

## API y Documentación Interactiva

### Acceso a Swagger UI

Para utilizar la interfaz de Swagger, primero realiza una petición de ping para inicializar el contexto:

```
GET http://localhost:8080/api/accountCatalogue/test/ping
```

Luego, accede a la documentación interactiva de la API:

```
http://localhost:8080/swagger-ui/index.html#/
```

## Pruebas

### Ejecutar Pruebas Unitarias

```bash
mvn test
```

### Generar Reporte de Cobertura con Jacoco

```bash
mvn clean test
```

El reporte de cobertura se genera en `target/site/jacoco/index.html`. Abre este archivo en un navegador para visualizar el reporte detallado.

## Documentación Técnica

### Generar Documentación con Doxygen

```bash
doxygen Doxyfile
```

### Visualizar Documentación

La documentación generada se encuentra en `target/docs/html/index.html`. Para abrirla automáticamente en el navegador (Windows PowerShell):

```powershell
Start-Process target/docs/html/index.html
```

## Arquitectura

### Diagrama de Contexto
![](https://res.cloudinary.com/dtmtu3rkh/image/upload/v1718347245/Contexto.drawio_fays6y.svg)

### Diagrama de Contenedores
![](https://res.cloudinary.com/dtmtu3rkh/image/upload/v1718347271/Contonedores.drawio_vv9z8a.svg)

### Diagrama de Componentes
![](https://res.cloudinary.com/dtmtu3rkh/image/upload/v1718347136/Componentes.drawio_vbnfok.svg)

### Diagrama de Paquetes
![](https://res.cloudinary.com/dtmtu3rkh/image/upload/v1718348624/AccountCAatalogue.drawio_gkket5.svg)

## Estructura del Proyecto

```
account-catalogue/
├── src/
│   ├── main/
│   │   ├── java/com/
│   │   │   └── account_catalogue/  # Código fuente principal
│   │   └── resources/              # Configuraciones y recursos
│   └── test/
│       └── java/com/
│           └── account_catalogue/  # Pruebas unitarias
├── target/                         # Archivos compilados y reportes
├── Doxyfile                        # Configuración de Doxygen
├── pom.xml                         # Configuración de Maven
├── Dockerfile                      # Configuración de Docker
├── docker-compose.yml              # Configuración de Docker Compose
└── README.md                       # Este archivo
```

## Contribución

1. Crea una rama feature desde `develop`
2. Realiza tus cambios siguiendo las convenciones del proyecto
3. Ejecuta las pruebas y verifica la cobertura
4. Genera la documentación si has modificado código
5. Crea un Pull Request con una descripción detallada

## Convenciones de Código

- Sigue los principios SOLID
- Utiliza nombres descriptivos en inglés
- Mantén la cobertura de pruebas por encima del 90%
- Documenta el código con comentarios en español cuando sea necesario
- Ejecuta `mvn clean compile` antes de commits
