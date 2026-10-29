# HoraCerta — Monitoramento de Manutenção Preventiva

Sistema para gerenciamento de equipamentos e acompanhamento de manutenções preventivas por horímetro. O HoraCerta foi desenvolvido com o objetivo de facilitar o controle dos equipamentos, organizar os planos de manutenção e identificar serviços próximos do vencimento ou atrasados.

**Repositório:** [github.com/Marii65/HoraCerta](https://github.com/Marii65/HoraCerta)

## Objetivo

Oferecer uma solução para registrar equipamentos, configurar planos de manutenção, acompanhar leituras de horímetro e gerenciar ordens de serviço, auxiliando no planejamento das manutenções preventivas.

## Funcionalidades do backend

- **Autenticação:** cadastro e login de usuários com autenticação JWT.
- **Gerenciamento de usuários:** consulta, atualização e gerenciamento de perfis de acesso.
- **Equipamentos:** cadastro, consulta, atualização e controle de equipamentos.
- **Planos de manutenção:** cadastro e gerenciamento de planos reutilizáveis.
- **Itens de manutenção:** definição de serviços, intervalos de horas e margens de alerta.
- **Vínculos equipamento-plano:** associação e ativação ou desativação de planos para equipamentos.
- **Leituras de horímetro:** registro e consulta do histórico de leituras.
- **Ordens de serviço:** criação, consulta, conclusão e cancelamento.
- **Monitoramento preventivo:** cálculo do limite de alerta com base no horímetro atual, no intervalo e na margem de alerta configurada.

## Tecnologias

| Tecnologia | Utilização |
|---|---|
| Java 21 | Linguagem de programação |
| Spring Boot | Desenvolvimento da API REST |
| Spring Web | Endpoints HTTP |
| Spring Data JPA e Hibernate | Persistência e mapeamento objeto-relacional |
| Spring Security | Autenticação e autorização |
| JWT | Autenticação baseada em token |
| MySQL | Banco de dados relacional |
| Flyway | Versionamento das migrações do banco |
| Maven Wrapper | Compilação e execução do backend |
| SpringDoc OpenAPI | Documentação interativa da API |
| Vue.js | Tecnologia prevista para o frontend |

## Arquitetura

O backend está organizado em camadas para separar responsabilidades e facilitar a manutenção:

- **Controllers:** recebem as requisições HTTP e encaminham as operações aos serviços.
- **DTOs:** definem os dados de entrada e saída da API.
- **Entities:** representam as entidades persistidas no banco de dados.
- **Repositories:** realizam consultas e operações de persistência.
- **Services:** concentram as regras de negócio.
- **Exceptions:** organizam o tratamento de erros da aplicação.
- **Security:** reúne os componentes relacionados à autenticação e à autorização.

O frontend está organizado em uma pasta separada dentro do mesmo repositório.

## Modelo de dados

O domínio da aplicação inclui as seguintes entidades:

| Entidade | Responsabilidade |
|---|---|
| `User` | Usuários e perfis de acesso |
| `Equipment` | Equipamentos e horímetro atual |
| `MaintenancePlan` | Planos de manutenção |
| `MaintenancePlanItem` | Serviços, intervalos e margens de alerta |
| `EquipmentPlan` | Associação entre equipamento e plano |
| `HorometerReading` | Histórico de leituras de horímetro |
| `WorkOrder` | Ordens de serviço e seus status |

As alterações estruturais do banco são gerenciadas por migrações do Flyway.

## Pré-requisitos

Antes de executar o projeto, tenha instalado:

- JDK 21.
- MySQL Server.
- Git.

O Maven Wrapper está incluído no backend, dispensando a instalação separada do Maven.

## Configuração do banco de dados

O projeto utiliza o MySQL com as seguintes configurações locais de desenvolvimento:

| Configuração | Valor |
|---|---|
| Banco de dados | `horacerta` |
| Host | `localhost` |
| Porta | `3307` |
| Usuário | `root` |

Ajuste as configurações conforme o ambiente utilizado. As credenciais e a chave secreta JWT devem ser configuradas localmente ou por variáveis de ambiente, sem serem publicadas no repositório.

Para criar o banco, caso ainda não exista, execute:

```sql
CREATE DATABASE horacerta
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

O Flyway deve aplicar as migrações configuradas na inicialização da aplicação.

## Como executar o backend

### 1. Clonar o repositório

```bash
git clone https://github.com/Marii65/HoraCerta.git
cd HoraCerta
```

### 2. Acessar a pasta do backend

Entre na pasta que contém o arquivo `pom.xml`.

### 3. Compilar o projeto

No terminal do Windows:

```powershell
.\mvnw.cmd clean compile
```

### 4. Executar a aplicação

```powershell
.\mvnw.cmd spring-boot:run
```

Com a configuração padrão, a API estará disponível em `http://localhost:8080`, desde que a porta não tenha sido alterada.

## Documentação da API

Com o backend em execução, acesse:

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [Especificação OpenAPI em JSON](http://localhost:8080/v3/api-docs)

O Swagger UI permite consultar os endpoints, visualizar os parâmetros e testar as operações disponibilizadas pela API, respeitando as permissões de acesso.

As principais classes e operações do código também possuem documentação Javadoc.

## Como executar os testes

Na pasta do backend, execute:

```powershell
.\mvnw.cmd test
```

Os testes automatizados implementados no projeto foram executados com sucesso no ambiente de desenvolvimento.

## Frontend

O frontend está localizado em uma pasta separada dentro do repositório.

Para executá-lo, acesse essa pasta e siga as instruções do `README` específico, caso exista. Os comandos dependem das configurações e dos scripts definidos no `package.json`.

## Demonstração

**Vídeo de apresentação:** a ser adicionado.

A demonstração deve apresentar os fluxos principais da aplicação e as funcionalidades efetivamente implementadas.

## Autoria

Desenvolvido por **Maria Luiza** como projeto técnico de desenvolvimento de software.
