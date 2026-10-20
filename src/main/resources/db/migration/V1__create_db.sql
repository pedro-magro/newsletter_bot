CREATE TABLE usuario (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    telegram_id BIGINT NOT NULL UNIQUE,
    chat_id BIGINT NOT NULL UNIQUE,
    nome VARCHAR(120),
    username VARCHAR(120),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    frequencia VARCHAR(20) NOT NULL DEFAULT 'DIARIA',
    horario_envio TIME NOT NULL DEFAULT '09:00',
    timezone VARCHAR(60) NOT NULL DEFAULT 'America/Sao_Paulo',
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE assunto (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    slug VARCHAR(80) NOT NULL UNIQUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario_assunto (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    assunto_id BIGINT NOT NULL REFERENCES assunto(id),
    PRIMARY KEY (usuario_id, assunto_id)
);

CREATE TABLE provedores (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    prioridade INT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    limite_diario INT NOT NULL,
    reserva_diaria INT NOT NULL,
    CHECK (limite_diario >= 0),
    CHECK (reserva_diaria >= 0)
);

CREATE TABLE noticia (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo VARCHAR(500) NOT NULL,
    descricao TEXT,
    url TEXT NOT NULL,
    url_hash VARCHAR(64) NOT NULL UNIQUE,
    fonte_nome VARCHAR(120),
    publicada_em TIMESTAMPTZ,
    coletada_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    provedor_id BIGINT REFERENCES provedores(id)
);

CREATE TABLE noticia_assunto (
    noticia_id BIGINT NOT NULL REFERENCES noticia(id) ON DELETE CASCADE,
    assunto_id BIGINT NOT NULL REFERENCES assunto(id),
    PRIMARY KEY (noticia_id, assunto_id)
);

CREATE TABLE newsletter (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    janela_inicio TIMESTAMPTZ NOT NULL,
    janela_fim TIMESTAMPTZ NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
    criada_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    enviada_em TIMESTAMPTZ,
    tentativas INT NOT NULL DEFAULT 0,
    telegram_id BIGINT
);

CREATE TABLE newsletter_itens (
    newsletter_id BIGINT NOT NULL REFERENCES newsletter(id) ON DELETE CASCADE,
    noticia_id BIGINT NOT NULL REFERENCES noticia(id),
    PRIMARY KEY (newsletter_id, noticia_id)
);

CREATE TABLE consultas_provedor (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    provedor_id BIGINT NOT NULL REFERENCES provedores(id),
    assunto_id BIGINT NOT NULL REFERENCES assunto(id),
    consultado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    creditos_consumidos INT NOT NULL DEFAULT 0,
    sucesso BOOLEAN NOT NULL,
    quantidade_resultados INT NOT NULL DEFAULT 0,
    erro VARCHAR(300)
);

CREATE INDEX idx_noticia_publicada_em
    ON noticia(publicada_em DESC);

CREATE INDEX idx_newsletter_pendente
    ON newsletter(status, criada_em);

CREATE INDEX idx_consulta_provedor_data
    ON consultas_provedor(provedor_id, consultado_em DESC);

INSERT INTO assunto (nome, slug) VALUES
    ('Tecnologia', 'tecnologia'),
    ('Cibersegurança', 'ciberseguranca'),
    ('Economia', 'economia'),
    ('Ciência', 'ciencia'),
    ('Inteligência Artificial', 'inteligencia-artificial');
