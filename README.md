# HoraCerta — Monitoramento de Manutenção Preventiva

O **HoraCerta** é um sistema web para gerenciamento de equipamentos e monitoramento de manutenções preventivas com base no horímetro.

A aplicação centraliza o cadastro de equipamentos, planos de manutenção, leituras de horímetro e ordens de serviço, permitindo identificar equipamentos com manutenções **em dia, próximas do vencimento ou vencidas**.

Para este MVP, o sistema foi desenvolvido com foco na operação de **maquinário pesado**, como tratores, retroescavadeiras e escavadeiras.

**Repositório:** [github.com/Marii65/HoraCerta](https://github.com/Marii65/HoraCerta)

---

## Objetivo

O HoraCerta busca facilitar o acompanhamento da manutenção preventiva de equipamentos cuja utilização é medida em horas de operação.

A partir das leituras de horímetro e dos intervalos definidos nos planos de manutenção, o sistema permite acompanhar a situação de cada equipamento e antecipar serviços antes que ultrapassem o período recomendado.

---

## Funcionalidades

- **Dashboard de monitoramento**
  - Visão geral da frota.
  - Equipamentos em dia, em atenção ou com manutenção vencida.
  - Exibição das manutenções mais críticas.

- **Equipamentos**
  - Cadastro, consulta e atualização.
  - Controle do horímetro atual.
  - Controle de status.
  - Associação com planos de manutenção.

- **Planos de manutenção**
  - Cadastro de planos reutilizáveis.
  - Definição dos serviços de cada plano.
  - Intervalos de manutenção em horas.
  - Margem de alerta antes do vencimento.
  - Ativação e desativação de planos.

- **Leituras de horímetro**
  - Registro de novas leituras.
  - Atualização do horímetro atual do equipamento.
  - Histórico de leituras.
  - Consulta por período.

- **Ordens de serviço**
  - Manutenções preventivas e corretivas.
  - Agendamento de serviços.
  - Controle de status.
  - Registro da conclusão da manutenção.
  - Cancelamento de ordens.

- **Usuários e segurança**
  - Cadastro e login.
  - Autenticação com JWT.
  - Controle de acesso baseado em perfis.
  - Ativação e desativação de usuários.
  - Gerenciamento de usuários por administradores.

---

## Perfis de acesso

O sistema possui três níveis de acesso:

| Perfil | Permissões |
| --- | --- |
| `ADMIN` | Acesso administrativo, gerenciamento de equipamentos, planos, usuários e demais operações |
| `TECHNICIAN` | Operações relacionadas às manutenções, planos e ordens de serviço |
| `OPERATOR` | Consulta de informações e registro de leituras de horímetro |

As permissões são validadas no backend por meio do **Spring Security**.

---

## Tecnologias

### Backend

| Tecnologia | Utilização |
| --- | --- |
| Java 21 | Linguagem de programação |
| Spring Boot | Desenvolvimento da API REST |
| Spring Web | Endpoints HTTP |
| Spring Data JPA | Persistência de dados |
| Hibernate | Mapeamento objeto-relacional |
| Spring Security | Autenticação e autorização |
| JWT | Autenticação baseada em token |
| Bean Validation | Validação dos dados de entrada |
| MySQL | Banco de dados relacional |
| Flyway | Versionamento e inicialização do banco |
| SpringDoc OpenAPI | Documentação da API |
| Maven | Gerenciamento de dependências, build e testes |

### Frontend

| Tecnologia | Utilização |
| --- | --- |
| Vue 3 | Construção da interface |
| Vite | Ambiente de desenvolvimento e build |
| Vue Router | Navegação entre páginas |
| Pinia | Gerenciamento de estado |
| Fetch API | Comunicação com a API REST |
| Lucide | Ícones da interface |

---

## Arquitetura

O backend foi organizado em camadas, separando as responsabilidades da aplicação:

- **Controllers:** exposição dos endpoints REST.
- **DTOs:** dados de entrada e saída da API.
- **Entities:** representação das entidades persistidas.
- **Repositories:** acesso e persistência dos dados.
- **Services:** regras de negócio.
- **Exceptions:** tratamento centralizado de erros.
- **Security:** autenticação JWT e controle de autorização.

O frontend está separado do backend e organizado em páginas, componentes reutilizáveis e serviços responsáveis pela comunicação com a API.

---

## Modelo de dados

As principais entidades do domínio são:

| Entidade | Responsabilidade |
| --- | --- |
| `User` | Usuários e perfis de acesso |
| `Equipment` | Equipamentos e horímetro atual |
| `MaintenancePlan` | Planos de manutenção |
| `MaintenancePlanItem` | Serviços, intervalos e margens de alerta |
| `EquipmentPlan` | Associação entre equipamento e plano |
| `HorometerReading` | Histórico de leituras |
| `WorkOrder` | Ordens de serviço |

O relacionamento `EquipmentPlan` permite que um mesmo plano seja utilizado por diferentes equipamentos.

As alterações estruturais e os dados iniciais do banco são gerenciados por **Flyway**.

---

## Regras de negócio principais

Algumas das regras implementadas no sistema:

- Uma leitura de horímetro não pode ser inferior ao horímetro atual do equipamento.
- Planos de manutenção possuem serviços com intervalo e margem de alerta definidos em horas.
- Um equipamento pode possuir diferentes planos de manutenção.
- Um mesmo vínculo entre equipamento e plano não é duplicado.
- Ao reativar um vínculo anteriormente desativado, o horímetro inicial passa a considerar o horímetro atual do equipamento.
- Planos inativos não podem receber novos vínculos.
- A conclusão de uma ordem de serviço registra o horímetro em que a manutenção foi executada.
- Usuários desativados não podem se autenticar ou acessar endpoints protegidos.

---

# Executando o projeto

## Pré-requisitos

Antes de executar o HoraCerta, tenha instalado:

- JDK 21
- MySQL Server
- Node.js
- Git

O projeto inclui o **Maven Wrapper**, portanto não é necessário instalar o Maven separadamente.

---

## Banco de dados

A configuração utilizada durante o desenvolvimento foi:

| Configuração | Valor |
| --- | --- |
| Banco | `horacerta` |
| Host | `localhost` |
| Porta | `3307` |
| Usuário | `root` |

As configurações podem ser alteradas conforme o ambiente.

Credenciais do banco e a chave utilizada para assinatura dos tokens JWT devem ser configuradas localmente e não devem ser publicadas no repositório.

### Criando o banco

Caso o banco ainda não exista:

```sql
CREATE DATABASE horacerta
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

Na inicialização do backend, o **Flyway** executará automaticamente as migrações disponíveis em:

```text
src/main/resources/db/migration
```

As migrações criam a estrutura necessária e inserem os dados utilizados para demonstração da aplicação.

---

## Executando o backend

### 1. Clone o repositório

```bash
git clone https://github.com/Marii65/HoraCerta.git
cd HoraCerta
```

### 2. Acesse a pasta do backend

Entre na pasta que contém o arquivo:

```text
pom.xml
```

### 3. Execute a aplicação

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

A API será disponibilizada em:

```text
http://localhost:8080
```

---

## Executando o frontend

Em outro terminal, acesse a pasta do frontend.

Instale as dependências:

```bash
npm install
```

Execute o ambiente de desenvolvimento:

```bash
npm run dev
```

A interface estará disponível em:

```text
http://localhost:5173
```

---

## Usuários de demonstração

O banco inicial possui três usuários para demonstração dos diferentes níveis de acesso:

| Perfil | E-mail | Senha |
| --- | --- | --- |
| Administrador | `admin@horacerta.com` | `HoraCerta@2026` |
| Técnico | `tecnico@horacerta.com` | `HoraCerta@2026` |
| Operador | `operador@horacerta.com` | `HoraCerta@2026` |

> Essas credenciais são destinadas exclusivamente ao ambiente de demonstração do projeto.

---

## Documentação da API

Com o backend em execução:

**Swagger UI**

```text
http://localhost:8080/swagger-ui/index.html
```

**OpenAPI JSON**

```text
http://localhost:8080/v3/api-docs
```

O Swagger permite visualizar os endpoints, parâmetros, DTOs e operações disponibilizadas pela API.

As principais classes e operações do backend também possuem documentação Javadoc.

---

## Testes automatizados

O projeto possui testes de integração para os principais fluxos da aplicação, incluindo:

- autenticação;
- autorização e perfis de acesso;
- equipamentos;
- planos de manutenção;
- associação entre equipamentos e planos;
- leituras de horímetro;
- ordens de serviço;
- regras de negócio relevantes.

Para executar todos os testes:

```bash
.\mvnw.cmd test
```

Os testes foram executados com sucesso durante a validação final do projeto.

---

## Estrutura geral

```text
HoraCerta/
├── backend/
│   └── API Spring Boot
│
├── frontend/
│   └── Aplicação Vue
│
└── README.md
```

> A estrutura exata das pastas pode variar conforme a organização do repositório.

---

## Demonstração

**Vídeo de apresentação:** a ser adicionado.

O vídeo apresenta o problema abordado, as principais funcionalidades do HoraCerta e uma demonstração do fluxo da aplicação.

---

## Autoria

Desenvolvido por **Maria Luiza** como desafio técnico de desenvolvimento de software para a [Support Sistemas](https://supportsistemas.com.br/).
