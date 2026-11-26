INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'ADMIN', 'Administrador del sistema', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name, description, created_at, updated_at)
SELECT 'CONTADOR', 'Contador del sistema', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CONTADOR');