ALTER TABLE payment_methods
    ADD COLUMN IF NOT EXISTS requires_bank_account BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE accounting_entries DROP CONSTRAINT IF EXISTS accounting_entries_code_key;
CREATE UNIQUE INDEX IF NOT EXISTS uk_accounting_tenant_source_type
    ON accounting_entries(tenant_id, source_document_id, type);
CREATE UNIQUE INDEX IF NOT EXISTS uk_accounting_tenant_code
    ON accounting_entries(tenant_id, code);
