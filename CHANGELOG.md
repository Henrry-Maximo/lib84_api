Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

O formato é baseado em
[Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao
[Versionamento Semântico](https://semver.org).

## [Unreleased]

### Added
- Adiciona informações de precedência das operações quanto a cadastro no Swagger
- Adiciona vídeo (biblioteca) no header do Swagger
- Adiciona logo ao fundo da documentação do Swagger

### Changed

- Padronização das operações CRUD da entidade Author.
- Normalização dos nomes de autores antes da validação e persistência.
- Atualização dos exemplos de respostas no Swagger.

### Fixed

- Correção da validação de parâmetros nas consultas de autores.
- Correção da verificação de nomes duplicados durante a atualização de autores.
- Aprimoramento do tratamento global de erros de validação.

## [1.0.0] - 2026-11-09

### Added

- Adição de exemplos de conteúdo (request) nos endpoints.
- Adição de exemplos de resposta (response) nos endpoints.
- Adição de endpoint com informações sobre os termos de serviço da API.
- Adição de descrição detalhada no Swagger sobre o contexto da API.

### Changed

- Personalização da interface Swagger com identidade visual da aplicação.

### Fixed

- Correção do redirecionamento padrão para a documentação customizada da API.
- Correção da descrição do Swagger, removendo informações sobre paginação e especificando o formato HATEOAS (HAL).
- Correção do tratamento de erros nos endpoints.