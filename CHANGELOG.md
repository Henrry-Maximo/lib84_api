Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

O formato é baseado em
[Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e este projeto adere ao
[Versionamento Semântico](https://semver.org).

## [Unreleased]

### Added
- Adiciona `AuthorHasBooksException` para impedir exclusão de autor vinculado a livros (HTTP 409)
- Adiciona `existsByAuthors_Id` no `BookRepository` para verificação de integridade referencial
- Adiciona handler genérico para exceções não previstas (HTTP 500) com log completo no servidor
- Adiciona handler para `DataIntegrityViolationException` (HTTP 409) com identificação de violações UNIQUE e de chave estrangeira
- Adiciona handler para `NoSuchElementException` (HTTP 404)
- Adiciona infraestrutura de versionamento por header `X-API-Version` (`ApiVersionInterceptor`, `@ApiVersion`)
- Adiciona `ApiVersionConfig` registrando o interceptor para todos os endpoints de negócio
- Adiciona informações de precedência das operações quanto a cadastro no Swagger
- Adiciona vídeo (biblioteca) no header do Swagger
- Adiciona logo ao fundo da documentação do Swagger
- Adiciona gif animado na documentação do Swagger
- Adiciona `BooksNotFoundException` para quando um ou mais livros não são encontrados em empréstimos

### Changed
- Grupos do Swagger (`v1` e `v2`) agora filtram endpoints pela anotação `@ApiVersion` em vez de prefixo de URL
- Padronização das operações CRUD da entidade Author
- Normalização dos nomes de autores antes da validação e persistência
- Padronização das operações CRUD da entidade Book
- Padronização das operações CRUD da entidade Category
- Padronização das operações CRUD da entidade Loan
- Padronização das operações CRUD da entidade Profile
- Padronização das operações CRUD da entidade Supplier
- Padronização das operações CRUD da entidade User
- Atualização dos exemplos de respostas no Swagger para todos os endpoints
- Mensagens de erro das exceptions alinhadas ao padrão `"could not find the [entity] with [field]: '[value]'."`
- Entidades corrigidas: `@Entity(name=...)` substituído por `@Entity` + `@Table(name=...)`, adicionados `@Schema`, `@Size` e `length` nas colunas
- DTOs corrigidos: campos obrigatórios com `@NotNull`/`@NotBlank` e mensagens explícitas
- Services refatorados: removido `BeanUtils`, `orElseThrow` sem argumento substituído por `if (isEmpty())` explícito, `delete`/`update` passaram a receber `UUID`
- Controllers refatorados: `@ApiResponse` com `@ExampleObject` multilinha, `@Schema` nos `@PathVariable`, `@NotBlank` nos `@RequestParam`, `ResponseEntity<Void>` no delete

### Fixed
- Correção de violação de integridade referencial ao excluir autor vinculado a livros (era 500, agora 409)
- Correção da exposição de stack traces e detalhes internos nas respostas de erro (`server.error.*=never`)
- Correção da validação de parâmetros nas consultas de autores
- Correção da verificação de nomes duplicados durante a atualização de autores
- Aprimoramento do tratamento global de erros de validação
- Correção do `orElseThrow()` sem argumento nas services que causava 500 em vez de 404
- Correção do campo `datePublication` no `BookRecordDto` que aceitava `null` e causava 500
- Correção do campo `loanDateScheduled` no `LoanRecordDto` que aceitava `null` e causava 500
- Correção das mensagens de erro das exceptions `CategoryByNameNotFoundException`, `SupplierByNameNotFoundException`, `UserByEmailNotFoundException` e `DuplicateEmailException`
- Correção do `update` de Category que não verificava título duplicado
- Correção do `getByUser` em `LoanService` que não validava existência do usuário
- Correção do `getByUserId` em `ProfileService` que não validava existência do usuário

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