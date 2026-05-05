-- Criação do schema inicial
-- Inicialmente vazio para inicialização do Flyway, mas pode ser expandido depois.

CREATE TABLE IF NOT EXISTS flyway_init_test (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
