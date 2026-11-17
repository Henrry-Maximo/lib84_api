# Changelog

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/), e este projeto adere ao [Versionamento Semântico](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.0] - 2026-11-17

### Added

- Adicionado `IdempotencyStore` para armazenamento em memória de chaves de idempotência com TTL de 24 horas, hash SHA-256 do corpo da requisição e controle de estado (`IN_PROGRESS` / `DONE`) via `ConcurrentHashMap`.
- Adicionado `IdempotencyWrapperFilter` (`@Order(1)`) para envolver requisições POST e PATCH da V2 nos wrappers `CachedBodyRequestWrapper` e `ContentCachingResponseWrapper`, permitindo múltiplas leituras do `InputStream` e captura do corpo da resposta.
- Adicionado `CachedBodyRequestWrapper` para armazenar o corpo da requisição em buffer e permitir releitura do `InputStream`.
- Adicionado `IdempotencyInterceptor` para validar o header `Idempotency-Key` em operações POST e PATCH da V2, aplicando as regras: chave ausente → 422, chave em andamento (concorrência) → 409, mesma chave com corpo diferente → 422, mesma chave com mesmo corpo → replay da resposta cacheada com header `Idempotent-Replayed: true`.
- Adicionado `IdempotencyDemoController` com `GET /idempotency-demo` (documentação interativa das regras) e `POST /idempotency-demo/echo` (endpoint de teste sem exigência de chave).

### Changed

- Atualizado `ApiVersionConfig` para registrar o `IdempotencyInterceptor` após o `RateLimitingInterceptor`.
- Atualizado `ConfigSwagger` para injetar o parâmetro de header `Idempotency-Key` (obrigatório, com exemplo UUID) em todos os endpoints POST e PATCH da V2, exceto `/session` e `/idempotency-demo`.
- Atualizado `SecurityConfig` para liberar a rota `/idempotency-demo` sem autenticação.
- Atualizado configuração do swagger para abrigar campo UUID da idempotência.
- Atualizado readme para refletir conclusão da etapa II.

## [2.0.0] - 2026-11-16

### Added

- Adicionado RateLimitService e TokenBucket baseados no algoritmo de Token Bucket com threads seguras (ReentrantLock) para controle de taxa de requisições.
- Adicionado RateLimitingInterceptor para interceptar requisições globais e aplicar cotas dinâmicas baseadas no plano do usuário (BASIC vs PREMIUM) ou por IP para clientes anônimos.
- Adicionada resposta HTTP 429 Too Many Requests acompanhada do header Retry-After e headers informativos de cota (X-RateLimit-Limit, X-RateLimit-Remaining).
- Adicionada validação por parâmetro de query no ApiVersionInterceptor como fallback para garantir compatibilidade com o seletor visual do Swagger UI (prevenir contra escolha de versão não existente).
- Adicionados exemplos estruturados e schemas de resposta JSON para os códigos de erro HTTP 401 (missing or invalid JWT Token) e 403 (not authorized) em todos os controllers da V2.
- Adicionada enum com valor único no ConfigSwagger para estabelecer versão (X-API-Version), sendo renderizado como dropdown estático no Swagger UI.

### Changed

- Atualizado UserPatchDto e UserService para permitir a atualização dinâmica do plano de assinatura do usuário via método PATCH "/users/{id}".
- Atualizada a mensagem da exceção ProfileNotFoundException no ProfileService para orientar explicitamente usuários autenticados sem perfil a criá-lo via POST "/profiles". Caso contrário, perfil não terá as informações opcionais.
- Atualizado o RateLimitingInterceptor para aplicar proteção de taxa em todas as requisições (incluindo rotas públicas e anônimas por IP), mitigando riscos de abusos e ataques de negação de serviço (DoS).
- Otimizado o método tryConsume no TokenBucket para eliminar chamadas duplicadas ao mecanismo de recarga (refill), garantindo a consistência exata dos tokens restantes nos headers informativos (bloqueio via reentrant lock).

## [2.0.0] - 2026-11-15

### Added

- Adicionado suporte explícito para versões: 1 e 2.
- Adicionada autenticação JWT na V2: `POST /session` retorna token assinado com o `user_id` no subject.
- Adicionado `SecurityFilter` para validação do token JWT e população do atributo `user_id` no request.
- Adicionada chain de segurança V2 exigindo autenticação em todas as rotas, exceto infra e `/session`.
- Adicionado `AuthenticationEntryPoint` retornando 401 com corpo JSON quando o token está ausente ou inválido.
- Adicionado comentários para explicar a abordagem adotada, não resultado gerado.
- Adicionado `POST /profiles` na V2 sem o campo `userId` no body — o ID do usuário é extraído do JWT.
- Adicionado `ProfileV2RecordDto` sem o campo `userId`.
- Adicionada documentação Swagger separada por versão usando `addOpenApiMethodFilter`, eliminando ambiguidade de paths entre V1 e V2.
- Adicionado `addOperationCustomizer` para pré-preencher o header `X-API-Version` com o valor da versão no Swagger UI.
- Adicionado esquema de segurança Bearer JWT no Swagger, aplicado automaticamente a todas as operações da V2.
- Adicionado `AuthenticationEntryPoint` no grupo V2 do Swagger com resposta 401 documentada.

### Changed

- Atualizado `ConfigSwagger` para usar `addOpenApiMethodFilter` e `addOperationCustomizer` por grupo, removendo a reflexão manual sobre `RequestMappingHandlerMapping`.
- Simplificado `SecurityConfig` para duas chains ordenadas: V2 (autenticada) e V1 (pública).
- Atualizado `SessionV2Controller` para usar `@PostMapping(version = "2")` em vez de `headers = "X-API-Version=2"`.
- Atualizado `ProfileV2Controller` para usar `@PostMapping(version = "2")` em vez de `headers = "X-API-Version=2"` e `@ApiVersion(2)`.
- Atualizado localização de Controllers/DTO da versão 2 para pasta denominada "v3", mais legibilidade em
manutenção posteriores.

### Removed

- Removida a propriedade `spring.mvc.pathmatch.use-suffix-pattern=true` do `application.properties`, incompatível com Spring Boot 4.

### Fixed

- Corrigido bug intermitente no Swagger onde `GET /profiles` aparecia no grupo V2 e o `POST /profiles` exibia o DTO errado (com `userId`), causado por ambiguidade de paths entre controllers de versões diferentes.
- Corrigido retorno 403 ao acessar rotas V2 sem token, que agora retorna 401 com corpo JSON.
- Corrigido exemplo da resposta 500 no Swagger, que era exibido como string escapada em vez de objeto JSON.

## [2.0.0] - 2026-11-14

### Added

- Adicionada infraestrutura de versionamento nativo via Spring Framework 7 (`ApiVersionConfig`), substituindo o interceptor customizado anterior (ApiVersionInterceptor/ApiVersion) que gerava problema no mapeamento das rotas com o mesmo path, rota duplicada.
- Adicionado suporte ao cabeçalho `X-API-Version` com versão padrão `1` quando o cabeçalho está ausente.
- Adicionado atributo `version` nos mapeamentos dos controllers V2 (`@PostMapping(version = "2")`).
- Adicionado exemplo de resposta 500 como objeto JSON tipado (`ObjectSchema`) em vez de string escapada.

### Changed

- Migrada a configuração de versionamento de interceptor customizado (`ApiVersionInterceptor`) para o mecanismo nativo do Spring Framework 7.

### Removed

- Removida a anotação customizada `@ApiVersion`.
- Removido o `ApiVersionInterceptor`.

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
