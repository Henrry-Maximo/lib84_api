# Changelog

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.
O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org).

## [1.0.0]

## - 2026-11-09

### Added
- Adição de exemplos de conteúdo (request) nos endpoints
- Adição de exemplos de resposta (response) nos endpoints
- Adição de endpoint com informações sobre o termos de serviço da API
- Adição de descrição detalhada no Swagger sobre o contexto da API

### Changed
- A interface swagger customizada com a personalidade da aplicação e tema

### Fixed
- Correção de redirecionamento padrão para documentação customizada da API 
- Correção de descrição estabelecida no Swagger para aprensentação da API, removendo paginação e especifidade de tipo quanto ao Hateoas
- Correção de respostas de erros 500 nos endpoints
- Correção nos endpoints de cadastro com valores inválidos para informar status corretos (400/422)

## - 2026-11-10
- Adição de rotas ordenadas alfabeticamente

## [2.0.0]