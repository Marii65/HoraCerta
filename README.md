# HoraCerta — Monitoramento de Manutenção Preventiva

Sistema para gerenciamento de equipamentos e acompanhamento de manutenções preventivas por horímetro. O HoraCerta foi desenvolvido com o objetivo de facilitar o controle dos equipamentos, organizar os planos de manutenção e identificar serviços próximos do vencimento ou atrasados.

**Repositório:** [github.com/Marii65/HoraCerta](https://github.com/Marii65/HoraCerta)

## Objetivo

O HoraCerta busca facilitar o acompanhamento da manutenção preventiva de equipamentos cuja utilização é medida em horas de operação.

A partir das leituras de horímetro e dos intervalos definidos nos planos de manutenção, o sistema permite acompanhar a situação de cada equipamento e antecipar serviços antes que ultrapassem o período recomendado.

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

## Perfis de acesso

O sistema possui três níveis de acesso:

| Perfil | Permissões |
| --- | --- |
| `ADMIN` | Acesso administrativo, gerenciamento de equipamentos, planos, usuários e demais operações |
| `TECHNICIAN` | Operações relacionadas às manutenções, planos e ordens de serviço |
| `OPERATOR` | Consulta de informações e registro de leituras de horímetro |

As permissões são validadas no backend por meio do **Spring Security**.

## Tecnologias

### Backend

| **Tecnologia**              | **Utilização**                              |
| --------------------------- | ------------------------------------------- |
| Java 21                     | Linguagem de programação                    |
| Spring Boot                 | Desenvolvimento da API REST                 |
| Spring Web                  | Endpoints HTTP                              |
| Spring Data JPA e Hibernate | Persistência e mapeamento objeto-relacional |
| Spring Security             | Autenticação e autorização                  |
| JWT                         | Autenticação baseada em token               |
| Bean Validation             | Validação dos dados de entrada              |
| MySQL                       | Banco de dados relacional                   |
| Flyway                      | Versionamento das migrações do banco        |
| Maven Wrapper               | Compilação e execução do backend            |
| SpringDoc OpenAPI           | Documentação interativa da API              |

### Frontend

| **Tecnologia**              | **Utilização**                  |
| --------------------------- | ----------------------------------- |
| Vue 3                       | Construção da interface             |
| Vite                        | Ambiente de desenvolvimento e build |
| Vue Router                  | Navegação entre páginas             |
| Pinia                       | Gerenciamento de estado             |
| Fetch API                   | Comunicação com a API REST          |
| Lucide                      | Ícones da interface                 |

## Arquitetura

O backend foi organizado em camadas, separando as responsabilidades e facilitando a manutenção da aplicação:

- **Controllers:** recebem as requisições HTTP e encaminham as operações aos serviços.
- **DTOs:** definem os dados de entrada e saída da API.
- **Entities:** representam as entidades persistidas no banco de dados.
- **Repositories:** realizam consultas e operações de persistência.
- **Services:** concentram as regras de negócio.
- **Exceptions:** organizam o tratamento de erros da aplicação.
- **Security:** reúne os componentes relacionados à autenticação e à autorização.

O frontend está separado do backend e organizado em páginas, componentes reutilizáveis e serviços responsáveis pela comunicação com a API.

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

O relacionamento `EquipmentPlan` permite que um mesmo plano seja utilizado por diferentes equipamentos.

As alterações estruturais do banco são gerenciadas por migrações do Flyway.

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

# Executando o projeto

## Pré-requisitos

Antes de executar o HoraCerta, tenha instalado:

- JDK 21
- MySQL Server
- Node.js
- Git

O Maven Wrapper está incluído no backend, dispensando a instalação separada do Maven.

## Configuração do banco de dados

O projeto utiliza **MySQL** com as seguintes configurações padrão:

| Configuração | Valor |
| --- | --- |
| Banco de dados | `horacerta` |
| Host | `localhost` |
| Porta | `3307` |
| Usuário | `root` |

Caso necessário, ajuste as configurações de conexão no `application.properties` de acordo com o seu ambiente.

### Iniciando o MySQL

Antes de executar o backend, certifique-se de que o **servidor MySQL está em execução**.

Você pode iniciar o MySQL pela ferramenta utilizada em seu ambiente, como:

- **XAMPP:** abra o XAMPP Control Panel e inicie o serviço **MySQL**;
- **MySQL Server:** inicie o serviço do MySQL instalado no sistema;
- ou utilize outra ferramenta de gerenciamento do MySQL de sua preferência.

> O HoraCerta está configurado para acessar o MySQL pela porta `3307`. Caso seu MySQL utilize outra porta, altere `spring.datasource.url` no `application.properties`.

Não é necessário criar o banco de dados manualmente. Ao iniciar o backend, o banco `horacerta` será criado automaticamente caso ainda não exista.

Em seguida, o **Flyway** executará automaticamente as migrações disponíveis em:

```text
src/main/resources/db/migration
```

As migrações são responsáveis pela criação das tabelas e pela inserção dos dados utilizados para demonstração da aplicação.

## Configuração do JWT

A aplicação utiliza JWT para autenticação. Antes de iniciar o backend, é necessário configurar uma chave secreta para assinatura dos tokens.

A chave deve estar no formato **Base64** e, após decodificada, possuir pelo menos **32 bytes**.

### Gerando a chave no Windows

Abra o **PowerShell** e execute:

```powershell
$rng = New-Object System.Security.Cryptography.RNGCryptoServiceProvider
$bytes = New-Object byte[] 32
$rng.GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

O último comando exibirá a chave gerada no terminal. Copie esse valor.

Em seguida, ainda no **PowerShell**, registre a chave como uma variável de ambiente do usuário:

```powershell
[Environment]::SetEnvironmentVariable("JWT_SECRET", "SUA_CHAVE_GERADA", "User")
```

Por exemplo, se a chave gerada fosse:

```text
AbCdEf1234567890GhIjKlMnOpQrStUvWxYz1234567=
```

o comando ficaria:

```powershell
[Environment]::SetEnvironmentVariable("JWT_SECRET", "AbCdEf1234567890GhIjKlMnOpQrStUvWxYz1234567=", "User")
```

> O valor acima é apenas um exemplo. Utilize a chave gerada no seu próprio PowerShell.

Após configurar a variável, **feche o terminal e abra um novo PowerShell** antes de iniciar a aplicação.

Para verificar se a variável foi configurada, execute:

```powershell
$env:JWT_SECRET
```

O backend utiliza essa variável no `application.properties` mas não é necessário alterar, a linha deve permanecer:

```properties
security.jwt.secret=${JWT_SECRET}
```

> A chave JWT é uma configuração local e não deve ser publicada no repositório.

## Como executar o backend

### 1. Clonar o repositório

```bash
git clone https://github.com/Marii65/HoraCerta.git
cd HoraCerta
```
### 2. Acessar a pasta do backend

Entre na pasta com `cd backend/horaCerta`.

### 3. Execute a aplicação

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```
### 4. Ou execute a aplicação diretamente na sua IDE

A API será disponibilizada em:

```text
http://localhost:8080
```

## Executando o frontend

Em outro terminal, acesse a pasta do frontend:
```bash
cd frontend/hora-certa
```
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

## Usuários de demonstração

O banco inicial possui três usuários para demonstração dos diferentes níveis de acesso:

| Perfil | E-mail | Senha |
| --- | --- | --- |
| Administrador | `admin@horacerta.com` | `HoraCerta@2026` |
| Técnico | `tecnico@horacerta.com` | `HoraCerta@2026` |
| Operador | `operador@horacerta.com` | `HoraCerta@2026` |

> Essas credenciais são destinadas exclusivamente ao ambiente de demonstração do projeto.

## Documentação da API

Com o backend em execução, acesse:

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- [Especificação OpenAPI em JSON](http://localhost:8080/v3/api-docs)

O Swagger UI permite consultar os endpoints, visualizar os parâmetros e testar as operações disponibilizadas pela API, respeitando as permissões de acesso.

As principais classes e operações do código também possuem documentação Javadoc.

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

## Estrutura do projeto

O HoraCerta mantém backend, frontend e documentação separados dentro do mesmo repositório:
```text
HoraCerta/
├── backend/
│   └── horaCerta/
│       ├── src/
│       ├── pom.xml
│       ├── mvnw
│       └── mvnw.cmd
│
├── frontend/
│   └── hora-certa/
│       ├── public/
│       ├── src/
│       ├── package.json
│       └── vite.config.js
│
├── docs/
│   └── ARQUITETURA-E-REGRAS.md
│
└── README.md
```

backend/horaCerta — API REST desenvolvida com Java e Spring Boot.

frontend/hora-certa — aplicação web desenvolvida com Vue 3 e Vite.

docs — documentação complementar sobre arquitetura e regras do sistema.

README.md — documentação principal para instalação, execução e apresentação do projeto.

## Demonstração

**Vídeo de apresentação:** a ser adicionado.

## Autoria

Desenvolvido por **Maria Luiza** como desafio técnico de desenvolvimento de software para a [Support](https://supportsistemas.com.br/).
