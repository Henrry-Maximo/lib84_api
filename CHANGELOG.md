# Changelog

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/), e este projeto adere ao [Versionamento Semântico](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2026-11-13

### Added

- Adicionada a exceção `AuthorHasBooksException` para impedir a exclusão de autores associados a livros (HTTP 409).
- Adicionado o método `existsByAuthors_Id` ao `BookRepository` para verificar relacionamentos entre autores e livros.
- Adicionado handler genérico para exceções não previstas (HTTP 500), com registro de detalhes nos logs do servidor.
- Adicionado handler para `DataIntegrityViolationException`, contemplando violações de restrições UNIQUE e de integridade referencial (HTTP 409).
- Adicionado handler para `NoSuchElementException` (HTTP 404).
- Adicionada infraestrutura de validação do cabeçalho `X-API-Version`, com V1 como versão padrão.
- Adicionada a anotação `@ApiVersion` e o `ApiVersionConfig` para identificação e validação das versões da API.
- Adicionada a exceção `BooksNotFoundException` para tratar situações em que livros solicitados não são encontrados durante operações de empréstimo.
- Adicionada ordenação das operações HTTP na documentação Swagger (GET, POST, PUT, PATCH e DELETE).
- Adicionado vídeo com temática de biblioteca ao cabeçalho do Swagger.
- Adicionada logo ao fundo da documentação Swagger.
- Adicionado GIF animado à documentação Swagger.

### Changed

- Atualizados os grupos de documentação Swagger (V1 e V2) para filtrar endpoints por meio da anotação `@ApiVersion`.
- Padronizadas as operações CRUD das entidades Author, Book, Category, Loan, Profile, Supplier e User.
- Padronizada a normalização dos nomes de autores antes da validação e persistência.
- Atualizados os exemplos de respostas no Swagger para os endpoints.
- Padronizadas as mensagens das exceções de recursos não encontrados e registros duplicados.
- Corrigido o mapeamento JPA das entidades, substituindo `@Entity(name = "...")` por `@Entity` e `@Table(name = "...")`.
- Alinhados os limites de validação dos DTOs e os tamanhos das colunas das entidades.
- Atualizada a estrutura das entidades com exemplos de documentação (`@Schema`) e ordenação das propriedades JSON (`@JsonPropertyOrder`).
- Padronizados os services com atribuições explícitas de propriedades, removendo o uso de `BeanUtils.copyProperties`.
- Atualizadas as operações de atualização e exclusão para receber identificadores UUID diretamente nos services.
- Padronizados os controllers com documentação detalhada de respostas HTTP, exemplos JSON e validações dos parâmetros de consulta.
- Padronizadas as respostas de exclusão com `ResponseEntity<Void>`.

### Fixed

- Corrigida a violação de integridade referencial ao excluir autores associados a livros, substituindo o erro 500 por 409 Conflict.
- Corrigida a exposição de stack traces e detalhes internos nas respostas de erro, utilizando as configurações `server.error.*`.
- Corrigida a validação de parâmetros nas operações de consulta.
- Corrigida a verificação de nomes duplicados durante a atualização de autores.
- Aprimorado o tratamento global de erros de validação, incluindo múltiplas mensagens por campo e JSON malformado.
- Corrigidas ocorrências de `orElseThrow()` sem exceção personalizada nos services, evitando respostas 500 para recursos inexistentes.
- Corrigida a validação do campo `datePublication` no `BookRecordDto`, que aceitava valores nulos indevidamente.
- Corrigida a validação do campo `loanDateScheduled` no `LoanRecordDto`, que aceitava valores nulos indevidamente.
- Corrigidas as mensagens das exceções `CategoryByNameNotFoundException`, `SupplierByNameNotFoundException`, `UserByEmailNotFoundException` e `DuplicateEmailException`.
- Corrigida a atualização de categorias, que não verificava a duplicidade do título.
- Corrigido o método `getByUser` em `LoanService`, que não verificava previamente a existência do usuário.
- Corrigido o método `getByUserId` em `ProfileService`, que não verificava previamente a existência do usuário.

## [1.0.0] - 2026-11-09

### Added

- Adicionados exemplos de requisições (request) aos endpoints.
- Adicionados exemplos de respostas (response) aos endpoints.
- Adicionado endpoint com informações sobre os termos de serviço da API.
- Adicionada descrição detalhada ao Swagger, apresentando o contexto da aplicação.

### Changed

- Personalizada a interface Swagger com a identidade visual da aplicação.

### Fixed

- Corrigido o redirecionamento da rota raiz para a documentação Swagger customizada.
- Corrigida a descrição do Swagger, removendo referências a paginação e especificando o formato HATEOAS (HAL).
- Corrigido o tratamento de erros nos endpoints.