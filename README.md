# HoraCerta — Monitoramento de Manutenção Preventiva

Sistema para gerenciamento de equipamentos e acompanhamento de manutenções preventivas por horímetro. O HoraCerta foi desenvolvido com o objetivo de facilitar o controle dos equipamentos, organizar os planos de manutenção e identificar serviços próximos do vencimento ou atrasados.

**Repositório:** [github.com/Marii65/HoraCerta](https://github.com/Marii65/HoraCerta)

## Objetivo

O HoraCerta busca facilitar o acompanhamento da manutenção preventiva de equipamentos cuja utilização é medida em horas de operação.

A partir das leituras de horímetro e dos intervalos definidos nos planos de manutenção, o sistema permite acompanhar a situação de cada equipamento e antecipar serviços antes que ultrapassem o período recomendado.

## Perfis de acesso

O sistema possui três níveis de acesso:

| Perfil | Permissões |
| --- | --- |
| `ADMIN` | Acesso administrativo, gerenciamento de equipamentos, planos, usuários e demais operações |
| `TECHNICIAN` | Operações relacionadas às manutenções, planos e ordens de serviço |
| `OPERATOR` | Consulta de informações e registro de leituras de horímetro |

As permissões são validadas no backend por meio do **Spring Security**.

---

## Funcionalidades do backend

Dashboard de monitoramento
- Visão geral da frota.
- Equipamentos em dia, em atenção ou com manutenção vencida.
- Exibição das manutenções mais críticas.

Equipamentos
- Cadastro, consulta e atualização.
- Controle do horímetro atual.
- Controle de status.
- Associação com planos de manutenção.

Planos de manutenção
- Cadastro de planos reutilizáveis.
- Definição dos serviços de cada plano.
- Intervalos de manutenção em horas.
- Margem de alerta antes do vencimento.
- Ativação e desativação de planos.

Leituras de horímetro
- Registro de novas leituras.
- Atualização do horímetro atual do equipamento.
- Histórico de leituras.
- Consulta por período.

Ordens de serviço
- Manutenções preventivas e corretivas.
- Agendamento de serviços.
- Controle de status.
- Registro da conclusão da manutenção.
- Cancelamento de ordens.

Usuários e segurança
- Cadastro e login.
- Autenticação com JWT.
- Controle de acesso baseado em perfis.
- Ativação e desativação de usuários.
- Gerenciamento de usuários por administradores.

## Tecnologias

| **Tecnologia**              | **Utilização**                              |
| --------------------------- | ------------------------------------------- |
| Java 21                     | Linguagem de programação                    |
| Spring Boot                 | Desenvolvimento da API REST                 |
| Spring Web                  | Endpoints HTTP                              |
| Spring Data JPA e Hibernate | Persistência e mapeamento objeto-relacional |
| Spring Security             | Autenticação e autorização                  |
| JWT                         | Autenticação baseada em token               |
| MySQL                       | Banco de dados relacional                   |
| Flyway                      | Versionamento das migrações do banco        |
| Maven Wrapper               | Compilação e execução do backend            |
| SpringDoc OpenAPI           | Documentação interativa da API              |
| Vue                         | Tecnologia prevista para o frontend         |

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

| **Entidade**          | **Responsabilidade**                     |
| --------------------- | ---------------------------------------- |
| `User`                | Usuários e perfis de acesso              |
| `Equipment`           | Equipamentos e horímetro atual           |
| `MaintenancePlan`     | Planos de manutenção                     |
| `MaintenancePlanItem` | Serviços, intervalos e margens de alerta |
| `EquipmentPlan`       | Associação entre equipamento e plano     |
| `HorometerReading`    | Histórico de leituras de horímetro       |
| `WorkOrder`           | Ordens de serviço e seus status          |

As alterações estruturais do banco são gerenciadas por migrações do Flyway.

## Pré-requisitos

Antes de executar o projeto, tenha instalado:

- JDK 21.
- MySQL Server.
- Git.

O Maven Wrapper está incluído no backend, dispensando a instalação separada do Maven.

## Configuração do banco de dados

O projeto utiliza o MySQL com as seguintes configurações locais de desenvolvimento:

| **Configuração** | **Valor**   |
| ---------------- | ----------- |
| Banco de dados   | `horacerta` |
| Host             | `localhost` |
| Porta            | `3307`      |
| Usuário          | `root`      |

Ajuste as configurações conforme o ambiente utilizado. As credenciais e a chave secreta JWT devem ser configuradas localmente ou por variáveis de ambiente, sem serem publicadas no repositório.

Para criar o banco, caso ainda não exista, execute:

CREATE DATABASE horacerta     CHARACTER SET utf8mb4     COLLATE utf8mb4_unicode_ci;

O Flyway deve aplicar as migrações configuradas na inicialização da aplicação.

## Como executar o backend

### 1. Clonar o repositório

git clone [https://github.com/Marii65/HoraCerta.git](https://github.com/Marii65/HoraCerta.git) cd HoraCerta

### 2. Acessar a pasta do backend

Entre na pasta que contém o arquivo `pom.xml`.

### 3. Compilar o projeto

No terminal do Windows:

.\mvnw\.cmd clean compile

### 4. Executar a aplicação

.\mvnw\.cmd spring-boot:run

Com a configuração padrão, a API estará disponível em `http://localhost:8080`, desde que a porta não tenha sido alterada.

## Documentação da API

Com o backend em execução, acesse:

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [Especificação OpenAPI em JSON](http://localhost:8080/v3/api-docs)

O Swagger UI permite consultar os endpoints, visualizar os parâmetros e testar as operações disponibilizadas pela API, respeitando as permissões de acesso.

As principais classes e operações do código também possuem documentação Javadoc.

## Como executar os testes

Na pasta do backend, execute:

.\mvnw\.cmd test

Os testes automatizados implementados no projeto foram executados com sucesso no ambiente de desenvolvimento.

## Frontend

## Demonstração

**Vídeo de apresentação:** a ser adicionado.

## Autoria

Desenvolvido por **Maria Luiza** como desafio técnico de desenvolvimento de software para a [Support](https://supportsistemas.com.br/).
