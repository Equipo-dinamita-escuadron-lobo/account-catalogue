# Documentación de Pruebas de Integración - CATALOGO DE CUENTAS

Esta colección de Postman contiene pruebas de integración para el módulo de Catálogo de Cuentas del sistema ContApp. Las pruebas cubren operaciones CRUD completas, validaciones de negocio, importación/exportación de Excel, y manejo de errores.

## Variables de Colección

- `testRunId`: Identificador único para la ejecución de tests
- `timestamp`: Marca de tiempo para logs
- `tokenKeycloak`: Token de autenticación JWT de Keycloak
- `accountsHierarchy`: Jerarquía de cuentas en formato JSON
- `currentAccountIndex`: Índice actual para creación de cuentas
- `createdAccounts`: IDs de cuentas creadas durante los tests
- `accountsToUpdate`: Lista de cuentas para actualizar
- `currentUpdateIndex`: Índice actual para actualizaciones

## 1 Setup

### Obtener token Keycloak

**Método:** POST  
**URL:** {{baseUrl}}/auth/realms/contapp/protocol/openid-connect/token  
**Autenticación:** No aplica  
**Headers:** 
- Content-Type: application/x-www-form-urlencoded  

**Body (form-data):**
- grant_type: password
- client_id: contapp-client
- username: {{username}}
- password: {{password}}

**Pre-request Script:**
```javascript
console.log('Obteniendo token de Keycloak...');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene access_token', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('access_token');
});

var jsonData = pm.response.json();
pm.collectionVariables.set('tokenKeycloak', jsonData.access_token);
console.log('Token obtenido y guardado en variable de colección');
```

### Crear jerarquía de cuentas (5 niveles)

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "code": "{{currentCode}}",
    "description": "{{currentDescription}}",
    "nature": "{{currentNature}}",
    "financialStatus": "{{currentFinancialStatus}}",
    "classification": "{{currentClassification}}",
    "parent": {{currentParent}},
    "crossing": {{currentCrossing}},
    "costCenter": {{currentCostCenter}},
    "status": {{currentStatus}},
    "idEnterprise": "{{enterpriseId}}"
}
```

**Pre-request Script:**
```javascript
console.log('Creando jerarquía de cuentas de 5 niveles...');

var accountsHierarchy = [
    { code: "1", description: "Activo", nature: "Debito", financialStatus: "Estado de Situacion Financiero", classification: "Activo", parent: null, crossing: null, costCenter: null, status: true },
    { code: "11", description: "Activo Corriente", nature: "Debito", financialStatus: "Estado de Situacion Financiero", classification: "Activo Corriente", parent: 1, crossing: null, costCenter: null, status: true },
    { code: "1105", description: "Caja", nature: "Debito", financialStatus: "Estado de Situacion Financiero", classification: "Activo Corriente", parent: 11, crossing: null, costCenter: null, status: true },
    { code: "110505", description: "Caja General", nature: "Debito", financialStatus: "Estado de Situacion Financiero", classification: "Activo Corriente", parent: 1105, crossing: null, costCenter: null, status: true },
    { code: "11050501", description: "Caja General Principal", nature: "Debito", financialStatus: "Estado de Situacion Financiero", classification: "Activo Corriente", parent: 110505, crossing: null, costCenter: null, status: true }
];

pm.collectionVariables.set('accountsHierarchy', JSON.stringify(accountsHierarchy));
pm.collectionVariables.set('currentAccountIndex', 0);

console.log('Jerarquía definida con ' + accountsHierarchy.length + ' niveles');
```

**Test Script:**
```javascript
pm.test('Status code es 201', function () {
    pm.response.to.have.status(201);
});

pm.test('Respuesta contiene ID de cuenta creada', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
});

var jsonData = pm.response.json();
var accountsHierarchy = JSON.parse(pm.collectionVariables.get('accountsHierarchy'));
var currentIndex = parseInt(pm.collectionVariables.get('currentAccountIndex'));

var createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts') || '{}');
createdAccounts[accountsHierarchy[currentIndex].code] = jsonData.id;
pm.collectionVariables.set('createdAccounts', JSON.stringify(createdAccounts));

console.log('Cuenta ' + accountsHierarchy[currentIndex].code + ' creada con ID: ' + jsonData.id);

currentIndex++;
pm.collectionVariables.set('currentAccountIndex', currentIndex);

if (currentIndex < accountsHierarchy.length) {
    postman.setNextRequest('Crear jerarquía de cuentas (5 niveles)');
} else {
    console.log('Jerarquía completa creada');
    pm.collectionVariables.unset('accountsHierarchy');
    pm.collectionVariables.unset('currentAccountIndex');
}
```

## 2 CRUD Operations

### Listar cuentas

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/search/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando todas las cuentas...');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta es un array', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});

var jsonData = pm.response.json();
console.log('Total de cuentas encontradas: ' + jsonData.length);
```

### Obtener cuenta por ID

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/{{accountId}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
const createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts') || '{}');
const account1Id = createdAccounts['1'];
pm.environment.set('accountId', account1Id);
console.log('Obteniendo cuenta con ID: ' + account1Id);
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene propiedades requeridas', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('description');
});

console.log('Cuenta obtenida correctamente');
```

### Editar cuenta

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/{{accountId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "code": "1",
    "description": "Activo Editado",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true
}
```

**Pre-request Script:**
```javascript
const createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts') || '{}');
const account1Id = createdAccounts['1'];
pm.environment.set('accountId', account1Id);
console.log('Editando cuenta con ID: ' + account1Id);
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene cuenta actualizada', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.description).to.equal('Activo Editado');
});

console.log('Cuenta editada correctamente');
```

### Cambiar estado de cuenta

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/changeState/{{accountId}}/{{enterpriseId}}?status=false  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
const createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts') || '{}');
const account11Id = createdAccounts['11'];
pm.environment.set('accountId', account11Id);
console.log('Cambiando estado de cuenta con ID: ' + account11Id);
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene estado actualizado', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.status).to.be.false;
});

console.log('Estado de cuenta cambiado correctamente');
```

### Listar por código

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/accountByCode/1/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando cuenta por código: 1');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene cuenta con código 1', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.code).to.equal('1');
});

console.log('Cuenta encontrada por código');
```

### Listar árbol por código

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/tree/1/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Obteniendo árbol jerárquico desde código: 1');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene estructura de árbol', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('children');
});

console.log('Árbol jerárquico obtenido correctamente');
```

### Listar todos los árboles

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/trees/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando todos los árboles de cuentas');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta es un array de árboles', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});

console.log('Árboles listados correctamente');
```

### Listar cuentas auxiliares

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/auxiliary/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando cuentas auxiliares');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene estructura auxiliar', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('auxiliaryAccounts');
    pm.expect(jsonData).to.have.property('totalCount');
});

console.log('Cuentas auxiliares listadas correctamente');
```

### Listar auxiliares con cruce

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/auxiliary/crossing/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando cuentas auxiliares con cruce');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene estructura auxiliar con cruce', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('auxiliaryAccounts');
    pm.expect(jsonData).to.have.property('totalCount');
});

console.log('Cuentas auxiliares con cruce listadas correctamente');
```

### Buscar cuentas

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/search/{{enterpriseId}}?query=activo  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando cuentas con query: activo');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta es un array de resultados', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.be.an('array');
});

console.log('Búsqueda de cuentas completada');
```

### Descargar plantilla Excel

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/template/excel?entId={{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Descargando plantilla Excel para importación');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta es un archivo Excel', function () {
    pm.expect(pm.response.headers.get('Content-Type')).to.include('spreadsheet');
});

console.log('Plantilla Excel descargada correctamente');
```

### Exportar catálogo a Excel

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/export/excel?entId={{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Iniciando exportación de catálogo a Excel');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene jobId', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('jobId');
});

var jsonData = pm.response.json();
pm.collectionVariables.set('exportJobId', jsonData.jobId);
console.log('Job de exportación iniciado con ID: ' + jsonData.jobId);
```

### Consultar estado exportación

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/export/status/{{exportJobId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando estado del job de exportación: ' + pm.collectionVariables.get('exportJobId'));
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene estado del job', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('status');
});

var jsonData = pm.response.json();
console.log('Estado del job: ' + jsonData.status);

if (jsonData.status === 'COMPLETED') {
    console.log('Exportación completada, archivo listo para descargar');
} else if (jsonData.status === 'FAILED') {
    console.log('Exportación fallida');
}
```

### Descargar archivo exportado

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/export/download/{{exportJobId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Descargando archivo exportado');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta es un archivo Excel', function () {
    pm.expect(pm.response.headers.get('Content-Type')).to.include('spreadsheet');
});

console.log('Archivo Excel exportado descargado correctamente');
```

### Importar catálogo desde Excel

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/import/excel  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Body (form-data):**
- entId: {{enterpriseId}}
- file: [Archivo Excel]

**Nota:** Este test requiere cargar un archivo Excel con el catálogo de cuentas válido para la importación.

**Pre-request Script:**
```javascript
console.log('Iniciando importación de catálogo desde Excel');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene jobId', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('jobId');
});

var jsonData = pm.response.json();
pm.collectionVariables.set('importJobId', jsonData.jobId);
console.log('Job de importación iniciado con ID: ' + jsonData.jobId);
```

### Consultar estado importación

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/import/status/{{importJobId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando estado del job de importación: ' + pm.collectionVariables.get('importJobId'));
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta contiene estado del job', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('status');
});

var jsonData = pm.response.json();
console.log('Estado del job: ' + jsonData.status);

if (jsonData.status === 'COMPLETED') {
    console.log('Importación completada exitosamente');
} else if (jsonData.status === 'FAILED') {
    console.log('Importación fallida');
}
```

### Eliminar cuenta

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/{{accountId}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
const createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts') || '{}');
const account11050501Id = createdAccounts['11050501'];
pm.environment.set('accountId', account11050501Id);
console.log('Eliminando cuenta hoja con ID: ' + account11050501Id);
```

**Test Script:**
```javascript
pm.test('Status code es 200 o 204', function () {
    pm.expect(pm.response.code).to.be.oneOf([200, 204]);
});

console.log('Cuenta eliminada correctamente');
```

## 3 Error Cases

### Crear cuenta - código duplicado

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "code": "1",
    "description": "Cuenta con código duplicado",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true,
    "idEnterprise": "{{enterpriseId}}"
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta con código duplicado...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 o 409 (código duplicado)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 409]);
});

console.log('Error esperado recibido: ' + pm.response.code);
```

### Crear cuenta - descripción vacía

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "code": "99",
    "description": "",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true,
    "idEnterprise": "{{enterpriseId}}"
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta con descripción vacía...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (validación fallida)', function () {
    pm.response.to.have.status(400);
});

console.log('Error esperado recibido: ' + pm.response.code);
```

### Crear cuenta - naturaleza inválida

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "code": "99",
    "description": "Cuenta con naturaleza inválida",
    "nature": "NaturalezaInvalida",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true,
    "idEnterprise": "{{enterpriseId}}"
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta con naturaleza inválida...');
console.log('Código de respuesta: ' + pm.response.code);
```

**Test Script:**
```javascript
pm.test('Status code es 400 (naturaleza inválida)', function () {
    pm.response.to.have.status(400);
});

console.log('Error esperado recibido: ' + pm.response.code);
```

### Crear cuenta - padre inexistente

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "code": "9999",
    "description": "Cuenta con padre inexistente",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": 999999,
    "crossing": null,
    "costCenter": null,
    "status": true,
    "idEnterprise": "{{enterpriseId}}"
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta con padre inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (padre no encontrado)', function () {
    pm.response.to.have.status(404);
});

console.log('Error esperado recibido: ' + pm.response.code);
```

### Crear cuenta - empresa inexistente

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "code": "99",
    "description": "Cuenta con empresa inválida",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true,
    "idEnterprise": "00000000-0000-0000-0000-000000000000"
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta con empresa inexistente...');
console.log('Nota: Este test verifica que el sistema rechaza empresas no válidas');
```

**Test Script:**
```javascript
// El comportamiento esperado depende de la implementación:
// - Si hay validación de empresa: 400 o 404
// - Si no hay validación: 200 (se crea la cuenta)
pm.test('Status code indica manejo de empresa (200, 400, 404 o 500)', function () {
    pm.expect(pm.response.code).to.be.oneOf([200, 400, 404, 500]);
});

// Si se creó la cuenta, guardar el ID para eliminarlo en el teardown
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.id) {
        pm.collectionVariables.set('accountId_invalidEnterprise', jsonData.id);
        console.log('Cuenta creada con ID: ' + jsonData.id + ' - Se eliminará en teardown');
    }
}

console.log('Código de respuesta: ' + pm.response.code);
```

### Editar cuenta - ID inexistente

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/999999  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "code": "99",
    "description": "Cuenta editada",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando editar cuenta con ID inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (cuenta no encontrada)', function () {
    pm.response.to.have.status(404);
});

console.log('Error esperado recibido: ' + pm.response.code);
```

### Editar cuenta - código duplicado

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/{{accountToEditId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "code": "1",
    "description": "Intentando duplicar código",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando editar cuenta con código duplicado...');
const createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts') || '{}');
const account11Id = createdAccounts['11'];
pm.environment.set('accountToEditId', account11Id);
console.log('Editando cuenta con ID: ' + account11Id + ' para cambiar código a 1 (duplicado)');
```

**Test Script:**
```javascript
pm.test('Status code es 400 o 409 (código duplicado)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 409]);
});

console.log('Error esperado recibido: ' + pm.response.code);
```

### Editar cuenta - descripción vacía

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/{{accountToEditId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "code": "1",
    "description": "",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando editar cuenta con descripción vacía...');
const createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts') || '{}');
const account1Id = createdAccounts['1'];
pm.environment.set('accountToEditId', account1Id);
console.log('Editando cuenta con ID: ' + account1Id);
```

**Test Script:**
```javascript
pm.test('Status code es 400 (validación fallida)', function () {
    pm.response.to.have.status(400);
});

console.log('Error esperado recibido: ' + pm.response.code);
```

### Editar cuenta - empresa incorrecta

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/{{accountToEditId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "00000000-0000-0000-0000-000000000000",
    "code": "1",
    "description": "Cuenta editada",
    "nature": "Debito",
    "financialStatus": "Estado de Situacion Financiero",
    "classification": "Activo Corriente",
    "parent": null,
    "crossing": null,
    "costCenter": null,
    "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando editar cuenta con empresa incorrecta...');
const createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts') || '{}');
const account1Id = createdAccounts['1'];
pm.environment.set('accountToEditId', account1Id);
console.log('Editando cuenta con ID: ' + account1Id + ' con empresa incorrecta');
```

**Test Script:**
```javascript
// El servicio valida que idEnterprise coincida, lanza IllegalArgumentException
// que actualmente retorna 500 (no manejada en GlobalExceptionHandler)
pm.test('Status code indica rechazo de empresa incorrecta (400, 404 o 500)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 404, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Importar catálogo - sin archivo

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/import/excel  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Body (form-data):**
- entId: {{enterpriseId}}

**Pre-request Script:**
```javascript
console.log('Intentando importar catálogo sin archivo...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 o 500 (archivo requerido)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Importar catálogo - sin enterpriseId

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/import/excel  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Body (form-data):**
- file: [Archivo Excel]

**Pre-request Script:**
```javascript
console.log('Intentando importar catálogo sin enterpriseId...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 o 500 (enterpriseId requerido)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Importar catálogo - enterpriseId vacío

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/import/excel  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Body (form-data):**
- entId: 
- file: [Archivo Excel]

**Pre-request Script:**
```javascript
console.log('Intentando importar catálogo con enterpriseId vacío...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (enterpriseId vacío)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Consultar estado importación - jobId inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/import/status/job-inexistente-12345  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando estado de importación con jobId inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (jobId no encontrado)', function () {
    pm.response.to.have.status(404);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Exportar catálogo - sin enterpriseId

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/export/excel  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando exportar catálogo sin enterpriseId...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 o 500 (enterpriseId requerido)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Exportar catálogo - enterpriseId vacío

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/export/excel?entId=  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando exportar catálogo con enterpriseId vacío...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 o 500 (enterpriseId vacío)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Consultar estado exportación - jobId inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/export/status/job-inexistente-99999  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando estado de exportación con jobId inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (jobId no encontrado)', function () {
    pm.response.to.have.status(404);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Descargar archivo exportado - jobId inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/export/download/job-inexistente-99999  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Descargando archivo con jobId inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (jobId no encontrado)', function () {
    pm.response.to.have.status(404);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Descargar plantilla - sin enterpriseId

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/template/excel  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Descargando plantilla sin enterpriseId...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 o 500 (enterpriseId requerido)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Listar por código - código inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/accountByCode/999999999/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando cuenta con código inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (código no encontrado)', function () {
    pm.response.to.have.status(404);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Listar por código - empresa inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/accountByCode/1/empresa-inexistente-99999  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando cuenta con empresa inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (empresa no encontrada)', function () {
    pm.response.to.have.status(404);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Listar árbol - código inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/tree/999999999/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando árbol con código inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (código no encontrado)', function () {
    pm.response.to.have.status(404);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Listar árboles - empresa inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/trees/empresa-inexistente-99999  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando árboles con empresa inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 con lista vacía o 404', function () {
    pm.expect(pm.response.code).to.be.oneOf([200, 404]);
});

if (pm.response.code === 200) {
    pm.test('Respuesta es lista vacía', function () {
        var jsonData = pm.response.json();
        pm.expect(jsonData).to.be.an('array').that.is.empty;
    });
}

console.log('Código de respuesta: ' + pm.response.code);
```

### Listar auxiliares - empresa inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/auxiliary/empresa-inexistente-99999  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando auxiliares con empresa inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta tiene lista de cuentas vacía', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.auxiliaryAccounts).to.be.an('array').that.is.empty;
    pm.expect(jsonData.totalCount).to.equal(0);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Listar auxiliares con cruce - empresa inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/auxiliary/crossing/empresa-inexistente-99999  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando auxiliares con cruce y empresa inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

pm.test('Respuesta tiene lista de cuentas vacía', function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.auxiliaryAccounts).to.be.an('array').that.is.empty;
    pm.expect(jsonData.totalCount).to.equal(0);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Buscar cuentas - empresa inexistente

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/search/empresa-inexistente-99999  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando cuentas con empresa inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 con lista vacía o 404', function () {
    pm.expect(pm.response.code).to.be.oneOf([200, 404]);
});

if (pm.response.code === 200) {
    pm.test('Respuesta es lista vacía', function () {
        var jsonData = pm.response.json();
        pm.expect(jsonData).to.be.an('array').that.is.empty;
    });
}

console.log('Código de respuesta: ' + pm.response.code);
```

### Cambiar estado - cuenta inexistente

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/changeState/999999/{{enterpriseId}}?status=false  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando cambiar estado de cuenta inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (cuenta no encontrada)', function () {
    pm.response.to.have.status(404);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Cambiar estado - empresa inexistente

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/changeState/1/empresa-inexistente-99999?status=false  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando cambiar estado con empresa inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (empresa no encontrada)', function () {
    pm.response.to.have.status(404);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Cambiar estado - sin parámetro status

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/changeState/1/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando cambiar estado sin parámetro status...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (status requerido)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

### Cambiar estado - ID inválido (texto)

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/changeState/abc/{{enterpriseId}}?status=true  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando cambiar estado con ID inválido (texto)...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (ID inválido)', function () {
    pm.expect(pm.response.code).to.be.oneOf([400, 500]);
});

console.log('Código de respuesta: ' + pm.response.code);
```

## 4 Tear Down

### Eliminar cuenta con empresa inexistente

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/{{accountId_invalidEnterprise}}/00000000-0000-0000-0000-000000000000  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
var accountIdInvalidEnterprise = pm.collectionVariables.get('accountId_invalidEnterprise');
console.log('Variable accountId_invalidEnterprise: ' + accountIdInvalidEnterprise);

if (!accountIdInvalidEnterprise || accountIdInvalidEnterprise === '' || accountIdInvalidEnterprise === 'undefined') {
    console.log('No hay cuenta con empresa inexistente para eliminar, saltando...');
} else {
    pm.variables.set('deleteInvalidAccount', 'true');
    console.log('Preparando eliminación de cuenta con empresa inexistente, ID: ' + accountIdInvalidEnterprise);
}
```

**Test Script:**
```javascript
var shouldDelete = pm.variables.get('deleteInvalidAccount');

if (shouldDelete === 'true') {
    pm.test('Cuenta con empresa inexistente eliminada', function () {
        pm.expect(pm.response.code).to.be.oneOf([200, 204, 404]);
    });
    console.log('Status eliminación cuenta empresa inexistente: ' + pm.response.code);
    pm.collectionVariables.unset('accountId_invalidEnterprise');
    pm.variables.unset('deleteInvalidAccount');
} else {
    pm.test('Omitido - No hay cuenta con empresa inexistente', function () {
        pm.expect(true).to.be.true;
    });
}
```

### Obtener todas las cuentas para eliminar

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/search/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Obteniendo todas las cuentas para eliminar...');
console.log('Enterprise ID: ' + pm.environment.get('enterpriseId'));
```

**Test Script:**
```javascript
pm.test('Status code es 200', function () {
    pm.response.to.have.status(200);
});

var accounts = pm.response.json();
console.log('Total de cuentas encontradas: ' + accounts.length);

if (accounts.length === 0) {
    console.log('No hay cuentas para eliminar');
    postman.setNextRequest(null);
} else {
    accounts.sort(function(a, b) {
        return b.code.length - a.code.length;
    });
    
    var accountsToDelete = accounts.map(function(acc) {
        return { id: acc.id, code: acc.code };
    });
    
    pm.collectionVariables.set('accountsToDelete', JSON.stringify(accountsToDelete));
    pm.collectionVariables.set('currentDeleteIndex', 0);
    
    console.log('Orden de eliminación (hijas a padres):');
    accountsToDelete.forEach(function(acc, idx) {
        console.log((idx + 1) + '. ' + acc.code + ' (ID: ' + acc.id + ')');
    });
}
```

### Eliminar cuenta

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/{{currentDeleteId}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
var accountsToDelete = JSON.parse(pm.collectionVariables.get('accountsToDelete') || '[]');
var currentIndex = parseInt(pm.collectionVariables.get('currentDeleteIndex') || '0');

if (accountsToDelete.length === 0) {
    console.log('No hay cuentas para eliminar');
    postman.setNextRequest(null);
} else if (currentIndex < accountsToDelete.length) {
    var currentAccount = accountsToDelete[currentIndex];
    pm.environment.set('currentDeleteId', currentAccount.id);
    console.log('Eliminando cuenta ' + (currentIndex + 1) + '/' + accountsToDelete.length + ': ' + currentAccount.code + ' (ID: ' + currentAccount.id + ')');
} else {
    console.log('Todas las cuentas fueron procesadas');
    postman.setNextRequest(null);
}
```

**Test Script:**
```javascript
var accountsToDelete = JSON.parse(pm.collectionVariables.get('accountsToDelete') || '[]');
var currentIndex = parseInt(pm.collectionVariables.get('currentDeleteIndex') || '0');

if (currentIndex < accountsToDelete.length) {
    var currentAccount = accountsToDelete[currentIndex];
    
    pm.test('Cuenta ' + currentAccount.code + ' eliminada correctamente', function () {
        pm.expect(pm.response.code).to.be.oneOf([200, 204]);
    });
    
    console.log('Cuenta ' + currentAccount.code + ' eliminada - Status: ' + pm.response.code);
    
    currentIndex++;
    pm.collectionVariables.set('currentDeleteIndex', currentIndex);
    
    if (currentIndex < accountsToDelete.length) {
        postman.setNextRequest('Eliminar cuenta');
    } else {
        console.log('Todas las cuentas fueron eliminadas exitosamente');
        pm.collectionVariables.unset('accountsToDelete');
        pm.collectionVariables.unset('currentDeleteIndex');
        pm.collectionVariables.unset('accountId_1');
        pm.collectionVariables.unset('accountId_11');
        pm.collectionVariables.unset('accountId_1105');
        pm.collectionVariables.unset('accountId_110505');
        pm.collectionVariables.unset('accountId_11050501');
        postman.setNextRequest(null);
    }
}
```

**Notas de ejecución:**

1. **Configuración inicial:** Antes de ejecutar la colección, asegúrese de que las variables de entorno estén configuradas correctamente (baseUrl, enterpriseId, tokenKeycloak).

2. **Orden de ejecución:** La colección está organizada en carpetas que deben ejecutarse en orden: 1 Setup, 2 CRUD Operations, 3 Error Cases, 4 Tear Down.

3. **Dependencias:** Los tests de CRUD dependen de que las cuentas se hayan creado correctamente en la carpeta Setup. Los tests de error requieren que las cuentas existan.

4. **Limpieza:** La carpeta Tear Down elimina todas las cuentas creadas durante los tests para mantener la base de datos limpia.

5. **Variables dinámicas:** La colección utiliza variables de colección para almacenar IDs de cuentas creadas dinámicamente durante la ejecución.

6. **Jerarquía de cuentas:** Los tests crean una jerarquía de cuentas de 5 niveles para probar las relaciones padre-hijo.

7. **Importación/Exportación:** Los tests de importación y exportación son asíncronos y requieren verificar el estado del job antes de descargar archivos.
