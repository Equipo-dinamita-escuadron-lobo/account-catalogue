# Documentación de Pruebas de Integración - IMPUESTOS

## Introducción

Esta colección de Postman contiene pruebas de integración completas para el módulo de **Impuestos** del sistema ContApp. Las pruebas cubren todas las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) y validaciones de negocio para la gestión de impuestos.

### Propósito
- Validar la funcionalidad completa del API de impuestos
- Probar casos de uso correctos e incorrectos
- Verificar validaciones de negocio y restricciones de datos
- Asegurar la integridad de los datos relacionados con cuentas contables

### Alcance
- **Entidad principal**: Impuestos (Tax)
- **Dependencias**: Cuentas contables auxiliares (8 dígitos)
- **Autenticación**: JWT via Keycloak
- **Base de datos**: PostgreSQL con validaciones a nivel de aplicación

## Variables de Entorno

| Variable | Valor por Defecto | Descripción |
|----------|------------------|-------------|
| `baseUrl` | `localhost:8080` | URL base del servidor de desarrollo |
| `enterpriseId` | `d9a1a122-662e-47b4-852e-2769b124e025` | ID de la empresa para pruebas |
| `keycloakUser` | *(vacío)* | Usuario para autenticación Keycloak |
| `keycloakPassword` | *(vacío)* | Contraseña para autenticación Keycloak |
| `currentRequestBody` | *(dinámico)* | Cuerpo de solicitud actual |
| `currentAccountCode` | *(dinámico)* | Código de cuenta actual |
| `currentAccountLevel` | *(dinámico)* | Nivel de cuenta actual |
| `currentDeleteId` | *(dinámico)* | ID de elemento a eliminar |
| `taxId` | *(dinámico)* | ID del impuesto creado |
| `taxCode` | *(dinámico)* | Código del impuesto |
| `salesAccountId` | *(dinámico)* | ID de cuenta de ventas |
| `purchaseAccountId` | *(dinámico)* | ID de cuenta de compras |
| `encodedTaxCode` | *(dinámico)* | Código de impuesto codificado para URL |
| `createTaxRequestBody` | *(dinámico)* | Cuerpo de solicitud para crear impuesto |
| `updateRequestBody` | *(dinámico)* | Cuerpo de solicitud para actualizar |
| `invalidTaxRequestBody` | *(dinámico)* | Cuerpo de solicitud inválido |
| `nonExistentCode` | *(dinámico)* | Código inexistente para pruebas |
| `updateTaxBody` | *(dinámico)* | Cuerpo de actualización de impuesto |

## 1. Token

### 1.1 Token
**Método**: `POST`  
**URL**: `http://contables.unicauca.edu.co/dev/api/keycloak/token/`  
**Autenticación**: No requiere  
**Cuerpo**:
```json
{
    "username": "{{keycloakUser}}",
    "password": "{{keycloakPassword}}"
}
```

**Propósito**: Obtener token JWT de Keycloak para autenticar las siguientes solicitudes.

**Tests incluidos**:
- Validar código de estado 200
- Verificar presencia del access_token

**Variables modificadas**:
- `tokenKeycloak`: Almacena el token JWT obtenido

## 2. Init

Esta sección prepara el entorno de pruebas creando una jerarquía completa de cuentas contables (6 niveles) que serán utilizadas como cuentas auxiliares para los impuestos.

### 2.1 Crear Jerarquía Completa (5 niveles)
**Método**: `GET`  
**URL**: `{{baseUrl}}/api/accountCatalogue/search/{{enterpriseId}}`  
**Propósito**: Inicializar variables para la creación de jerarquía de cuentas.

### 2.2 Crear Cuenta en Bucle
**Método**: `POST`  
**URL**: `{{baseUrl}}/api/accountCatalogue/`  
**Cuerpo**: `{{currentRequestBody}}`

**Jerarquía de cuentas creada**:
1. **Nivel 1**: `1` - Activos
2. **Nivel 2**: `11` - Disponible  
3. **Nivel 3**: `1105` - Caja
4. **Nivel 4**: `110505` - Caja General
5. **Nivel 5**: `11050501` - Caja General Sede Principal *(auxiliar)*
6. **Nivel 6**: `11050502` - Caja General Sede secundaria *(auxiliar)*

**Tests incluidos**:
- Validar creación exitosa de cada cuenta
- Verificar estructura JSON de respuesta
- Confirmar códigos y IDs correctos

### 2.3 Verificar Jerarquía Creada
**Método**: `GET`  
**URL**: `{{baseUrl}}/api/accountCatalogue/search/{{enterpriseId}}`  

**Tests incluidos**:
- Confirmar creación de las 6 cuentas jerárquicas
- Validar que aparecen en el listado general

## 3. Integration

### 3.1 Casos correctos

#### 3.1.1 Crear Impuesto
**Método**: `POST`  
**URL**: `{{baseUrl}}/api/tax/`  
**Cuerpo**: `{{createTaxRequestBody}}`

**Datos de prueba**:
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "code": "IVA 19",
    "description": "Impuesto al Valor Agregado",
    "interest": 19.0,
    "salesTaxId": {{accountId_11050501}},
    "purchaseTaxId": {{accountId_11050502}}
}
```

**Tests incluidos**:
- Validar código de estado 200
- Verificar ID generado
- Confirmar datos correctos (código, descripción, interés)

#### 3.1.2 Obtener impuesto por código
**Método**: `GET`  
**URL**: `{{baseUrl}}/api/tax/{{encodedTaxCode}}/{{enterpriseId}}`  

**Tests incluidos**:
- Validar respuesta 200
- Verificar datos del impuesto
- Confirmar ID, código, descripción e interés

#### 3.1.3 Obtener impuestos paginados
**Método**: `GET`  
**URL**: `{{baseUrl}}/api/tax/taxes/{{enterpriseId}}?page=0&size=10`  

**Tests incluidos**:
- Validar estructura de paginación
- Confirmar presencia del impuesto creado
- Verificar metadatos de paginación

#### 3.1.4 Obtener impuestos paginados con búsqueda
**Método**: `GET`  
**URL**: `{{baseUrl}}/api/tax/taxes/{{enterpriseId}}?page=0&size=10&search=IVA`  

**Tests incluidos**:
- Validar funcionalidad de búsqueda
- Confirmar filtrado correcto por término de búsqueda

#### 3.1.5 Obtener impuestos activos
**Método**: `GET`  
**URL**: `{{baseUrl}}/api/tax/active/{{enterpriseId}}`  

**Tests incluidos**:
- Validar respuesta de impuestos activos
- Confirmar que el impuesto creado está presente
- Verificar que todos tienen status = true

#### 3.1.6 Actualizar impuesto
**Método**: `PUT`  
**URL**: `{{baseUrl}}/api/tax/{{taxId}}`  
**Cuerpo**: `{{updateRequestBody}}`

**Datos actualizados**:
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "code": "IVA 19 UPDATED",
    "description": "Impuesto al Valor Agregado Actualizado",
    "interest": 21.0,
    "salesTaxId": {{accountId_11050501}},
    "purchaseTaxId": {{accountId_11050502}}
}
```

**Tests incluidos**:
- Validar actualización exitosa
- Confirmar cambios en código, descripción e interés

#### 3.1.7 Cambiar estado de impuesto a inactivo
**Método**: `PATCH`  
**URL**: `{{baseUrl}}/api/tax/changeState/{{taxId}}/{{enterpriseId}}?status=false`  

**Tests incluidos**:
- Validar cambio de estado
- Confirmar status = false en respuesta

#### 3.1.8 Verificar impuesto no está en activos
**Método**: `GET`  
**URL**: `{{baseUrl}}/api/tax/active/{{enterpriseId}}`  

**Tests incluidos**:
- Confirmar que el impuesto inactivo no aparece en lista de activos

#### 3.1.9 Cambiar estado de impuesto a activo
**Método**: `PATCH`  
**URL**: `{{baseUrl}}/api/tax/changeState/{{taxId}}/{{enterpriseId}}?status=true`  

**Tests incluidos**:
- Validar reactivación del impuesto
- Confirmar status = true en respuesta

### 3.2 Casos incorrectos

#### 3.2.1 Crear
- **Crear impuesto sin código**: Valida campo requerido
- **Crear impuesto sin descripción**: Valida campo requerido  
- **Crear impuesto sin enterprise ID**: Valida campo requerido
- **Crear impuesto sin interés**: Valida campo requerido
- **Crear impuesto con código duplicado**: Valida unicidad (409 Conflict)
- **Crear impuesto con cuenta de ventas inexistente**: Valida FK (404 Not Found)
- **Crear impuesto con cuenta de compras inexistente**: Valida FK (404 Not Found)
- **Crear impuesto con cuenta sin 8 dígitos**: Valida regla de negocio
- **Crear impuesto con cuentas iguales**: Valida regla de negocio (409 Conflict)

#### 3.2.2 Consultar
- **Obtener impuesto por código inexistente**: 404 Not Found
- **Obtener impuesto con enterprise ID inexistente**: 404 Not Found
- **Obtener impuestos paginados con enterprise inexistente**: Lista vacía
- **Obtener impuestos paginados con página negativa**: Manejo de error
- **Obtener impuestos paginados con size cero**: Manejo de error
- **Obtener impuestos con búsqueda sin resultados**: Lista vacía
- **Obtener impuestos activos con enterprise inexistente**: Lista vacía

#### 3.2.3 Actualizar
- **Actualizar impuesto sin código**: 400 Bad Request
- **Actualizar impuesto sin descripción**: 400 Bad Request
- **Actualizar impuesto sin enterprise ID**: 400 Bad Request
- **Actualizar impuesto sin interés**: 400 Bad Request
- **Actualizar impuesto con ID inexistente**: 404 Not Found
- **Actualizar impuesto con cuenta compras inexistente**: 404 Not Found
- **Actualizar impuesto con cuenta ventas inexistente**: 404 Not Found
- **Actualizar impuesto con cuentas iguales**: 409 Conflict

#### 3.2.4 Cambiar Estado
- **Cambiar estado con ID inexistente**: 404 Not Found
- **Cambiar estado con enterprise inexistente**: 404 Not Found
- **Cambiar estado sin parámetro status**: 400 Bad Request
- **Cambiar estado con status inválido**: 400 Bad Request

## 4. Tear Down

### 4.1 Eliminar impuesto
**Método**: `DELETE`  
**URL**: `{{baseUrl}}/api/tax/{{taxId}}/{{enterpriseId}}`  

**Propósito**: Eliminar el impuesto creado durante las pruebas.

### 4.2 Obtener todas las cuentas para eliminar
**Método**: `GET`  
**URL**: `{{baseUrl}}/api/accountCatalogue/search/{{enterpriseId}}`  

**Propósito**: Obtener listado completo de cuentas para eliminación ordenada.

### 4.3 Eliminar cuenta
**Método**: `DELETE`  
**URL**: `{{baseUrl}}/api/accountCatalogue/{{currentDeleteId}}/{{enterpriseId}}`  

**Propósito**: Eliminar todas las cuentas creadas en orden inverso (hijas primero, luego padres).

**Limpieza de variables**:
- `accountsToDelete`
- `currentDeleteIndex`
- `accountId_*` (todos los IDs de cuentas)
- `taxId`
- `taxCode`

## Notas de Ejecución

### Orden de Ejecución
1. **Token**: Ejecutar primero para obtener autenticación
2. **Init**: Crear jerarquía de cuentas (ejecutar en secuencia)
3. **Integration**: Ejecutar casos correctos primero, luego incorrectos
4. **Tear Down**: Ejecutar al final para limpieza

### Dependencias
- **Keycloak**: Debe estar disponible y configurado
- **Base de datos**: PostgreSQL con esquema actualizado
- **Variables de entorno**: Configurar `keycloakUser` y `keycloakPassword`

### Consideraciones Especiales
- Las cuentas auxiliares deben tener exactamente 8 dígitos
- Los impuestos requieren cuentas de ventas y compras diferentes
- La eliminación de cuentas debe hacerse en orden jerárquico inverso
- Los códigos de impuesto deben ser únicos por empresa

### Manejo de Errores
- **400 Bad Request**: Datos inválidos o campos requeridos faltantes
- **404 Not Found**: Recursos inexistentes (IDs o empresas)
- **409 Conflict**: Violación de restricciones de unicidad o negocio

### Variables Dinámicas
- Los IDs de cuentas se generan automáticamente durante Init
- Los cuerpos de solicitud se construyen dinámicamente
- Los códigos se codifican para URLs cuando es necesario

### Validaciones de Negocio
- Unicidad de códigos de impuesto por empresa
- Cuentas auxiliares deben existir y tener 8 dígitos
- Cuentas de ventas y compras deben ser diferentes
- Estados booleanos para activar/desactivar impuestos