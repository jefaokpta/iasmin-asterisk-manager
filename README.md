# iasmin-asterisk-ari

Backend em Kotlin/Spring Boot para gerenciar chamadas do Asterisk via ARI (Asterisk REST Interface). Este serviço expõe endpoints HTTP para iniciar/rotear chamadas e serve como base para evoluções futuras (ex.: conferências, ponte entre peers, gravações, etc.).

Este README foi escrito para que um humano ou um agente de IA consiga instalar, executar, debugar, manter e criar novas features seguindo boas práticas.


## Sumário
- Visão Geral
- Arquitetura e Organização
- Tecnologias
- Como Executar (dev) e Build
- Configuração (ARI e ambiente)
- Endpoints Disponíveis
- Logs e Observabilidade
- Testes
- Padrões de Código e Boas Práticas (DRY, Imutabilidade)
- Diretrizes de Contribuição (inclui dicas para agentes de IA)
- Troubleshooting


## Visão Geral
- Objetivo: Gerenciar chamadas do Asterisk usando ARI, roteando e interligando peers.
- Estado atual: Endpoint de exemplo para iniciar chamada; integração ARI preparada via dependência ari4java (utilizar nas próximas implementações).


## Arquitetura e Organização
- Framework: Spring Boot (3.5.x)
- Linguagem: Kotlin (JVM)
- Estrutura principal:
  - src/main/kotlin/com/example/iasminasteriskari/
    - IasminAsteriskAriApplication.kt — ponto de entrada da aplicação Spring.
    - controller/CallController.kt — endpoints relacionados a chamadas.
    - Startup.kt — classe utilitária de exemplo (não gerenciada por Spring no estado atual).
  - src/main/resources/application.properties — configuração padrão do Spring.
- Abordagem recomendada (futuras evoluções):
  - controller — HTTP layer (validações simples, DTOs de entrada/saída).
  - service — regras de negócio e orquestração com ARI.
  - client/ari — cliente ARI (ari4java) e mapeamentos.
  - domain/model — modelos imutáveis do domínio.
  - config — beans e configurações Spring.


## Tecnologias
- Spring Boot — Framework principal: https://spring.io/projects/spring-boot
- ari4java — Cliente ARI para Asterisk: https://github.com/ari4java/ari4java/wiki/Getting-Started
- asterisk-java - Gestão do Asterisk via AMI: https://github.com/asterisk-java/asterisk-java
- Jackson Kotlin — Serialização JSON.
- Logback — Logging.
- JUnit 5 — Testes.
- Java 21 — Toolchain.

As versões e dependências estão definidas em build.gradle.kts. Principais plugins:
- org.springframework.boot
- io.spring.dependency-management
- kotlin("jvm"), kotlin("plugin.spring")


## Como Executar (dev) e Build
Pré‑requisitos:
- JDK 21 disponível (o projeto usa toolchain 21).
- Acesso à internet para baixar dependências Maven.

Comandos:
- Executar em modo desenvolvimento:
  - Linux/macOS: ./gradlew bootRun
  - Windows:     gradlew.bat bootRun
- Rodar testes: ./gradlew test
- Build do artefato: ./gradlew build

Após iniciar, por padrão a aplicação sobe em http://localhost:8080.


## Configuração (ARI e ambiente)
Ainda não há configuração ARI efetiva no código, mas a biblioteca ari4java já está disponível. Para evoluir:
- Crie propriedades em src/main/resources/application.properties, por exemplo:
  - ari.baseUrl=http://SEU_ASTERISK:8088/ari
  - ari.username=usuario
  - ari.password=senha
  - ari.appName=nome-da-aplicacao
- Alternativamente, use variáveis de ambiente (recomendado para credenciais):
  - export ARI_BASE_URL=...
  - export ARI_USERNAME=...
  - export ARI_PASSWORD=...
  - export ARI_APP_NAME=...
- Crie uma classe de configuração (ex.: AriConfig) que constrói o cliente ari4java usando essas propriedades e o registra como bean Spring.
- Garanta que o Asterisk esteja com o ARI habilitado (http.conf e ari.conf configurados) e que a aplicação ARI (appName) exista.

Exemplo de referência (pseudo‑código Kotlin):
- data class AriProperties(val baseUrl: String, val username: String, val password: String, val appName: String)
- @Configuration @ConfigurationProperties(prefix = "ari")
- fun ariClient(props: AriProperties): AriXyzClient { /* criar client via ari4java */ }


## Endpoints Disponíveis
- GET /call/{phoneNumber}
  - Descrição: Exemplo de endpoint de chamada (retorna texto "Calling {number}...")
  - Exemplo:
    - curl http://localhost:8080/call/5511999999999
  - Próximos passos: substituir implementação mock por lógica que use ari4java para originar/rotear a chamada via ARI.


## Logs e Observabilidade
- Logback está incluído. Níveis de log podem ser ajustados em application.properties:
  - logging.level.root=INFO
  - logging.level.com.example.iasminasteriskari=DEBUG
- Sugestão para futuras evoluções: incluir MDC (correlationId), métricas via Micrometer/Prometheus e tracing distribuído.


## Testes
- Framework: JUnit 5 + spring-boot-starter-test.
- Comando: ./gradlew test
- Recomendações:
  - Testes de unidade para services e integrações ARI (mockar cliente ari4java).
  - Testes de contrato para endpoints (MockMvc/WebTestClient).


## Padrões de Código e Boas Práticas
- DRY (Don't Repeat Yourself):
  - Centralize lógica comum em serviços/utilitários.
  - Evite duplicação de regras de negócio em múltiplos controllers/services.
- Imutabilidade:
  - Use data classes imutáveis (val) para modelos de domínio e DTOs.
  - Evite estados mutáveis compartilhados; prefira injeção de dependências e retornos puros.
- Kotlin/Spring:
  - Null-safety: tipos não-nulos por padrão; use Optionals/Result quando fizer sentido.
  - Tratamento de erros: @ControllerAdvice para mapear exceções → respostas HTTP.
  - Logging: use logger por classe; não logar segredos.
  - Configuração: externalizar credenciais; perfis (spring.profiles.active) para dev/test/prod.
- Estilo de código:
  - KtLint/Detekt (sugerido) para padronização.
  - Nomeie pacotes por contexto (controller, service, domain, client, config).


## Diretrizes de Contribuição (inclui dicas para agentes de IA)
- Antes de implementar:
  - Abra uma issue descrevendo propósito, contrato do endpoint e mudanças no domínio.
  - Defina o fluxo da chamada no ARI (canais, bridges, playback, etc.).
- Implementação:
  - Crie camada service que orquestra o cliente ARI (ari4java) e encapsula detalhes do protocolo.
  - Exponha apenas DTOs imutáveis ao controller.
  - Mantenha logs suficientes para diagnóstico sem vazar dados sensíveis.
- Commits:
  - Prefira Conventional Commits (feat:, fix:, docs:, refactor:, test:, chore:).
- Revisão:
  - Garanta cobertura de testes para regras de negócio.

Para agentes de IA:
- Leia build.gradle.kts para versões e dependências.
- Liste endpoints em controller/.
- Se precisar criar cliente ARI, adicione um bean de configuração e injete-o em services.
- Atualize README quando alterar endpoints/rotas.


## Troubleshooting
- Porta 8080 ocupada: export SERVER_PORT=8081 e usar --server.port=8081 ou configurar em application.properties.
- Falha ao conectar no ARI:
  - Verifique baseUrl, credenciais e se o ARI está habilitado no Asterisk.
  - Teste curl com basic auth no endpoint do ARI.
- Erros de dependência: rode ./gradlew --refresh-dependencies.
- Incompatibilidade de Java: confirme JDK 21 (java -version) e toolchain configurado.


## Licença
Defina aqui a licença do projeto (ex.: MIT, Apache-2.0). Atualmente não especificada.