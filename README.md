# Plataforma de Serviços de Restauração de Imagens

Plataforma desenvolvida para prestação de serviços de restauração de imagens, com foco em automação, segurança, e fluxo financeiro integrado. 

A aplicação é dividida no modelo "Monólito Modular" contendo os seguintes diretórios:
- **`backend/`**: API RESTful em Java 21 / Spring Boot 3.x
- **`frontend-landing/`**: Aplicação Next.js para Landing Page e SEO.
- **`frontend-panel/`**: Aplicação React/Vite para Painéis Administrativo e do Cliente.
- **`infra/`**: Configurações de infraestrutura Docker (Postgres, Redis, RabbitMQ, MinIO).

---

## Pré-requisitos

Para executar a aplicação localmente, você precisará ter instalado:
1. **Docker e Docker Compose** (Para rodar os bancos de dados, cache, mensageria e o MinIO)
2. **Java 21 (JDK)** (Para compilar e rodar o Backend via Maven)
3. **Node.js (versão 18+) e NPM** (Para rodar os Frontends)

---

## Passo a Passo para Execução Local

### Passo 1: Subir a Infraestrutura (Docker)
Antes de rodar o código, precisamos iniciar os bancos e dependências:
```bash
cd infra
docker compose up -d
```
> Isso irá iniciar o PostgreSQL na porta `5432`, Redis na `6379`, RabbitMQ na `5672` (e painel em `15672`), além do MinIO (S3 Local) na porta `9000`.

### Passo 2: Executar o Backend (Spring Boot)
O backend usa o Flyway, então ao subir a primeira vez, as tabelas do banco serão criadas automaticamente.
```bash
cd backend

# Se for Linux/macOS
./mvnw spring-boot:run

# Se for Windows
.\mvnw.cmd spring-boot:run
```
> O backend rodará na porta `8080`.

### Passo 3: Executar a Landing Page (Next.js)
Abra uma nova aba do terminal.
```bash
cd frontend-landing
npm install
npm run dev
```
> O Next.js iniciará na porta `3000`.

### Passo 4: Executar o Painel Administrativo / Cliente (Vite)
Abra outra aba do terminal.
```bash
cd frontend-panel
npm install
npm run dev
```
> O Vite iniciará por padrão na porta `5173`.

---

## Verificação do Módulo 1 (Autenticação)

Se você já possui o banco rodando e subiu a API, você pode testar o fluxo de registro e login com comandos `cURL` ou via Postman/Insomnia:

### Teste de Registro:
```bash
curl -X POST http://localhost:8080/auth/register \
     -H "Content-Type: application/json" \
     -d '{
           "name": "Cliente Teste",
           "email": "cliente@teste.com",
           "password": "senhaSegura123"
         }'
```
> O retorno será um objeto JSON contendo o token JWT (`{"token": "..."}`).

### Teste de Login:
```bash
curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{
           "email": "cliente@teste.com",
           "password": "senhaSegura123"
         }'
```
> Novamente, o retorno será o token válido para você utilizar no cabeçalho `Authorization: Bearer <TOKEN>` nas demais rotas protegidas que serão criadas no futuro.
