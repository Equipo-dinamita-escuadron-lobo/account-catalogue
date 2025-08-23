# Cuenta Contable Inmutable en Métodos de Pago

## Resumen del Cambio

Se ha implementado una restricción que **impide modificar la cuenta contable** (`accountingAccount`) de un método de pago una vez que ha sido creado. Solo se permite modificar el nombre y el estado del método de pago.

## Razón del Cambio

Esta restricción garantiza la integridad contable y evita inconsistencias en los registros financieros una vez que un método de pago está en uso.

## Cambios Implementados

### 1. Nueva Excepción: `AccountingAccountImmutableException`
- **Ubicación**: `commons/exceptions/paymentMethods/AccountingAccountImmutableException.java`
- **Código de Error**: `ACCOUNTING_ACCOUNT_IMMUTABLE`
- **Mensaje**: "La cuenta contable no puede ser modificada una vez creado el método de pago"

### 2. Validación en el Servicio
- **Archivo**: `PaymentMethodServiceImpl.java`
- **Método**: `update()`
- **Validación**: Compara la cuenta contable actual con la solicitada y lanza excepción si son diferentes

### 3. Documentación en DTO
- **Archivo**: `PaymentMethodUpdateReq.java`
- **Campo**: `accountingAccount`
- **Comentario**: Explica que el campo no puede ser modificado y debe enviarse con el valor actual

## Comportamiento del Sistema

### ✅ Campos Modificables
- `name` - Nombre del método de pago
- `status` - Estado (activo/inactivo)

### ❌ Campos NO Modificables
- `accountingAccount` - Cuenta contable (inmutable después de la creación)
- `id` - Identificador único
- `idEnterprise` - ID de la empresa

## Ejemplo de Uso

### ✅ Actualización Correcta
```json
{
  "id": 1,
  "idEnterprise": "EMP001",
  "name": "Efectivo - Caja Principal",
  "accountingAccount": "11050001",  // Mismo valor que tiene actualmente
  "status": true
}
```

### ❌ Actualización que Genera Error
```json
{
  "id": 1,
  "idEnterprise": "EMP001", 
  "name": "Efectivo",
  "accountingAccount": "11050002",  // Valor diferente al actual
  "status": true
}
```

**Error Generado**:
```json
{
  "code": "ACCOUNTING_ACCOUNT_IMMUTABLE",
  "message": "No se puede modificar la cuenta contable. Cuenta actual: '11050001', cuenta solicitada: '11050002'"
}
```

## Flujo de Validación

1. **Buscar método de pago** por ID y empresa (solo activos)
2. **Comparar cuenta contable** actual vs solicitada
3. **Si son diferentes**: Lanzar `AccountingAccountImmutableException`
4. **Si son iguales**: Continuar con validaciones normales
5. **Actualizar** solo nombre y estado

## Consideraciones Técnicas

### Para el Frontend
- Al cargar un método de pago para edición, deshabilitar el campo `accountingAccount`
- Enviar siempre el valor actual de `accountingAccount` en requests de actualización
- Manejar la excepción `ACCOUNTING_ACCOUNT_IMMUTABLE` para mostrar mensaje apropiado

### Para Testing
- Verificar que no se pueda cambiar cuenta contable en actualizaciones
- Probar que nombre y estado sí se puedan modificar
- Validar que la excepción se lance con el mensaje correcto

## API Endpoints Afectados

### PUT `/api/accountCatalogue/payment-methods/update`
- **Validación Adicional**: Cuenta contable inmutable
- **Nuevos Errores Posibles**: `ACCOUNTING_ACCOUNT_IMMUTABLE`
- **Campos Actualizables**: Solo `name` y `status`

### Otros Endpoints (sin cambios)
- `POST /create` - Permite especificar cuenta contable inicial
- `GET /findById` - Sin cambios
- `GET /findAll` - Sin cambios
- `DELETE /delete` - Sin cambios (soft delete)

## Migración y Compatibilidad

### ✅ Retrocompatibilidad
- Los métodos de pago existentes no se ven afectados
- Las aplicaciones que envían la cuenta contable correcta siguen funcionando

### ⚠️ Cambios Requeridos
- Aplicaciones que intentan modificar `accountingAccount` necesitarán actualizarse
- Implementar manejo de la nueva excepción en el frontend

## Próximos Pasos Recomendados

1. **Actualizar Frontend**: Deshabilitar edición de cuenta contable
2. **Actualizar Tests**: Agregar casos de prueba para la nueva validación
3. **Documentar API**: Actualizar documentación de endpoints
4. **Comunicar Cambio**: Notificar a equipos que consumen la API
