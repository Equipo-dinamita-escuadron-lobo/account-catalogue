# Microservicio de Catálogo de Cuentas (Account Catalogue)

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.12-orange)](https://www.rabbitmq.com/)
[![Apache POI](https://img.shields.io/badge/Apache%20POI-5.2.5-green)](https://poi.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)

Sistema contable integral que administra el catálogo completo de cuentas, procesamiento de asientos contables, gestión bancaria, métodos de pago e impuestos. Microservicio crítico del ecosistema CONTAPP con arquitectura modular avanzada, procesamiento asíncrono de eventos financieros y soporte completo para multi-tenancy empresarial.

## 📋 Tabla de Contenidos

- [Características Principales](#-características-principales)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Ejecución](#-ejecución)
- [API Documentation](#-api-documentation)
- [Módulos](#-módulos)
- [Testing](#-testing)
- [CI/CD](#-cicd)
- [Contribución](#-contribución)

## 🚀 Características Principales

### Sistema Contable Integral
- **Catálogo de Cuentas**: Gestión jerárquica completa del plan de cuentas contables
- **Asientos Contables**: Procesamiento automático de movimientos financieros
- **Facturas y Recibos**: Gestión completa del ciclo de ingresos y egresos
- **Amortizaciones**: Sistema de castigos y provisiones automáticas
- **Multi-tenancy Empresarial**: Aislamiento total por empresa con contexto dinámico

### Gestión Bancaria y Financiera
- **Cuentas Bancarias**: Administración completa de cuentas por empresa
- **Bancos**: Catálogo maestro de instituciones financieras
- **Métodos de Pago**: Formas de pago configurables por empresa
- **Impuestos**: Sistema completo de tasas impositivas (IVA, ICA, etc.)

### Procesamiento Asíncrono Avanzado
- **Eventos RabbitMQ**: 6 exchanges dedicados para diferentes tipos de eventos
- **Dead Letter Queues**: Sistema de reintento y recuperación de mensajes fallidos
- **Message Listeners**: Procesamiento automático de eventos contables
- **Circuit Breaker**: Manejo robusto de fallos en mensajería

### Operaciones Masivas y Reportes
- **Import/Export Excel**: Procesamiento masivo de catálogos y movimientos
- **Reportes Jerárquicos**: Estructuras arbóreas de cuentas y centros de costo
- **Templates Dinámicos**: Plantillas configurables por empresa
- **Auditoría Completa**: Tracking de todas las operaciones financieras

### Arquitectura Empresarial
- **Arquitectura Hexagonal**: Separación clara entre dominio e infraestructura
- **Clean Architecture**: Capas bien definidas con responsabilidades específicas
- **Domain-Driven Design**: Modelos ricos con lógica de negocio compleja
- **Event Sourcing**: Eventos financieros como fuente de verdad
- **CQRS Pattern**: Separación de comandos y queries para optimización

## 🏗️ Arquitectura

El proyecto implementa una **arquitectura hexagonal compleja** con múltiples módulos especializados:

### Estructura Modular por Dominios
```
account-catalogue/
├── catalogue/               # 📊 Catálogo de Cuentas Contables
│   ├── domain/             # Modelos de cuentas, jerarquía, naturaleza
│   ├── application/        # Casos de uso: CRUD, jerarquía, export
│   └── infraestructure/    # JPA, RabbitMQ, Excel processing
├── accounting/             # 💼 Contabilidad Financiera
│   ├── domain/             # Asientos, movimientos, balances
│   ├── application/        # Procesamiento de facturas, recibos
│   └── infraestructure/    # Message brokers, persistencia
├── bankAccounts/           # 🏦 Cuentas Bancarias
├── banks/                  # 🏛️ Instituciones Financieras
├── paymentMethods/         # 💳 Métodos de Pago
└── taxes/                  # 💰 Sistema Tributario
```

### Arquitectura por Capas (Hexagonal)
Cada módulo sigue el patrón de **arquitectura hexagonal**:

```
Módulo/
├── domain/                  # 🎯 Reglas de Negocio Core
│   ├── models/             # Entidades del dominio (Account, Entry, Tax)
│   ├── services/           # Servicios de dominio puro
│   ├── enums/              # Enumeraciones de negocio
│   └── messageBroker/      # Eventos de dominio (opcional)
├── application/            # 🔄 Casos de Uso
│   ├── input/ports/        # Interfaces de entrada (Use Cases)
│   ├── output/ports/       # Interfaces de salida (Repos, External)
│   └── services/           # Implementación de casos de uso
└── infraestructure/        # 🔌 Adaptadores Externos
    ├── input/              # REST Controllers, Message Listeners
    ├── output/             # JPA Repositories, RabbitMQ Publishers
    └── config/             # Spring Configurations
```

### Arquitectura de Mensajería (Event-Driven)

#### Exchanges y Queues Principales
```javascript
// Catálogo de Cuentas
account.used.exchange        // FanoutExchange para uso de cuentas
account.used.queue           // Queue para tracking de cuentas utilizadas

// Cuentas Bancarias
bankaccount.used.exchange    // FanoutExchange para cuentas bancarias
bankaccount.used.queue       // Queue para uso de cuentas bancarias

// Métodos de Pago
paymentmethod.used.exchange  // FanoutExchange para métodos de pago
paymentmethod.used.queue     // Queue para uso de métodos

// Impuestos
tax.used.exchange           // FanoutExchange para tasas impositivas
tax.used.queue              // Queue para uso de impuestos

// Contabilidad - Facturas
invoice.exchange            // DirectExchange para facturas
invoice.accounting.queue    // Queue principal para procesamiento
invoice.accounting.retry.queue // Queue de reintento
invoice.accounting.dlx      // Dead Letter Exchange

// Contabilidad - Recibos
receipt.exchange            // DirectExchange para recibos
receipt.accounting.queue    // Queue principal
receipt.accounting.retry.queue // Queue de reintento
receipt.accounting.dlx      // Dead Letter Exchange

// Contabilidad - Amortizaciones
writeoff.exchange          // DirectExchange para castigos
writeoff.accounting.queue  // Queue principal
writeoff.accounting.retry.queue // Queue de reintento
writeoff.accounting.dlx    // Dead Letter Exchange
```

### Características Arquitecturales Avanzadas
- **SOLID Principles**: Implementación estricta con alta cohesión y bajo acoplamiento
- **Dependency Inversion**: Puertos y adaptadores para inversión de dependencias
- **Domain Events**: Eventos ricos del dominio para comunicación entre módulos
- **Saga Pattern**: Transacciones distribuidas para operaciones complejas
- **Eventual Consistency**: Consistencia eventual en operaciones asíncronas

## 🛠️ Tecnologías

### Framework & Runtime
- **Java 17**: Lenguaje con últimas características LTS y performance optimizada
- **Spring Boot 3.4.7**: Framework principal con Spring Framework 6.x
- **Spring Cloud 2024.0.1**: Microservicios enterprise con configuración avanzada

### Persistencia de Datos
- **Spring Data JPA**: Abstracción ORM con Hibernate para mapeo objeto-relacional
- **PostgreSQL**: Base de datos relacional para datos transaccionales críticos
- **H2 Database**: Base de datos en memoria para testing y desarrollo rápido
- **HikariCP**: Connection pooling avanzado con métricas y monitoreo

### Mensajería y Comunicación Asíncrona
- **RabbitMQ**: Message broker enterprise con alta disponibilidad
- **Spring AMQP**: Cliente completo con configuración de listeners y publishers
- **Dead Letter Queues**: Sistema de recuperación automática de mensajes fallidos
- **Retry Mechanisms**: Estrategias de reintento configurables por tipo de mensaje

### Seguridad y Autenticación
- **Spring Security 6.x**: Framework de seguridad enterprise-grade
- **OAuth2/OpenID Connect**: Protocolo estándar para autenticación federada
- **JWT (JJWT 0.9.1)**: Tokens seguros para autorización stateless
- **Keycloak Integration**: Proveedor de identidad centralizado

### Procesamiento de Documentos y Excel
- **Apache POI 5.2.5**: Librería completa para manipulación de archivos Excel
- **Streaming API**: Procesamiento eficiente de archivos grandes
- **Template Engine**: Generación dinámica de reportes Excel
- **Validation Framework**: Validación robusta de formatos y datos

### Testing y Calidad de Código
- **JUnit 5**: Framework moderno de testing con Jupiter
- **Mockito**: Framework de mocking para pruebas unitarias
- **JaCoCo**: Cobertura de código con reportes detallados (mínimo configurable)
- **Spring Boot Test**: Testing integrado para aplicaciones Spring

### DevOps y Despliegue
- **Docker**: Contenedorización completa con multi-stage builds
- **Maven Wrapper**: Build consistente sin instalación de Maven
- **GitHub Actions**: CI/CD automatizado con workflows reutilizables
- **Spring Boot Actuator**: Health checks, métricas y monitoreo

### Utilidades y Herramientas
- **MapStruct 1.6.0.Beta1**: Mapeo type-safe entre objetos
- **Lombok**: Reducción significativa de boilerplate code
- **SpringDoc OpenAPI 2.5.0**: Documentación automática de APIs REST
- **PaginationHelper**: Utilidad común para paginación consistente
- **StringStandardizationUtils**: Normalización de textos y códigos

## 📋 Requisitos Previos

### Sistema Operativo
- **Windows 10/11**, **macOS 12+**, o **Linux (Ubuntu 20.04+, CentOS 8+)**
- **Arquitectura**: x64/AMD64 (requerido para Java 17)
- **Memoria RAM**: Mínimo 4GB, recomendado 8GB+

### Software Base
- **Java JDK**: 17 LTS obligatorio (OpenJDK o Oracle JDK)
- **Maven**: 3.8+ incluido (mvnw wrapper)
- **Git**: 2.30+ para control de versiones distribuido

### Servicios Externos (para desarrollo completo)
- **PostgreSQL**: 15+ para base de datos principal
- **RabbitMQ**: 3.12+ para mensajería asíncrona
- **Keycloak**: 20+ para gestión de identidad
- **Eureka Server**: Para registro de servicios

### Recursos del Sistema
- **CPU**: 2+ cores para procesamiento concurrente
- **Disco**: 1GB+ libres para código, dependencias y datos
- **Red**: Conexión estable para dependencias Maven y servicios externos

## ⚙️ Instalación y Configuración

### 1. Clonación del Repositorio
```bash
# Clonación del repositorio
git clone <repository-url>
cd account-catalogue

# Verificación de Java
java -version  # Debe mostrar Java 17
```

### 2. Configuración del Entorno

#### Variables de Entorno Esenciales
```bash
# Base de Datos PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/catalogue
DB_USER=postgres
DB_PASSWORD=your_secure_password
DB_DRIVER=org.postgresql.Driver
DB_HIBERNATE_DDL_AUTO=create-drop

# RabbitMQ - Configuración Principal
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# RabbitMQ - Configuración Avanzada
RABBITMQ_CONNECTION_POOL_SIZE=5
RABBITMQ_CHANNEL_POOL_SIZE=25
RABBITMQ_CHANNEL_CHECKOUT_TIMEOUT=5000
RABBITMQ_PREFETCH=10
RABBITMQ_CONCURRENCY=1
RABBITMQ_MAX_CONCURRENCY=5
RABBITMQ_RETRY_INITIAL_INTERVAL=1000
RABBITMQ_RETRY_MAX_ATTEMPTS=3
RABBITMQ_RETRY_MAX_INTERVAL=10000
RABBITMQ_RECEIVE_TIMEOUT=5000

# Seguridad - Keycloak
JWT_ISSUER_URI=http://localhost:8090/auth/realms/contapp-realm
JWT_JWK_SET_URI=http://localhost:8090/auth/realms/contapp-realm/protocol/openid-connect/certs
JWT_PRINCIPAL_ATTR=preferred_username
JWT_RESOURCE_ID=account-catalogue-service

# Servicio - Eureka
EUREKA_URL=http://localhost:8761/eureka/
INSTANCE_HOSTNAME=localhost

# Aplicación
PORT=8080
PROFILE=dev
SPRING_APPLICATION_NAME=catalogue
```

#### Variables de Entorno para Producción
```bash
# Configuración de Producción
PROFILE=prod
DB_HIBERNATE_DDL_AUTO=validate
LOGGING_LEVEL_ROOT=INFO
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics

# Configuración de Pool de Conexiones
DB_HIKARI_MAXIMUM_POOL_SIZE=20
DB_HIKARI_MINIMUM_IDLE=5
DB_HIKARI_CONNECTION_TIMEOUT=20000
DB_HIKARI_IDLE_TIMEOUT=300000
DB_HIKARI_MAX_LIFETIME=1200000
```

### 3. Configuración de Base de Datos

#### Esquema de Base de Datos
El sistema maneja múltiples esquemas por módulo:
- **account_catalogue**: Catálogo de cuentas contables
- **accounting_entries**: Asientos y movimientos contables
- **bank_accounts**: Cuentas bancarias por empresa
- **banks**: Catálogo maestro de bancos
- **payment_methods**: Métodos de pago
- **taxes**: Configuración impositiva

#### Inicialización Automática
```sql
-- El sistema crea automáticamente las tablas mediante Hibernate
-- DDL-AUTO: create-drop (desarrollo), validate (producción)
-- Datos maestros se cargan vía scripts SQL o servicios de inicialización
```

### 4. Configuración de RabbitMQ

#### Topología de Exchanges y Queues
```javascript
// Configuración de Exchanges (FanoutExchange para broadcasts)
const exchanges = {
  'account.used.exchange': { type: 'fanout', durable: true },
  'bankaccount.used.exchange': { type: 'fanout', durable: true },
  'paymentmethod.used.exchange': { type: 'fanout', durable: true },
  'tax.used.exchange': { type: 'fanout', durable: true },
  'invoice.exchange': { type: 'direct', durable: true },
  'receipt.exchange': { type: 'direct', durable: true },
  'writeoff.exchange': { type: 'direct', durable: true }
};

// Configuración de Queues con Dead Letter Queues
const queues = {
  // Queues principales
  'account.used.queue': { durable: true },
  'bankaccount.used.queue': { durable: true },
  'paymentmethod.used.queue': { durable: true },
  'tax.used.queue': { durable: true },

  // Queues contables con DLQ
  'invoice.accounting.queue': { dlx: 'invoice.accounting.dlx' },
  'receipt.accounting.queue': { dlx: 'receipt.accounting.dlx' },
  'writeoff.accounting.queue': { dlx: 'writeoff.accounting.dlx' },

  // Queues de reintento
  'invoice.accounting.retry.queue': { ttl: 60000 },
  'receipt.accounting.retry.queue': { ttl: 60000 },
  'writeoff.accounting.retry.queue': { ttl: 60000 }
};
```

## 🚀 Ejecución

### Desarrollo Local

#### Opción 1: Spring Boot Directo
```bash
# Compilación completa
./mvnw clean compile

# Ejecución con perfil de desarrollo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Ejecución con variables específicas
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -DDB_URL=jdbc:postgresql://localhost:5432/catalogue
```

#### Opción 2: Maven con JaCoCo
```bash
# Build completo con tests y cobertura
./mvnw clean verify

# Solo tests unitarios
./mvnw test

# Tests con reporte de cobertura
./mvnw clean test jacoco:report
```

#### Opción 3: Docker Standalone
```bash
# Build de imagen
docker build -t account-catalogue .

# Ejecución con variables de entorno
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/catalogue \
  -e DB_USER=postgres \
  -e DB_PASSWORD=password \
  -e RABBITMQ_HOST=host.docker.internal \
  account-catalogue
```

### Producción

#### Health Checks y Monitoreo
```bash
# Health check básico
curl http://localhost:8080/actuator/health

# Información detallada del servicio
curl http://localhost:8080/actuator/info

# Métricas de aplicación
curl http://localhost:8080/actuator/metrics

# Métricas de JVM
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

## 📚 API Documentation

### OpenAPI Specification JSON
```
http://localhost:8080/api-docs
```

### Postman Collections
Archivos disponibles en `.postman/`:
- `account-catalogue.postman_collection.json`: Suite completa de APIs
- `account-catalogue.postman_environment.json`: Variables de entorno

### Endpoints Principales por Módulo

#### Catálogo de Cuentas (`/api/accountCatalogue`)
```http
# Gestión de Cuentas
POST   /api/accountCatalogue/                    # Crear cuenta
PUT    /api/accountCatalogue/{id}                # Actualizar cuenta
GET    /api/accountCatalogue/accountByCode/{code}/{idEnterprise} # Buscar por código
DELETE /api/accountCatalogue/{id}/{idEnterprise} # Eliminar cuenta

# Jerarquía y Estructuras
GET    /api/accountCatalogue/tree/{code}/{idEnterprise} # Árbol jerárquico
GET    /api/accountCatalogue/trees/{idEnterprise} # Todos los árboles
GET    /api/accountCatalogue/search/{idEnterprise} # Búsqueda avanzada
GET    /api/accountCatalogue/auxiliary/{idEnterprise} # Cuentas auxiliares
GET    /api/accountCatalogue/auxiliary/crossing/{idEnterprise} # Cruce contable

# Import/Export
GET    /api/accountCatalogue/template/excel       # Plantilla Excel
GET    /api/accountCatalogue/export/excel         # Exportar catálogo
POST   /api/accountCatalogue/import/excel         # Importar catálogo
GET    /api/accountCatalogue/import/status/{jobId} # Estado de importación
GET    /api/accountCatalogue/export/download/{jobId} # Descargar exportación
```

#### Contabilidad (`/api/accountCatalogue/accounting`)
```http
# Asientos Contables
GET    /api/accountCatalogue/accounting/entries/{id} # Asiento por ID
GET    /api/accountCatalogue/accounting/entries/by-receipt/{receiptId} # Por recibo
GET    /api/accountCatalogue/accounting/entries/by-source/{sourceId}/{type} # Por documento
GET    /api/accountCatalogue/accounting/movements/by-account/{accountId} # Movimientos por cuenta
GET    /api/accountCatalogue/accounting/movements/by-third-party/{thirdId} # Por tercero
```

#### Cartera (`/api/accountCatalogue/portfolio`)
```http
# Reportes de Cartera
GET    /api/accountCatalogue/portfolio/clients-summary # Resumen de clientes
GET    /api/accountCatalogue/portfolio/invoices/by-client/{clientId} # Facturas por cliente
GET    /api/accountCatalogue/portfolio/receipts/by-invoice/{invoiceId} # Recibos por factura
GET    /api/accountCatalogue/portfolio/aging-report/by-client/{clientId} # Reporte de antigüedad
```

#### Cuentas Bancarias (`/api/accountCatalogue/bank-accounts`)
```http
POST   /api/accountCatalogue/bank-accounts/create
PUT    /api/accountCatalogue/bank-accounts/update
GET    /api/accountCatalogue/bank-accounts/findAll/{enterpriseId}
GET    /api/accountCatalogue/bank-accounts/findAllActive/{enterpriseId}
DELETE /api/accountCatalogue/bank-accounts/delete/{id}/{enterpriseId}
```

#### Bancos (`/api/accountCatalogue/banks`)
```http
POST   /api/accountCatalogue/banks/create
PUT    /api/accountCatalogue/banks/update
GET    /api/accountCatalogue/banks/findAll/{enterpriseId}
GET    /api/accountCatalogue/banks/findAllActive/{enterpriseId}
DELETE /api/accountCatalogue/banks/delete/{id}/{enterpriseId}
```

#### Métodos de Pago (`/api/accountCatalogue/payment-methods`)
```http
POST   /api/accountCatalogue/payment-methods/create
PUT    /api/accountCatalogue/payment-methods/update
GET    /api/accountCatalogue/payment-methods/findAll/{enterpriseId}
GET    /api/accountCatalogue/payment-methods/findAllActive/{enterpriseId}
DELETE /api/accountCatalogue/payment-methods/delete/{id}/{enterpriseId}
```

#### Impuestos (`/api/tax`)
```http
POST   /api/tax/                                # Crear impuesto
GET    /api/tax/{code}/{idEnterprise}           # Buscar por código
GET    /api/tax/taxes/{idEnterprise}            # Todos los impuestos
GET    /api/tax/active/{idEnterprise}           # Impuestos activos
PUT    /api/tax/{id}                            # Actualizar impuesto
DELETE /api/tax/{id}/{enterpriseId}             # Eliminar impuesto
```

## 🎯 Módulos

### 1. 📊 Catálogo de Cuentas (Catalogue)

**Entidades Principales:**
- **AccountCatalogue**: Cuenta contable con jerarquía completa
- **Características**: Naturaleza (Débito/Crédito), estado financiero, clasificación
- **Jerarquía**: Estructura padre-hijo para plan de cuentas
- **Impuestos**: Asociación con tasas de venta y compra

**Funcionalidades:**
- Gestión jerárquica del plan de cuentas
- Búsqueda por código y descripción
- Árboles contables completos
- Cuentas auxiliares y de cruce
- Import/export masivo vía Excel

### 2. 💼 Contabilidad (Accounting)

**Entidades Principales:**
- **AccountingEntry**: Asiento contable con movimientos
- **AccountingMovement**: Movimientos débito/crédito
- **Receipt**: Recibo de caja con detalles
- **InvoiceReplica**: Réplica de factura para contabilidad
- **PortfolioWriteOff**: Amortización/castigo de cartera

**Funcionalidades:**
- Procesamiento automático de asientos
- Balance calculation strategies
- Integración con facturas y recibos
- Amortizaciones y castigos
- Auditoría completa de movimientos

### 3. 🏦 Cuentas Bancarias (Bank Accounts)

**Entidades Principales:**
- **BankAccount**: Cuenta bancaria con tipo y estado
- **Tipos**: Ahorros, Corriente, etc.
- **Estados**: Activa, Inactiva, Bloqueada

**Funcionalidades:**
- Gestión completa por empresa
- Validaciones bancarias
- Tracking de uso
- Estados dinámicos

### 4. 🏛️ Bancos (Banks)

**Entidades Principales:**
- **Bank**: Institución financiera
- **Estados**: Activo/Inactivo

**Funcionalidades:**
- Catálogo maestro de bancos
- Asociación con cuentas
- Estados configurables

### 5. 💳 Métodos de Pago (Payment Methods)

**Entidades Principales:**
- **PaymentMethod**: Forma de pago
- **Estados**: Activo/Inactivo

**Funcionalidades:**
- Configuración de métodos por empresa
- Validaciones de uso
- Estados dinámicos

### 6. 💰 Impuestos (Taxes)

**Entidades Principales:**
- **Tax**: Tasa impositiva (IVA, ICA, etc.)
- **Porcentajes**: Tasas configurables
- **Estados**: Activo/Inactivo

**Funcionalidades:**
- Gestión de tasas tributarias
- Asociación con cuentas contables
- Validaciones de porcentajes
- Estados configurables

### Características Comunes

#### Multi-tenancy Empresarial
- **Aislamiento Total**: Datos completamente separados por `enterpriseId`
- **Contexto Automático**: Resolver dinámico de tenant
- **Validaciones Cruzadas**: Consistencia dentro de cada empresa

#### RabbitMQ Integration
- **Eventos por Módulo**: Cada módulo tiene su exchange dedicado
- **Tracking de Uso**: Eventos para monitorear utilización de entidades
- **Dead Letter Queues**: Recuperación de mensajes fallidos

#### Operaciones CRUD Estándar
- **Create**: Creación con validaciones de negocio
- **Read**: Consultas paginadas y filtradas
- **Update**: Modificación con control de cambios
- **Delete**: Eliminación segura con verificación de uso

#### Manejo de Errores
- **35+ Tipos de Excepciones**: Específicas por módulo y operación
- **Códigos de Error Únicos**: Identificación precisa de problemas
- **Mensajes Descriptivos**: Información detallada para debugging
- **Global Exception Handler**: Manejo centralizado de errores

## 🧪 Testing

### Cobertura de Código
```bash
# Tests unitarios con cobertura
./mvnw clean test jacoco:report

# Ver reporte HTML
open target/site/jacoco/index.html

# Cobertura mínima: 0% (configurado para desarrollo flexible)
```

### Estructura de Tests
```
src/test/java/
├── com/account_catalogue/
│   ├── catalogue/           # Tests del catálogo de cuentas
│   ├── accounting/          # Tests de contabilidad
│   ├── bankAccounts/        # Tests de cuentas bancarias
│   ├── banks/               # Tests de bancos
│   ├── paymentMethods/      # Tests de métodos de pago
│   └── taxes/               # Tests de impuestos
└── commons/                 # Tests de utilidades comunes
```

### Tests por Categoría
- **Unit Tests**: 50+ clases de test unitario
- **Domain Tests**: Lógica de negocio pura
- **Infrastructure Tests**: Adaptadores y configuración
- **Integration Tests**: Flujos completos (futuro)

### Testing con Postman
```bash
# Ejecutar colección completa
newman run .postman/account-catalogue.postman_collection.json \
  -e .postman/account-catalogue.postman_environment.json
```

### Testing de Mensajería
- **RabbitMQ Tests**: Validación de exchanges y queues
- **Event Tests**: Procesamiento de mensajes asíncronos
- **DLQ Tests**: Recuperación de mensajes fallidos
- **Retry Tests**: Estrategias de reintento

## 🔄 CI/CD

### GitHub Actions Workflows

#### Pipeline de Desarrollo
```yaml
# on-push-to-dev.yaml
- Build automático en push a develop
- Tests unitarios completos con JaCoCo
- Build de imagen Docker
- Push a Docker Hub
- Notificación de resultados
```

#### Pipeline de Integración
```yaml
# on-pull-request-to-dev.yaml
- Validación de PRs
- Tests unitarios e integración
- Code quality checks
- Testing con Postman/Newman
- Merge automático tras aprobación
```

### Estrategia de Branches
```
main (producción)
├── develop (desarrollo)
│   ├── feature/account-catalogue-hierarchy
│   ├── feature/accounting-integration
│   ├── feature/bank-account-validation
│   ├── feature/tax-calculation
│   └── feature/portfolio-reports
└── hotfix/accounting-balance-fix
```

### Despliegue Continuo
- **Build Automático**: En cada push/merge a develop
- **Testing Automatizado**: Suite completa de tests
- **Docker Images**: Generación automática de contenedores
- **Environment Promotion**: develop → staging → production

## 🤝 Contribución

### Estándares de Desarrollo
1. **Arquitectura Hexagonal**: Mantener separación clara entre capas
2. **Domain-Driven Design**: Modelos ricos con lógica de negocio
3. **Testing Obligatorio**: Tests unitarios para toda nueva funcionalidad
4. **Event-Driven**: Eventos para comunicación entre módulos
5. **Multi-tenancy**: Siempre considerar el contexto empresarial

### Proceso de Desarrollo
1. **Crear rama**: `git checkout -b feature/nombre-modulo-funcionalidad`
2. **Implementar**: Seguir arquitectura hexagonal y principios SOLID
3. **Testing**: Cobertura completa con tests unitarios
4. **RabbitMQ**: Configurar eventos si aplica
5. **Pull Request**: Descripción detallada con testing incluido
6. **Code Review**: Aprobación requerida antes del merge
7. **Merge**: Automático tras CI/CD exitoso

### Convenciones de Código
- **Lenguaje**: Español para comentarios, inglés para código
- **Nombres**: camelCase para variables/métodos, PascalCase para clases
- **Documentación**: JavaDoc completo en clases públicas
- **Imports**: Organizados automáticamente
- **Formato**: Google Java Style Guide

### Testing Standards
- **Cobertura Mínima**: 0% configurado (flexible para desarrollo)
- **Tests por Clase**: Al menos un test por método público
- **Mockito**: Para dependencias externas
- **Given-When-Then**: Estructura clara en tests

### Módulos y Responsabilidades
Cada módulo mantiene **responsabilidad única** clara:
- **Catalogue**: Solo gestión del plan de cuentas
- **Accounting**: Solo procesamiento de asientos contables
- **BankAccounts**: Solo gestión de cuentas bancarias
- **Banks**: Solo catálogo de instituciones
- **PaymentMethods**: Solo configuración de pagos
- **Taxes**: Solo gestión de tasas impositivas


---

## Bounded context `copy` (participante Hito 2)

### Propósito

Implementa el bounded context `copy` dentro de `account-catalogue` para participar
como primer módulo real en la saga de copia/backup orquestada por `enterprises-management`.
El bounded context está aislado del resto de módulos del servicio y sigue arquitectura hexagonal.

### 4 endpoints expuestos

Base URL: `/api/accountCatalogue/copy`

| Método | Ruta | Descripción | Código éxito |
|--------|------|-------------|--------------|
| POST | `/phase` | Ejecuta la copia de Account + Tax de `entOrigen` a `entDestino` | 200 |
| GET | `/{idProceso}/status` | Consulta el estado de una fase ejecutada | 200 |
| POST | `/{idProceso}/cancel` | Cancela una fase en curso | 200 |
| DELETE | `/{idProceso}/cleanup` | Elimina registros temporales del proceso | 204 |

**Request `POST /phase`** (contrato canónico — compartido con todos los participantes):

```json
{
  "idProceso":        "uuid",
  "fase":             1,
  "entOrigen":        "empresa-A",
  "entDestino":       "empresa-B",
  "snapshotCorte":    "2026-04-30T00:00:00Z",
  "equivalenciasPrev": []
}
```

**Response `POST /phase`**:

```json
{
  "estado":              "COMPLETADO",
  "registrosProcesados": 42,
  "equivalenciasGeneradas": [
    { "modulo": "CATALOGUE", "tabla": "account", "idViejo": "1", "idNuevo": "101" }
  ],
  "mensaje":   "Fase completada: 42 cuentas, 5 impuestos copiados",
  "advertencias": []
}
```

### Copia de Account con topological sort (Kahn O(V+E))

El servicio copia todas las `Account` de `entOrigen` con `created_at <= snapshotCorte`
en **orden topológico** para respetar las FK `parent_id` (padre antes que hijo).

**Algoritmo Kahn (implementado en `KahnTopologicalSort.java`):**
1. Construir grafo de dependencias por `parent_id`
2. Calcular in-degree de cada nodo
3. BFS desde nodos raíz (parent_id = null o sin parent en entOrigen)
4. Insertar en destino en ese orden; remapear `parent_id` via equivalencias internas

**Casos especiales:**
- **Ciclo detectado** (A.parent → B, B.parent → A): retorna `ERROR_NO_REINTENTABLE`
- **Padre faltante** (parent_id no existe en entOrigen): inserta con `parent_id=null` + advertencia

### Copia de Tax con FK Account remapeada

Después de copiar todas las Account, el servicio copia los `Tax` de `entOrigen`
remapeando `salesTax` y `purchaseTax` con las equivalencias internas generadas.

- FK resuelta → Tax insertado con el nuevo ID de Account en destino
- FK no resuelta → Tax insertado con `salesTax=null` / `purchaseTax=null` + advertencia

### Tenant override programático

El bounded context fuerza `tenantId = entDestino` en cada INSERT,
ignorando el `tenantId` del JWT entrante. Mecanismo:

```
ProgrammaticTenantContextHolder.setOverride(entDestino)
    try {
        // INSERT Account con tenantId=entDestino
    } finally {
        ProgrammaticTenantContextHolder.clear()
    }
```

`CopyAwareTenantResolver` (anotado `@Primary`) implementa `CurrentTenantIdentifierResolver`
y verifica el ThreadLocal antes de resolver el tenant del JWT.

### Idempotencia via `copy_job_log`

La tabla `copy_job_log` tiene constraint `UNIQUE(id_proceso, fase, modulo)`.
Si el orquestador re-invoca `POST /phase` con el mismo `(idProceso, fase)`,
el servicio detecta el registro existente y retorna el resultado previo sin re-ejecutar.

### Schema delta (Hito 2)

```sql
-- Columnas added (no destructivo, solo agrega con DEFAULT):
ALTER TABLE account ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT NOW();
ALTER TABLE tax     ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT NOW();

-- Tabla nueva para idempotencia y estado de fases:
CREATE TABLE copy_job_log (
    id           UUID PRIMARY KEY,
    id_proceso   UUID NOT NULL,
    fase         INT  NOT NULL,
    modulo       VARCHAR(64) NOT NULL,
    estado       VARCHAR(32) NOT NULL,
    resultado_json TEXT,
    creado_en    TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(id_proceso, fase, modulo)
);
```

### Cómo habilitar el bounded context `copy`

```yaml
app:
  copy:
    participant:
      enabled: true
      security:
        require-internal-permission: true
```

Con `enabled=false`, los endpoints `/api/accountCatalogue/copy/**` no se registran (404).

### Cómo correr los tests del bounded context

```bash
# Solo tests del bounded context copy
./mvnw test -Dtest="*Copy*"

# Suite completa
./mvnw test

# Con reporte de cobertura (copy.application >= 80%, copy.domain >= 90%)
./mvnw clean test jacoco:report
```

---

**Nota**: Este microservicio representa el corazón del sistema contable CONTAPP, manejando el catálogo completo de cuentas, el procesamiento de asientos contables, y toda la infraestructura financiera. Su arquitectura modular permite escalabilidad independiente por dominio y facilita el mantenimiento de reglas contables complejas.
