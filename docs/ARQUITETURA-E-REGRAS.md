### HoraCerta — Arquitetura e Regras de Negócio

**Autoria:** Maria Luiza S. Nascimento



##### 1\. Objetivo

O HoraCerta é um sistema para gerenciamento de equipamentos e acompanhamento de manutenção preventiva por horímetro. Esta documentação foi desenvolvida para estudo e planejamento da aplicação. Descreve a organização arquitetural, as responsabilidades dos serviços, as regras de negócio e os mecanismos de segurança e testes previstos no projeto.



##### 2\. Arquitetura em camadas

O projeto está organizado em camadas. De forma geral:

* **Controllers:** recebem as requisições HTTP, validam os dados de entrada e encaminham as operações para os serviços.
* **DTOs:** representam os dados recebidos e devolvidos pela API.
* **Services:** concentram as regras de negócio e coordenam as operações.
* **Entidades e enums:** representam os conceitos do domínio e seus estados.
* **Repositories:** encapsulam o acesso aos dados persistidos.
* **Configuração de segurança:** define as regras de acesso e a autenticação da API.





##### 3\. Funcionalidades e regras de negócio

###### 3.1. Usuários — `UserService`

Responsável pelo cadastro e gerenciamento de contas.

Regras documentadas no planejamento:

* Validar nome, e-mail e senha no cadastro.
* Normalizar o e-mail, removendo espaços externos e convertendo-o para minúsculas.
* Impedir o cadastro de e-mails já existentes.
* Armazenar senhas usando `PasswordEncoder`, sem persistir senhas em texto puro.
* Atribuir `Role.OPERATOR` ao cadastro público, sem permitir que o usuário escolha a própria função.
* Permitir listar usuários, buscar por identificador e atualizar nome e e-mail.
* Ao atualizar o e-mail, impedir que ele já pertença a outra conta.
* Restringir a exclusão quando ela comprometer registros relacionados.



###### 3.2. Equipamentos — `EquipmentService`

Responsável pelo cadastro e gerenciamento dos equipamentos monitorados.

Regras documentadas no planejamento:

* Exigir código e nome no cadastro e impedir códigos duplicados.
* Permitir atualizar dados cadastrais, consultar equipamentos e buscar por identificador.
* Permitir filtros por status e categoria.
* Não aceitar horímetro negativo nem reduzir o horímetro atual.
* Evitar a exclusão de equipamentos com histórico ou vínculos que precisem ser preservados.
* Atualizar o horímetro atual pelo fluxo de registro de leitura, e não livremente pelo formulário de edição, para preservar a consistência do histórico.



###### 3.3. Planos de manutenção — `MaintenancePlanService`

Responsável por definir planos que podem ser aplicados aos equipamentos.

Regras documentadas no planejamento:

* Exigir nome para o plano.
* Permitir descrição de até 500 caracteres.
* Permitir listar, buscar, atualizar e excluir planos, respeitando vínculos existentes.
* Permitir ativar e desativar um plano.
* Não permitir novos vínculos de equipamentos a planos inativos.
* Preservar planos já utilizados no histórico, evitando exclusões que comprometam ordens de serviço.



###### 3.4. Itens dos planos — `MaintenancePlanItemService`

Responsável por cadastrar as atividades que compõem cada plano.

Exemplo de uso: troca de óleo a cada 250 horas de operação, com alerta antecipado.

Regras documentadas no planejamento:

* Exigir que o plano informado exista.
* Exigir descrição do serviço.
* Exigir intervalo de manutenção maior que zero.
* Não permitir margem de alerta negativa.
* Representar intervalo e margem com valores numéricos decimais, por exemplo `250.00` e `20.00`.
* Não permitir novos itens em planos inativos.
* Preservar o histórico de itens referenciados por ordens de serviço.
* Validar que a margem de alerta seja menor que o intervalo de manutenção.



###### 3.5. Vínculos entre equipamentos e planos — `EquipmentPlanService`

Responsável por aplicar um plano de manutenção a um equipamento.

Regras documentadas no planejamento:

* Exigir que equipamento e plano existam.
* Exigir que o plano esteja ativo para criar um novo vínculo.
* Impedir vínculos duplicados entre o mesmo equipamento e plano.
* Se um vínculo existente estiver inativo, reativá-lo em vez de criar outro registro.
* Ao criar ou reativar o vínculo, usar o horímetro atual do equipamento como referência inicial.
* Permitir desativar o vínculo sem apagar o histórico.
* Não permitir novas ordens preventivas para vínculos desativados.
* Permitir que um plano seja aplicado a vários equipamentos e que um equipamento possua vários planos.



###### 3.6. Leituras de horímetro — `HorometerReadingService`

Responsável por registrar o histórico de utilização dos equipamentos.

Regras documentadas no planejamento:

* Exigir que o equipamento exista.
* Não aceitar valores de leitura negativos.
* Não aceitar uma leitura inferior ao horímetro atual do equipamento.
* Identificar pelo sistema o usuário responsável pela leitura, sem confiar em um usuário escolhido livremente no formulário.
* Registrar a data e hora da leitura.
* Atualizar o horímetro atual do equipamento quando a leitura for válida.
* Preservar o histórico de leituras anteriores.
* Permitir consultar leituras por equipamento e período.





###### 3.7. Ordens de serviço — `WorkOrderService`

Responsável por controlar manutenções previstas e executadas.

Regras documentadas no planejamento:

* Vincular a ordem a um plano aplicado ao equipamento.
* Quando houver um item de manutenção, validar que ele pertença ao plano associado.
* Permitir ordens preventivas e corretivas.
* Definir o status inicial pelo sistema, sem permitir que o cliente cadastre uma ordem já concluída livremente.
* Validar as transições de status permitidas.
* Registrar o horímetro previsto quando aplicável.
* Ao concluir uma ordem, exigir o horímetro executado e registrar a data de conclusão.
* Não aceitar horímetro executado negativo nem incompatível com o histórico do equipamento.
* Impedir a conclusão repetida de uma ordem já concluída.
* Preservar o histórico de ordens canceladas ou concluídas.
* Permitir filtrar ordens por equipamento, status, tipo e período.
* Calcular a proximidade do vencimento de manutenções com base no horímetro atual, no horímetro de referência e no intervalo do item de manutenção.



##### 4\. Segurança e autenticação

O planejamento descreve os seguintes componentes de segurança. 

###### `JwtService`

Responsabilidades descritas:

* Gerar tokens JWT assinados que identificam o usuário.
* Extrair a identificação do usuário após validar o token.
* Verificar a validade temporal e a assinatura do token.
* Extrair claims somente após a validação apropriada.



###### `JwtAuthenticationFilter`

Responsabilidades descritas:

* Ler o cabeçalho `Authorization`.
* Identificar tokens no formato `Bearer`.
* Extrair a identificação do usuário do token.
* Buscar o usuário no banco de dados.
* Verificar se o usuário existe, está ativo e se o token é válido.
* Registrar a autenticação e as permissões correspondentes ao papel do usuário.

###### 

###### `SecurityConfig`

Responsável pela configuração de segurança da API, incluindo regras para endpoints públicos e protegidos, gerenciamento de sessões, CSRF, `PasswordEncoder` e posicionamento do filtro JWT.



###### `AuthService` e `AuthController`

O planejamento atribui a esses componentes o cadastro e o login:

* O serviço de autenticação reutiliza o cadastro de usuários para evitar duplicação de validações.
* O login normaliza o e-mail, verifica as credenciais, considera o estado da conta e gera um token.
* O controlador expõe as operações HTTP de cadastro e login e retorna os status apropriados.



##### 5\. Testes automatizados

O documento de planejamento lista testes de integração para os seguintes fluxos:



###### Equipamentos — `EquipmentControllerIntegrationTest`

* Cadastro válido e resposta `201 Created`.
* Validação de dados inválidos.
* Tratamento de código duplicado.
* Listagem de equipamentos.
* Busca por identificador e tratamento de registro inexistente.
* Exclusão e tratamento de equipamentos com histórico vinculado.



###### Planos de manutenção — `MaintenancePlanControllerIntegrationTest`

* Criação de plano.
* Alternância de status entre ativo e inativo.
* Inclusão de item válido em plano ativo.
* Tratamento de tentativa de incluir item em plano inativo.



###### Vínculos — `EquipmentPlanControllerIntegrationTest`

* Associação de plano a equipamento.
* Consulta de planos vinculados a um equipamento.
* Ativação e desativação de vínculo.



###### Leituras — `HorometerReadingControllerIntegrationTest`

* Registro de leitura e atualização do horímetro atual.
* Tratamento de leituras inferiores ao horímetro atual.
* Consulta do histórico por intervalo de datas.



###### Ordens de serviço — `WorkOrderControllerIntegrationTest`

* Criação ou agendamento de ordem.
* Conclusão com horímetro executado e validação correspondente.
* Cancelamento de ordem pendente ou agendada.
* Filtragem de ordens por critérios disponíveis na API.





##### 6\. Relação com a documentação da API

A documentação interativa da API é disponibilizada pelo SpringDoc/OpenAPI, se a dependência e a configuração estiverem presentes no projeto. O Javadoc complementa essa documentação ao explicar classes e métodos Java; ele não substitui a documentação interativa dos endpoints HTTP.



##### 

