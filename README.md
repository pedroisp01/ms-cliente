# Desafio MS-Cliente

Este microserviço foi desenvolvido como parte de um desafio técnico.

---

## Tecnologias e Frameworks

* **Java 11 & Spring Boot 2.7.18**
* **Spring Security OAuth2**
* **RabbitMQ**
* **H2 Database**
* **MapStruct & Lombok**
* **JUnit 5 & Mockito** 
* **Docker & Docker Compose**

---

## Arquitetura e Fluxo de Negócio

O sistema utiliza um fluxo de **Ativação Assíncrona**:

1. **Cadastro**
   O cliente é salvo no banco de dados com o status inicial `PENDENTE`.

2. **Mensageria**
   O `ClienteProducer` envia os dados do cliente para uma fila no RabbitMQ.

3. **Processamento**
   O `ClienteConsumer` escuta a fila, recupera o cliente e altera seu status para `ATIVO`.

---

### Script de Inicialização (data.sql)

O projeto já conta com um script de carga inicial para facilitar a homologação imediata após o `docker-compose up`.

### Usuários e Permissões (RBAC)

Foram configurados três níveis de acesso para testar a segurança OAuth2:

| Usuário   | Senha | Role          | Descrição                                      |
| --------- | ----- | ------------- | ---------------------------------------------- |
| admin     | 123   | ROLE_ADMIN    | Acesso total (incluindo exclusão de clientes). |
| pedro     | 123   | ROLE_USER     | Pode cadastrar, listar e alterar clientes.     |
| visitante | 123   | ROLE_READONLY | Acesso restrito apenas para leitura (GET).     |

---

### Clientes Pré-cadastrados

O banco H2 é populado automaticamente com registros de exemplo para validar a listagem e os filtros:

* Teste Brasil *(Status: ATIVO)*
* Testando Technology *(Status: ATIVO)*
* Jose Silva *(Status: ATIVO)*

---

**Nota:**
As senhas estão armazenadas utilizando o algoritmo de hash **BCrypt**, garantindo as melhores práticas de segurança.

## Testes com Postman

Para facilitar os testes, disponibilizei os arquivos de exportação na pasta raiz:

* Importe o arquivo **MS-Cliente.postman_collection.json** no seu Postman.
* Importe o arquivo **Desafio.postman_environment.json**.
* Certifique-se de selecionar o ambiente **Desafio** no canto superior direito antes de realizar as requisições.

Isso garantirá que as variáveis de ambiente (como o token OAuth2) sejam configuradas corretamente durante os testes.


## Como Executar o Projeto

### Pré-requisitos

* Docker
* Docker Compose

### Passo a Passo

Na raiz do projeto, execute o comando abaixo para buildar a imagem da API e subir o RabbitMQ:

```bash
docker-compose up --build -d
```

Aguarde o log indicar que o sistema está pronto:

```bash
docker logs -f ms-cliente-app
```

---

## Segurança e Testes no Postman

### 1️. Obter Token OAuth2

Para autenticar na API, é necessário gerar um token utilizando o fluxo **Password Grant**.

Faça uma requisição **POST** para:

```
http://localhost:8080/oauth/token
```

### Headers

```
Authorization: Basic
Content-Type: application/x-www-form-urlencoded
```

### Body (form-data / x-www-form-urlencoded)

| Key        | Value    |
| ---------- |----------|
| grant_type | password |
| username   | admin    |
| password   | 123      |

---

### cURL

```bash
  curl --location --request POST 'http://localhost:8080/oauth/token' \
    --header 'Authorization: Basic Y2xpZW50LWlkOmNsaWVudC1zZWNyZXQ=' \
    --form 'grant_type=password' \
    --form 'username=admin' \
    --form 'password=123' \
    --user 'client-id:client-secret'
```

---

### Como fazer no Postman

1. Crie uma requisição **POST**
2. URL: `http://localhost:8080/oauth/token`
3. Vá na aba **Authorization**

    * Type: **Basic Auth**
    * Username: `client-id`
    * Password: `client-secret`
4. Vá na aba **Body**

    * Selecione **x-www-form-urlencoded**
    * Adicione:

        * `grant_type` → `password`
        * `username` → `admin`
        * `password` → `123`

---

### Salvar token automaticamente (opcional)

Na aba **Tests** eu adicionei um script para armazenar o token automaticamente na variavel **token**:

```javascript
if (pm.response.code === 200) {
    pm.environment.set("token", pm.response.json().access_token);
}
```

---

### Usar o token nas requisições

Após obter o token, adicione o header:

```
Authorization: Bearer {{token}}
```

---

### 2️. Criar Cliente

Com o token obtido, faça um **POST** para:

```
/api/clientes
```

```json
{
  "nome": "Pedro Azevedo",
  "email": "pedro@email.com",
  "cpf": "12345678901"
}
```

---

### 3. Buscar todos clientes

Faça um **GET** para:

```
/api/clientes
```

Exemplo de resposta:
```json
{
  "id": 1,
  "nome": "Pedro Azevedo",
  "email": "pedro@email.com",
  "cpf": "12345678901",
  "status": "ATIVO"
}
```

---

### 4. Buscar cliente por ID

Faça um **GET** para:

```
/api/clientes/buscar
```

### Params

| Key      | Value |
|----------|-------|
| id       | 1     |


Exemplo de resposta:
```json
{
  "id": 1,
  "nome": "Pedro Azevedo",
  "email": "pedro@email.com",
  "cpf": "12345678901",
  "status": "ATIVO"
}
```

---

### 5. Alterar Cliente

Faça um **PUT** para:

```
/api/clientes/alterar
```
### Params

| Key      | Value |
|----------|-------|
| id       | 1     |


### Body (raw)
### (*Campos opcionais) mas tem que enviar pelo menos um deles
```json
{
  "nome": "Pedro Azevedo",
  "email": "pedro@email.com",
  "cpf": "12345678901"
}
```

Exemplo de resposta:
```json
{
  "id": 1,
  "nome": "Pedro Azevedo",
  "email": "pedro@email.com",
  "cpf": "12345678901",
  "status": "ATIVO"
}
```

### 6. Deletar Cliente (Apenas user com role_admin)

Faça um **DELETE** para:

```
/api/clientes/apagar
```
### Params

| Key      | Value |
|----------|-------|
| id       | 1     |


Exemplo de resposta:
```json
"204 no content"
```

### 7. Validar no Banco (H2 Console) (Opcional)

Acesse:

```
http://localhost:8080/h2-console
```

**Configurações:**

* JDBC URL: `jdbc:h2:mem:clientedb`
* User: `sa`
* Senha: *(vazia)*

```sql
SELECT * FROM cliente;
```

---

## Testes Unitários

Para validar as regras de negócio sem subir o ambiente Docker, execute:

```bash
  mvn clean test
```

### Cobertura inclui:

* Validação de CPF duplicado
* Transição de status por Enum
* Garantia de tratamento de exceções (`BusinessException`)

---

### Validação de Regras de Negócio

No método `salvar`, foi implementada uma validação para garantir a integridade dos dados.

**Regra de Negócio:**
O sistema impede o cadastro de **CPFs duplicados**, lançando uma `BusinessException` que é tratada globalmente para retornar o **status HTTP apropriado** ao cliente.

---

## Autor

**Pedro Azevedo**

---
