DELETE FROM usuario;
DELETE FROM cliente;

-- Senha padrão para todos: 123 BCrypt hash

-- 1. Usuário (ROLE_ADMIN)
INSERT INTO usuario (username, password, role)
VALUES ('admin', '$2a$10$W0qLJ2yHNa1xQVuUkAZigun176FzGGDxL8ywx0pS0q9oCeSRZR9Au', 'ROLE_ADMIN');

-- 2. Usuário (ROLE_USER)
INSERT INTO usuario (username, password, role)
VALUES ('pedro', '$2a$10$W0qLJ2yHNa1xQVuUkAZigun176FzGGDxL8ywx0pS0q9oCeSRZR9Au', 'ROLE_USER');

-- 3. Usuário (ROLE_READONLY)
INSERT INTO usuario (username, password, role)
VALUES ('visitante', '$2a$10$W0qLJ2yHNa1xQVuUkAZigun176FzGGDxL8ywx0pS0q9oCeSRZR9Au', 'ROLE_READONLY');


INSERT INTO cliente (nome, email, cpf, status)
VALUES ('Teste Brasil', 'teste@brasil.com.br', '11122233344', 'ATIVO');

INSERT INTO cliente (nome, email, cpf, status)
VALUES ('Testando Technology', 'testando@tech.com', '55566677788', 'ATIVO');

INSERT INTO cliente (nome, email, cpf, status)
VALUES ('Jose Silva', 'jose@gmail.com', '99988877766', 'ATIVO');