# Documentación de Pruebas de Integración - MÉTODOS DE PAGO

Esta colección de Postman contiene pruebas de integración para el módulo de Métodos de Pago del sistema ContApp. Las pruebas cubren operaciones CRUD completas para métodos de pago, validaciones de negocio, y manejo de errores.

## Variables de Colección

- `testRunId`: Identificador único para la ejecución de tests
- `timestamp`: Marca de tiempo para logs
- `tokenKeycloak`: Token de autenticación JWT de Keycloak
- `accountsHierarchy`: Jerarquía de cuentas en formato JSON
- `currentAccountIndex`: Índice actual para creación de cuentas
- `createdAccounts`: IDs de cuentas creadas durante los tests
- `paymentMethodId_1`: ID del primer método de pago creado
- `paymentMethodId_2`: ID del segundo método de pago creado
- `paymentMethodId_3`: ID del tercer método de pago creado 
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
    
    // Guardar IDs específicos para métodos de pago
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

### 3.1 Casos correctos

#### Crear método de pago - Efectivo

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "name": "Efectivo",
    "accountingAccountId": {{accountId_11050501}}
}
```

**Pre-request Script:**
```javascript
console.log('Creando método de pago Efectivo...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El método de pago contiene ID', function () {
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson.id).to.be.a('number');
});

pm.test('El nombre es correcto', function () {
    pm.expect(responseJson.name).to.eql('Efectivo');
});

pm.test('La cuenta contable está asignada', function () {
    pm.expect(responseJson.accountingAccountId).to.not.be.null;
});

pm.test('El estado es activo por defecto', function () {
    pm.expect(responseJson.status).to.eql(true);
});

if (pm.response.code === 200 && responseJson.id) {
    pm.environment.set('paymentMethodId_1', responseJson.id);
    console.log('Método de pago Efectivo creado. ID: ' + responseJson.id);
}
```

#### Crear método de pago - Tarjeta de Crédito

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "name": "Tarjeta de Crédito",
    "accountingAccountId": {{accountId_11050502}}
}
```

**Pre-request Script:**
```javascript
console.log('Creando método de pago Tarjeta de Crédito...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El método de pago contiene ID', function () {
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson.id).to.be.a('number');
});

pm.test('El nombre es correcto', function () {
    pm.expect(responseJson.name).to.eql('Tarjeta de Crédito');
});

pm.test('La cuenta contable está asignada', function () {
    pm.expect(responseJson.accountingAccountId).to.not.be.null;
});

pm.test('El estado es activo por defecto', function () {
    pm.expect(responseJson.status).to.eql(true);
});

if (pm.response.code === 200 && responseJson.id) {
    pm.environment.set('paymentMethodId_2', responseJson.id);
    console.log('Método de pago Tarjeta de Crédito creado. ID: ' + responseJson.id);
}
```

#### Crear método de pago - Transferencia Bancaria

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "name": "Transferencia Bancaria",
    "accountingAccountId": {{accountId_11050501}}
}
```

**Pre-request Script:**
```javascript
console.log('Creando método de pago Transferencia Bancaria...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El método de pago contiene ID', function () {
    pm.expect(responseJson).to.have.property('id');
    pm.expect(responseJson.id).to.be.a('number');
});

pm.test('El nombre es correcto', function () {
    pm.expect(responseJson.name).to.eql('Transferencia Bancaria');
});

pm.test('La cuenta contable está asignada', function () {
    pm.expect(responseJson.accountingAccountId).to.not.be.null;
});

pm.test('El estado es activo por defecto', function () {
    pm.expect(responseJson.status).to.eql(true);
});

if (pm.response.code === 200 && responseJson.id) {
    pm.environment.set('paymentMethodId_3', responseJson.id);
    console.log('Método de pago Transferencia Bancaria creado. ID: ' + responseJson.id);
}
```

#### Consultar método de pago por ID

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/findById/{{paymentMethodId_1}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando método de pago por ID...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();
const paymentMethodId = parseInt(pm.environment.get('paymentMethodId_1'));

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El ID coincide con el solicitado', function () {
    pm.expect(responseJson.id).to.eql(paymentMethodId);
});

pm.test('El nombre está presente', function () {
    pm.expect(responseJson.name).to.not.be.undefined;
    pm.expect(responseJson.name).to.be.a('string');
});

pm.test('La cuenta contable está presente', function () {
    pm.expect(responseJson.accountingAccountId).to.not.be.null;
});

pm.test('El estado está presente', function () {
    pm.expect(responseJson.status).to.not.be.undefined;
});

console.log('✓ Método de pago consultado: ' + responseJson.name);
```

#### Consultar todos los métodos de pago

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/findAll/{{enterpriseId}}?page=0&size=10  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando todos los métodos de pago...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La respuesta contiene paginación', function () {
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson).to.have.property('page');
    pm.expect(responseJson.page).to.have.property('totalElements');
    pm.expect(responseJson.page).to.have.property('totalPages');
});

pm.test('Hay al menos 3 métodos de pago creados', function () {
    pm.expect(responseJson.page.totalElements).to.be.at.least(3);
});

pm.test('El contenido es un array', function () {
    pm.expect(responseJson.content).to.be.an('array');
});

console.log('✓ Total de métodos de pago: ' + responseJson.page.totalElements);
```

#### Consultar con paginación

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/findAll/{{enterpriseId}}?page=1&size=2  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando con paginación (página 1, tamaño 2)...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La paginación funciona correctamente', function () {
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson).to.have.property('page');
    pm.expect(responseJson.page.number).to.eql(1);
    pm.expect(responseJson.page.size).to.eql(2);
});

console.log('✓ Paginación correcta - Página: ' + responseJson.page.number + ', Tamaño: ' + responseJson.page.size);
```

#### Consultar con búsqueda por nombre

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/findAll/{{enterpriseId}}?page=0&size=10&search=Efectivo  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Buscando métodos de pago por nombre "Efectivo"...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('La búsqueda funciona', function () {
    pm.expect(responseJson).to.have.property('content');
    pm.expect(responseJson.content).to.be.an('array');
});

pm.test('Los resultados contienen el término buscado', function () {
    if (responseJson.content.length > 0) {
        responseJson.content.forEach(function(item) {
            pm.expect(item.name.toLowerCase()).to.include('efectivo');
        });
    }
});

console.log('✓ Búsqueda correcta - Resultados encontrados: ' + responseJson.content.length);
```

#### Consultar métodos activos

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/findAllActive/{{enterpriseId}}?page=0&size=10  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Consultando métodos de pago activos...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('Solo devuelve métodos activos', function () {
    pm.expect(responseJson).to.have.property('content');
    if (responseJson.content.length > 0) {
        responseJson.content.forEach(function(item) {
            pm.expect(item.status).to.eql(true);
        });
    }
});

console.log('✓ Métodos activos consultados: ' + responseJson.content.length);
```

#### Actualizar método de pago

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/update  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "id": {{paymentMethodId_1}},
    "idEnterprise": "{{enterpriseId}}",
    "name": "Efectivo Actualizado",
    "accountingAccountId": {{accountId_11050501}},
    "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Actualizando método de pago...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();
const paymentMethodId = parseInt(pm.environment.get('paymentMethodId_1'));

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El ID es el correcto', function () {
    pm.expect(responseJson.id).to.eql(paymentMethodId);
});

pm.test('El nombre fue actualizado', function () {
    pm.expect(responseJson.name).to.eql('Efectivo Actualizado');
});

console.log('✓ Método de pago actualizado: ' + responseJson.name);
```

#### Cambiar estado a inactivo

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/changeState/{{paymentMethodId_2}}/{{enterpriseId}}?state=false  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Cambiando estado a inactivo...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();
const paymentMethodId = parseInt(pm.environment.get('paymentMethodId_2'));

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El ID es el correcto', function () {
    pm.expect(responseJson.id).to.eql(paymentMethodId);
});

pm.test('El estado fue cambiado a inactivo', function () {
    pm.expect(responseJson.status).to.eql(false);
});

console.log('✓ Estado cambiado a inactivo para: ' + responseJson.name);
```

#### Cambiar estado a activo

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/changeState/{{paymentMethodId_2}}/{{enterpriseId}}?state=true  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Cambiando estado a activo...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();
const paymentMethodId = parseInt(pm.environment.get('paymentMethodId_2'));

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El ID es el correcto', function () {
    pm.expect(responseJson.id).to.eql(paymentMethodId);
});

pm.test('El estado fue cambiado a activo', function () {
    pm.expect(responseJson.status).to.eql(true);
});

console.log('✓ Estado cambiado a activo para: ' + responseJson.name);
```

### 3.2 Casos incorrectos

#### Crear sin nombre - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "accountingAccountId": {{accountId_11050501}}
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear método de pago sin nombre...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica error de validación', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.message || responseJson.error).to.not.be.undefined;
});

console.log('✓ Validación correcta: nombre es obligatorio');
```

#### Crear sin idEnterprise - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "name": "Método sin empresa",
    "accountingAccountId": {{accountId_11050501}}
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear método de pago sin idEnterprise...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica error de validación', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.message || responseJson.error).to.not.be.undefined;
});

console.log('✓ Validación correcta: idEnterprise es obligatorio');
```

#### Crear sin accountingAccountId - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "name": "Método sin cuenta contable"
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear método de pago sin accountingAccountId...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica error de validación', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.message || responseJson.error).to.not.be.undefined;
});

console.log('✓ Validación correcta: accountingAccountId es obligatorio');
```

#### Crear con accountingAccountId inexistente - 404

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "name": "Método con cuenta inexistente",
    "accountingAccountId": 999999
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear método de pago con accountingAccountId inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not Found)', function () {
    pm.response.to.have.status(404);
});

console.log('✓ Validación correcta: accountingAccountId debe existir');
```

#### Crear con nombre duplicado - 400

**Método:** POST  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/create  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "idEnterprise": "{{enterpriseId}}",
    "name": "Efectivo",
    "accountingAccountId": {{accountId_11050501}}
}
```

**Pre-request Script:**
```javascript
console.log('Intentando crear método de pago con nombre duplicado...');
```

**Test Script:**
```javascript
pm.test('Status code es 400 (Bad Request)', function () {
    pm.response.to.have.status(400);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El mensaje indica nombre duplicado', function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson.message || responseJson.error).to.not.be.undefined;
});

console.log('✓ Validación correcta: nombre debe ser único');
```

#### Consultar por ID inexistente - 404

**Método:** GET  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/findById/999999/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando consultar método de pago inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not Found)', function () {
    pm.response.to.have.status(404);
});

console.log('✓ Validación correcta: método de pago no encontrado');
```

#### Actualizar método inexistente - 404

**Método:** PUT  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/update  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** 
- Content-Type: application/json  

**Body:**
```json
{
    "id": 999999,
    "idEnterprise": "{{enterpriseId}}",
    "name": "Método inexistente",
    "accountingAccountId": {{accountId_11050501}},
    "status": true
}
```

**Pre-request Script:**
```javascript
console.log('Intentando actualizar método de pago inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not Found)', function () {
    pm.response.to.have.status(404);
});

console.log('✓ Validación correcta: método de pago no encontrado para actualizar');
```

#### Cambiar estado de método inexistente - 404

**Método:** PATCH  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/changeState/999999/{{enterpriseId}}?state=false  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Intentando cambiar estado de método de pago inexistente...');
```

**Test Script:**
```javascript
pm.test('Status code es 404 (Not Found)', function () {
    pm.response.to.have.status(404);
});

console.log('✓ Validación correcta: método de pago no encontrado para cambiar estado');
```

## 4 Tear Down

### Eliminar método de pago 1 - Efectivo

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/delete/{{paymentMethodId_1}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Eliminando método de pago 1 - Efectivo...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();
const paymentMethodId = parseInt(pm.environment.get('paymentMethodId_1'));

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El ID eliminado es el correcto', function () {
    pm.expect(responseJson.id).to.eql(paymentMethodId);
});

console.log('✓ Método de pago 1 eliminado: ' + responseJson.name);
pm.environment.unset('paymentMethodId_1');
```

### Eliminar método de pago 2 - Tarjeta

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/delete/{{paymentMethodId_2}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Eliminando método de pago 2 - Tarjeta...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();
const paymentMethodId = parseInt(pm.environment.get('paymentMethodId_2'));

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El ID eliminado es el correcto', function () {
    pm.expect(responseJson.id).to.eql(paymentMethodId);
});

console.log('✓ Método de pago 2 eliminado: ' + responseJson.name);
pm.environment.unset('paymentMethodId_2');
```

### Eliminar método de pago 3 - Transferencia

**Método:** DELETE  
**URL:** {{baseUrl}}/api/accountCatalogue/payment-methods/delete/{{paymentMethodId_3}}/{{enterpriseId}}  
**Autenticación:** Bearer Token ({{tokenKeycloak}})  
**Headers:** No aplica  

**Pre-request Script:**
```javascript
console.log('Eliminando método de pago 3 - Transferencia...');
```

**Test Script:**
```javascript
const responseJson = pm.response.json();
const paymentMethodId = parseInt(pm.environment.get('paymentMethodId_3'));

pm.test('Status code es 200 (OK)', function () {
    pm.response.to.have.status(200);
});

pm.test('La respuesta es JSON válido', function () {
    pm.response.to.be.json;
});

pm.test('El ID eliminado es el correcto', function () {
    pm.expect(responseJson.id).to.eql(paymentMethodId);
});

console.log('✓ Método de pago 3 eliminado: ' + responseJson.name);
pm.environment.unset('paymentMethodId_3');
console.log('=== FIN DE PRUEBAS DE MÉTODOS DE PAGO ===');
```

## Notas de ejecución

1. **Configuración inicial:** Antes de ejecutar la colección, asegúrese de que las variables de entorno estén configuradas correctamente (baseUrl, enterpriseId, keycloakUser, keycloakPassword).

2. **Orden de ejecución:** La colección está organizada en carpetas que deben ejecutarse en orden: 1 Token, 2 Init, 3 Integration, 4 Tear Down.

3. **Dependencias:** Los tests de Integration dependen de que la jerarquía de cuentas se haya creado correctamente en la fase Init.

4. **Limpieza:** La carpeta Tear Down elimina todos los métodos de pago creados durante los tests para mantener la base de datos limpia.

5. **Variables dinámicas:** La colección utiliza variables de colección y de entorno para almacenar IDs generados dinámicamente durante la ejecución.

6. **Jerarquía de cuentas:** Los tests crean una jerarquía de cuentas contables de 6 niveles para apoyar los métodos de pago.

7. **Validaciones:** Se incluyen casos de error para probar validaciones de negocio como unicidad de nombres, campos obligatorios, y existencia de referencias.