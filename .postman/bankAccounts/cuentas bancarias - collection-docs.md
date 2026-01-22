# Documentación de Pruebas de Integración - CUENTAS BANCARIAS

Esta colección de Postman contiene pruebas de integración para los módulos de Bancos y Cuentas Bancarias del sistema ContApp. Las pruebas cubren operaciones CRUD completas para bancos y cuentas bancarias, validaciones de negocio, y manejo de errores.

## Variables de Colección

- `testRunId`: Identificador único para la ejecución de tests
- `timestamp`: Marca de tiempo para logs
- `tokenKeycloak`: Token de autenticación JWT de Keycloak
- `accountsHierarchy`: Jerarquía de cuentas en formato JSON
- `currentAccountIndex`: Índice actual para creación de cuentas
- `createdAccounts`: IDs de cuentas creadas durante los tests
- `bankId_1`: ID del primer banco creado
- `bankId_2`: ID del segundo banco creado
- `bankAccountId_1`: ID de la primera cuenta bancaria creada
- `bankAccountId_2`: ID de la segunda cuenta bancaria creada
- `accountId_11050501`: ID de la cuenta contable de prueba
- `accountId_11050502`: ID de la cuenta contable de prueba

## 1 Token

### Token

**Método:** POST  
**URL:** http://contables.unicauca.edu.co/dev/api/keycloak/token/  
**Autenticación:** No aplica  
**Headers:** No aplica  

**Body (raw/json):**
```json
{
    "username": "{{keycloakUser}}",
    "password": "{{keycloakPassword}}"
}
```

**Pre-request Script:**
```javascript
pm.collectionVariables.set("testRunId", pm.variables.replaceIn("{{$randomUUID}}"));
pm.collectionVariables.set("timestamp", new Date().toISOString());
console.log("=== INICIO DE PRUEBA ===");
console.log("Timestamp:", pm.collectionVariables.get("timestamp"));
console.log("Enterprise ID:", pm.environment.get("enterpriseId"));
```

**Test Script:**
```javascript
var jsonData = pm.response.json();
pm.collectionVariables.set("tokenKeycloak", jsonData.access_token);

pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Access token is present", function () {
    pm.expect(jsonData.access_token).to.not.be.null;
});
```

## 2 Init

### Crear Jerarquía Completa (5 niveles)

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/search/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
// Definir la jerarquía de cuentas a crear (5 niveles)
const accountsHierarchy = [
    {
        level: 1,
        code: '1',
        description: 'Activos',
        nature: 'Debito',
        financialStatus: 'Estado de Situacion Financiero',
        classification: 'Activo Corriente',
        parent: null,
    },
    {
        level: 2,
        code: '11',
        description: 'Disponible',
        nature: 'Debito',
        financialStatus: 'Estado de Situacion Financiero',
        classification: 'Activo Corriente',
        parentCode: '1'
    },
    {
        level: 3,
        code: '1105',
        description: 'Caja',
        nature: 'Debito',
        financialStatus: 'Estado de Situacion Financiero',
        classification: 'Activo Corriente',
        parentCode: '11'
    },
    {
        level: 4,
        code: '110505',
        description: 'Caja General',
        nature: 'Debito',
        financialStatus: 'Estado de Situacion Financiero',
        classification: 'Activo Corriente',
        parentCode: '1105'
    },
    {
        level: 5,
        code: '11050501',
        description: 'Caja General Sede Principal',
        nature: 'Debito',
        financialStatus: 'Estado de Resultados',
        classification: 'Activo Corriente',
        parentCode: '110505',
        crossing: true,
        costCenter: true
    },
    {
        level: 6,
        code: '11050502',
        description: 'Caja General Sede secundaria',
        nature: 'Debito',
        financialStatus: 'Estado de Resultados',
        classification: 'Activo Corriente',
        parentCode: '110505',
        crossing: true,
        costCenter: true
    }
];

// Guardar la jerarquía en variables de colección
pm.collectionVariables.set('accountsHierarchy', JSON.stringify(accountsHierarchy));
pm.collectionVariables.set('currentAccountIndex', 0);
pm.collectionVariables.set('createdAccounts', JSON.stringify({}));

console.log('=== INICIO CREACIÓN JERÁRQUICA DE CUENTAS ===');
console.log('Total de cuentas a crear: ' + accountsHierarchy.length);
```

**Test Script:**
```javascript
// Este request es solo para inicializar, el trabajo real lo hace el siguiente
```

### Crear Cuenta en Bucle

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
const accountsHierarchy = JSON.parse(pm.collectionVariables.get('accountsHierarchy'));
const currentIndex = parseInt(pm.collectionVariables.get('currentAccountIndex'));

if (currentIndex < accountsHierarchy.length) {
    const account = accountsHierarchy[currentIndex];
    
    // Establecer variables para el request
    pm.collectionVariables.set('currentCode', account.code);
    pm.collectionVariables.set('currentDescription', account.description);
    pm.collectionVariables.set('currentNature', account.nature);
    pm.collectionVariables.set('currentFinancialStatus', account.financialStatus);
    pm.collectionVariables.set('currentClassification', account.classification);
    pm.collectionVariables.set('currentCrossing', account.crossing || false);
    pm.collectionVariables.set('currentCostCenter', account.costCenter || false);
    pm.collectionVariables.set('currentStatus', true);
    
    // Resolver parent ID
    if (account.parentCode) {
        const createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts'));
        const parentId = createdAccounts[account.parentCode];
        pm.collectionVariables.set('currentParent', parentId);
    } else {
        pm.collectionVariables.set('currentParent', null);
    }
    
    console.log('Creando cuenta ' + (currentIndex + 1) + '/' + accountsHierarchy.length + ': ' + account.code + ' - ' + account.description);
} else {
    console.log('Todas las cuentas han sido creadas');
    postman.setNextRequest('Verificar Jerarquía Creada');
}
```

**Test Script:**
```javascript
const accountsHierarchy = JSON.parse(pm.collectionVariables.get('accountsHierarchy'));
const currentIndex = parseInt(pm.collectionVariables.get('currentAccountIndex'));

if (currentIndex < accountsHierarchy.length) {
    pm.test('Status code es 201', function () {
        pm.response.to.have.status(201);
    });

    pm.test('Respuesta contiene ID de cuenta creada', function () {
        var jsonData = pm.response.json();
        pm.expect(jsonData).to.have.property('id');
    });

    var jsonData = pm.response.json();
    var createdAccounts = JSON.parse(pm.collectionVariables.get('createdAccounts'));
    const account = accountsHierarchy[currentIndex];
    createdAccounts[account.code] = jsonData.id;
    pm.collectionVariables.set('createdAccounts', JSON.stringify(createdAccounts));
    
    // Guardar IDs específicos para cuentas bancarias
    if (account.code === '11050501') {
        pm.environment.set('accountId_11050501', jsonData.id);
    } else if (account.code === '11050502') {
        pm.environment.set('accountId_11050502', jsonData.id);
    }

    console.log('Cuenta ' + account.code + ' creada con ID: ' + jsonData.id);

    const nextIndex = currentIndex + 1;
    pm.collectionVariables.set('currentAccountIndex', nextIndex);

    if (nextIndex < accountsHierarchy.length) {
        postman.setNextRequest('Crear Cuenta en Bucle');
    } else {
        console.log('Jerarquía completa creada');
        postman.setNextRequest('Verificar Jerarquía Creada');
    }
}
```

### Verificar Jerarquía Creada

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/search/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Verificando jerarquía de cuentas creada...');
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
console.log('=== JERARQUÍA DE CUENTAS CREADA EXITOSAMENTE ===');
```

## 3 Integration

### 2.1 Bancos

#### 21.1 Casos correctos

##### Crear banco - 201

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "code": "001",
  "name": "Banco de Pruebas 1",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Creando primer banco...');
```

**Test Script:**
```javascript
pm.test('Status code es 201 (Created)', function () {
    pm.response.to.have.status(201);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta contiene los datos del banco', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson).to.have.property('code');
    pm.expect(responseJson).to.have.property('name');
    pm.expect(responseJson).to.have.property('status');
    pm.expect(responseJson.status).to.be.true;
    pm.expect(responseJson.code).to.equal('001');
});

const responseJson = pm.response.json();
pm.environment.set('bankId_1', responseJson.id);
console.log('✓ Banco 1 creado con ID: ' + responseJson.id);
```

##### Crear segundo banco - 201

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "code": "002",
  "name": "Banco de Pruebas 2",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Creando segundo banco...');
```

**Test Script:**
```javascript
pm.test('Status code es 201 (Created)', function () {
    pm.response.to.have.status(201);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta contiene los datos del banco', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson).to.have.property('code');
    pm.expect(responseJson).to.have.property('name');
    pm.expect(responseJson).to.have.property('status');
    pm.expect(responseJson.status).to.be.true;
    pm.expect(responseJson.code).to.equal('002');
});

const responseJson = pm.response.json();
pm.environment.set('bankId_2', responseJson.id);
console.log('✓ Banco 2 creado con ID: ' + responseJson.id);
```

##### Actualizar banco - 200

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/update  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "id": {{bankId_1}},
  "idEnterprise": "{{enterpriseId}}",
  "code": "001",
  "name": "Banco de Pruebas 1 Actualizado",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Actualizando banco 1...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El banco fue actualizado correctamente', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson.name).to.equal('Banco de Pruebas 1 Actualizado');
});

console.log('✓ Banco 1 actualizado correctamente');
```

##### Listar por ID - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/findById/{{bankId_1}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando banco por ID...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta contiene los datos del banco', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson).to.have.property('code');
    pm.expect(responseJson).to.have.property('name');
    pm.expect(responseJson).to.have.property('status');
});

console.log('✓ Banco consultado correctamente');
```

##### Listar Todos - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/findAll/{{enterpriseId}}?page=0&size=10  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando todos los bancos...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta es paginada con contenido', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson).to.have.property('page');
    pm.expect(responseJson.page).to.have.property('totalElements');
    pm.expect(responseJson.page).to.have.property('totalPages');
    pm.expect(responseJson.content).to.be.an('array');
    pm.expect(responseJson.page.totalElements).to.be.at.least(2);
});

console.log('✓ Listado de bancos correcto');
```

##### Listar Todos con búsqueda - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/findAll/{{enterpriseId}}?page=0&size=10&search=Pruebas  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando bancos con término "Pruebas"...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta es paginada', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson).to.have.property('page');
    pm.expect(responseJson.page).to.have.property('totalElements');
});

console.log('✓ Búsqueda de bancos correcta');
```

##### Listar Todos ordenado por código desc - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/findAll/{{enterpriseId}}?page=0&size=10&sort=code,desc  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando bancos ordenados por código descendente...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta está ordenada correctamente', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('content');
    if (responseJson.content.length > 1) {
        for (let i = 0; i < responseJson.content.length - 1; i++) {
            pm.expect(responseJson.content[i].code >= responseJson.content[i + 1].code).to.be.true;
        }
    }
});

console.log('✓ Listado ordenado correcto');
```

##### Listar Bancos Activos - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/findAllActive/{{enterpriseId}}?page=0&size=10  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando bancos activos...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta es paginada con contenido activo', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson).to.have.property('page');
    pm.expect(responseJson.page).to.have.property('totalElements');
    if (responseJson.content.length > 0) {
        responseJson.content.forEach(function(item) {
            pm.expect(item.status).to.be.true;
        });
    }
});

console.log('✓ Listado de bancos activos correcto');
```

##### Cambiar Estado a inactivo - 200

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/changeState/{{bankId_1}}/{{enterpriseId}}?state=false  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Cambiando estado del banco 1 a inactivo...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El estado cambió a inactivo', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.status).to.be.false;
});

console.log('✓ Estado del banco cambiado a inactivo');
```

##### Cambiar Estado a activo - 200

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/changeState/{{bankId_1}}/{{enterpriseId}}?state=true  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Cambiando estado del banco 1 a activo...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El estado cambió a activo', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.status).to.be.true;
});

console.log('✓ Estado del banco cambiado a activo');
```

#### 2.1.2 Casos incorrectos

##### Crear banco sin token - 401

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** No aplica  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "code": "003",
  "name": "Banco sin token",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear banco sin token...');
```

**Test Script:**
```javascript
pm.test('Status code es 401 (Unauthorized)', function () {
    pm.response.to.have.status(401);
});

console.log('✓ Rechazado correctamente sin token');
```

##### Crear banco sin idEnterprise - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "code": "003",
  "name": "Banco sin empresa",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear banco sin idEnterprise...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que la empresa es obligatoria', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('empresa');
});

console.log('✓ Validación correcta: empresa obligatoria');
```

##### Crear banco con idEnterprise vacío - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "",
  "code": "003",
  "name": "Banco con empresa vacía",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear banco con idEnterprise vacío...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que la empresa es obligatoria', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('empresa');
});

console.log('✓ Validación correcta: empresa no puede estar vacía');
```

##### Crear banco sin code - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "name": "Banco sin código",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear banco sin code...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que el código es obligatorio', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('código') || pm.expect(responseText).to.include('code');
});

console.log('✓ Validación correcta: código obligatorio');
```

##### Crear banco con code vacío - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "code": "",
  "name": "Banco con código vacío",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear banco con code vacío...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que el código es obligatorio', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('código') || pm.expect(responseText).to.include('code');
});

console.log('✓ Validación correcta: código no puede estar vacío');
```

##### Crear banco sin name - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "code": "003",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear banco sin name...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que el nombre es obligatorio', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('nombre') || pm.expect(responseText).to.include('name');
});

console.log('✓ Validación correcta: nombre obligatorio');
```

##### Crear banco con name vacío - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "code": "003",
  "name": "",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear banco con name vacío...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que el nombre es obligatorio', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('nombre') || pm.expect(responseText).to.include('name');
});

console.log('✓ Validación correcta: nombre no puede estar vacío');
```

##### Crear banco con code duplicado - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "code": "001",
  "name": "Banco con código duplicado",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear banco con code duplicado...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica código duplicado', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('duplicado') || pm.expect(responseText).to.include('único');
});

console.log('✓ Validación correcta: código debe ser único');
```

##### Actualizar banco inexistente - 404

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/update  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "id": 999999,
  "idEnterprise": "{{enterpriseId}}",
  "code": "999",
  "name": "Banco inexistente",
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando actualizar banco inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not Found)', function () {
    pm.response.to.have.status(404);
});

console.log('✓ Validación correcta: banco no encontrado');
```

##### Consultar banco inexistente - 404

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/findById/999999/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando consultar banco inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not Found)', function () {
    pm.response.to.have.status(404);
});

console.log('✓ Validación correcta: banco no encontrado');
```

##### Cambiar estado banco inexistente - 404

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/changeState/999999/{{enterpriseId}}?state=false  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando cambiar estado de banco inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not Found)', function () {
    pm.response.to.have.status(404);
});

console.log('✓ Validación correcta: banco no encontrado');
```

### 2.2 Cuentas bancarias

#### 2.2.1 Casos correctos

##### Crear cuenta bancaria 1 (Ahorros) - 200

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "accountNumber": 12345678,
  "bankId": {{bankId_1}},
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Creando cuenta bancaria 1 (Ahorros)...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta contiene los datos de la cuenta bancaria', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson).to.have.property('accountNumber');
    pm.expect(responseJson).to.have.property('bank');
    pm.expect(responseJson).to.have.property('accountType');
    pm.expect(responseJson).to.have.property('accountingAccountId');
    pm.expect(responseJson).to.have.property('status');
    pm.expect(responseJson.status).to.be.true;
    pm.expect(responseJson.accountType).to.equal('AHORROS');
});

const responseJson = pm.response.json();
pm.environment.set('bankAccountId_1', responseJson.id);
console.log('✓ Cuenta bancaria 1 creada con ID: ' + responseJson.id);
```

##### Crear cuenta bancaria 2 (Corriente) - 200

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "accountNumber": 98765432,
  "bankId": {{bankId_2}},
  "accountType": "CORRIENTE",
  "accountingAccountId": {{accountId_11050502}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Creando cuenta bancaria 2 (Corriente)...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta contiene los datos de la cuenta bancaria', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson).to.have.property('accountNumber');
    pm.expect(responseJson).to.have.property('bank');
    pm.expect(responseJson.accountType).to.equal('CORRIENTE');
    pm.expect(responseJson.status).to.be.true;
});

const responseJson = pm.response.json();
pm.environment.set('bankAccountId_2', responseJson.id);
console.log('✓ Cuenta bancaria 2 creada con ID: ' + responseJson.id);
```

##### Actualizar cuenta bancaria 1 - 200

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/update  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "id": {{bankAccountId_1}},
  "idEnterprise": "{{enterpriseId}}",
  "accountNumber": 11112222,
  "bankId": {{bankId_1}},
  "accountType": "CORRIENTE",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Actualizando cuenta bancaria 1...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La cuenta bancaria fue actualizada correctamente', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson.accountNumber).to.equal(11112222);
    pm.expect(responseJson.accountType).to.equal('CORRIENTE');
});

console.log('✓ Cuenta bancaria 1 actualizada correctamente');
```

##### Consultar cuenta bancaria por ID - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/findById/{{bankAccountId_1}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando cuenta bancaria por ID...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta contiene los datos de la cuenta bancaria', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson).to.have.property('accountNumber');
    pm.expect(responseJson).to.have.property('bank');
    pm.expect(responseJson).to.have.property('accountType');
    pm.expect(responseJson).to.have.property('status');
});

console.log('✓ Cuenta bancaria consultada correctamente');
```

##### Listar todas las cuentas bancarias - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/findAll/{{enterpriseId}}?page=0&size=10  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando todas las cuentas bancarias...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta es paginada con contenido', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson).to.have.property('page');
    pm.expect(responseJson.page).to.have.property('totalElements');
    pm.expect(responseJson.page).to.have.property('totalPages');
    pm.expect(responseJson.content).to.be.an('array');
    pm.expect(responseJson.page.totalElements).to.be.at.least(2);
});

console.log('✓ Listado de cuentas bancarias correcto');
```

##### Listar cuentas bancarias con búsqueda - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/findAll/{{enterpriseId}}?page=0&size=10&search=1111  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando cuentas bancarias con "1111"...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta es paginada', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson).to.have.property('page');
    pm.expect(responseJson.page).to.have.property('totalElements');
});

console.log('✓ Búsqueda de cuentas bancarias correcta');
```

##### Listar cuentas bancarias activas - 200

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/findAllActive/{{enterpriseId}}?page=0&size=10  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Listando cuentas bancarias activas...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta es paginada con contenido activo', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson).to.have.property('page');
    pm.expect(responseJson.page).to.have.property('totalElements');
    if (responseJson.content.length > 0) {
        responseJson.content.forEach(function(item) {
            pm.expect(item.status).to.be.true;
        });
    }
});

console.log('✓ Listado de cuentas bancarias activas correcto');
```

##### Cambiar estado a inactivo - 200

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/changeState/{{bankAccountId_1}}/{{enterpriseId}}?state=false  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Cambiando estado de cuenta bancaria 1 a inactivo...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El estado cambió a inactivo', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.status).to.be.false;
});

console.log('✓ Estado de cuenta bancaria cambiado a inactivo');
```

##### Cambiar estado a activo - 200

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/changeState/{{bankAccountId_1}}/{{enterpriseId}}?state=true  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Cambiando estado de cuenta bancaria 1 a activo...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El estado cambió a activo', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.status).to.be.true;
});

console.log('✓ Estado de cuenta bancaria cambiado a activo');
```

#### 2.2.2 Casos incorrectos

##### Crear cuenta bancaria sin token - 401

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** No aplica  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "accountNumber": 55556666,
  "bankId": {{bankId_1}},
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta bancaria sin token...');
```

**Test Script:**
```javascript
pm.test('Status code es 401 (Unauthorized)', function () {
    pm.response.to.have.status(401);
});

console.log('✓ Rechazado correctamente sin token');
```

##### Crear cuenta bancaria sin idEnterprise - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "accountNumber": 55556666,
  "bankId": {{bankId_1}},
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta bancaria sin idEnterprise...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que la empresa es obligatoria', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('empresa');
});

console.log('✓ Validación correcta: empresa obligatoria');
```

##### Crear cuenta bancaria con idEnterprise vacío - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "",
  "accountNumber": 55556666,
  "bankId": {{bankId_1}},
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta bancaria con idEnterprise vacío...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que la empresa es obligatoria', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('empresa');
});

console.log('✓ Validación correcta: empresa no puede estar vacía');
```

##### Crear cuenta bancaria sin accountNumber - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "bankId": {{bankId_1}},
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta bancaria sin accountNumber...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que el número de cuenta es obligatorio', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('cuenta');
});

console.log('✓ Validación correcta: número de cuenta obligatorio');
```

##### Crear cuenta bancaria con accountNumber menor a 8 dígitos - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "accountNumber": 1234567,
  "bankId": {{bankId_1}},
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta bancaria con accountNumber menor a 8 dígitos...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica error en número de cuenta', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('8');
});

console.log('✓ Validación correcta: mínimo 8 dígitos');
```

##### Crear cuenta bancaria con accountNumber mayor a 16 dígitos - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "accountNumber": 12345678901234567,
  "bankId": {{bankId_1}},
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta bancaria con accountNumber mayor a 16 dígitos...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica error en número de cuenta', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('16');
});

console.log('✓ Validación correcta: máximo 16 dígitos');
```

##### Crear cuenta bancaria con accountNumber negativo - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "accountNumber": -12345678,
  "bankId": {{bankId_1}},
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta bancaria con accountNumber negativo...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica error en número de cuenta', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('positivo') || pm.expect(responseText).to.include('cuenta');
});

console.log('✓ Validación correcta: número de cuenta debe ser positivo');
```

##### Crear cuenta bancaria sin bankId - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
  "idEnterprise": "{{enterpriseId}}",
  "accountNumber": 55556666,
  "accountType": "AHORROS",
  "accountingAccountId": {{accountId_11050501}},
  "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear cuenta bancaria sin bankId...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica que el banco es obligatorio', function () {
    const responseJson = pm.response.json();
    const responseText = JSON.stringify(responseJson).toLowerCase();
    pm.expect(responseText).to.include('banco');
});

console.log('✓ Validación correcta: banco obligatorio');
```

## 4 Tear Down

### Eliminar cuenta bancaria 1 - 200

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/delete/{{bankAccountId_1}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Eliminando cuenta bancaria 1...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

console.log('✓ Cuenta bancaria 1 eliminada correctamente');
```

### Verificar eliminación cuenta bancaria 1 - 404

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/findById/{{bankAccountId_1}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Verificando eliminación de cuenta bancaria 1...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not Found)', function () {
    pm.response.to.have.status(404);
});

console.log('✓ Verificación correcta: cuenta bancaria 1 ya no existe');
```

### Eliminar cuenta bancaria 2 - 200

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/bank-accounts/delete/{{bankAccountId_2}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Eliminando cuenta bancaria 2...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

console.log('✓ Cuenta bancaria 2 eliminada correctamente');
```

### Eliminar banco 1 - 200

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/delete/{{bankId_1}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Eliminando banco 1...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

console.log('✓ Banco 1 eliminado en tear down');
```

### Eliminar banco 2 - 200

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/delete/{{bankId_2}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Eliminando banco 2...');
```

**Test Script:**
```javascript
pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

console.log('✓ Banco 2 eliminado en tear down');
console.log('=== FIN DE PRUEBAS DE CUENTAS BANCARIAS ===');
```

### Verificar eliminación - 404

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/banks/findById/{{bankId_1}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Verificando eliminación de bancos...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not found)', function () {
    pm.response.to.have.status(404);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El banco fue eliminado', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.message).to.not.be.true;
});

console.log('✓ Verificado: el banco ya no existe');
```

## Notas de ejecución

1. **Configuración inicial:** Antes de ejecutar la colección, asegúrese de que las variables de entorno estén configuradas correctamente (baseUrl, enterpriseId, keycloakUser, keycloakPassword).

2. **Orden de ejecución:** La colección está organizada en carpetas que deben ejecutarse en orden: 1 Token, 2 Init, 3 Integration, 4 Tear Down.

3. **Dependencias:** Los tests de Integration dependen de que los bancos y cuentas bancarias se hayan creado correctamente en las fases anteriores.

4. **Limpieza:** La carpeta Tear Down elimina todas las cuentas bancarias y bancos creados durante los tests para mantener la base de datos limpia.

5. **Variables dinámicas:** La colección utiliza variables de colección y de entorno para almacenar IDs generados dinámicamente durante la ejecución.

6. **Jerarquía de cuentas:** Los tests crean una jerarquía de cuentas contables de 6 niveles para apoyar las cuentas bancarias.
