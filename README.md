# PlannerEventos

Sistema de Gestão de Eventos e Inscrições: uma API REST para cadastro e organização
de eventos, gerenciamento de participantes e controle de inscrições, com controle de
capacidade, prazos, lotes de valor e status de eventos e inscrições.

Esse projeto está sendo desenvolvido como um trabalho da matéria Qualidade de Software,
ministrada pelo Prof. Dr. Anisio Silva, no 6º período do Curso Tecnologia em Análise e Desenvolvimento
de Sistemas, no IFSP Campus Boituva.

## Integrantes

- Bruna Serra Amorim
- Helicássia Jesus da Silva
- João Victor Yudi Mizuno
- Victoria Benfica de Oliveira

## Pré-requisitos

- Java 21
- Maven (o projeto já inclui o Maven Wrapper, então não é obrigatório ter o Maven instalado)

## Como executar

Na raiz do projeto, rode:

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

A aplicação sobe por padrão na porta `8080`. Os dados ficam em memória, então são apagados
sempre que a aplicação é reiniciada.

### Com Docker

```bash
docker build -t planner-eventos .
docker run -p 8080:8080 planner-eventos
```

## Como rodar os testes

Na raiz do projeto:

**Linux/Mac:**
```bash
./mvnw test
```

**Windows:**
```bash
mvnw.cmd test
```

Os testes são unitários, escritos com **JUnit 5** e **Mockito**. Os repositórios, as estratégias e os
notificadores são isolados com mocks, e o `verify()` garante que as dependências foram chamadas
corretamente. Os testes cobrem:

- as regras de elegibilidade e as políticas de cancelamento de cada modalidade;
- o cálculo do valor final por lote;
- o ciclo de vida da inscrição (`PENDENTE` → `CONFIRMADA` ou `CANCELADA`);
- o disparo das notificações e a resiliência a falhas no envio;
- o prazo de confirmação da vaga: o cálculo do limite, a recusa de inscrições e confirmações fora do
  prazo, a formatação do tempo restante, o reenvio periódico dos lembretes e a expiração automática
  das inscrições pendentes;
- o cancelamento das inscrições pendentes quando as vagas do evento se esgotam;
- os cenários de erro (exceções personalizadas);
- a emissão de comprovantes e a geração do hash do QR Code.

## Integração contínua e qualidade

A cada push na `main` e a cada pull request, o GitHub Actions compila o projeto, roda os testes e
envia a análise para o **SonarQube Cloud**. O pull request só pode ser integrado se passar no
Quality Gate, que exige, entre outros critérios, cobertura de testes mínima de 80% e no máximo 3% de código
duplicado.

## Base da API

Todos os endpoints são servidos sob o prefixo `/api`.

Exemplo local: `http://localhost:8080/api/eventos`

## Modalidades de evento

Todo evento possui uma modalidade, informada no campo `modalidade` do cadastro
(quando omitida, o evento é cadastrado como `ABERTO`).

| Modalidade | Elegibilidade para se inscrever | Cancelamento da inscrição | Comprovante | Valor da inscrição |
|---|---|---|---|---|
| `ABERTO` | Existência de vagas disponíveis | Livre, até o início do evento | Simples | Sempre gratuito |
| `EXCLUSIVO_ALUNOS` | Participante com número de matrícula informado e ativo | Até 24 horas antes do início do evento | Digital completo (com QR Code) | Valor base com desconto do lote |
| `RESTRICAO_IDADE` | Idade na data do evento maior ou igual à `idadeMinima` (exige data de nascimento no cadastro do participante) | Exige o motivo do cancelamento | Digital completo (com QR Code) | Valor base com desconto do lote |

## Ciclo de vida da inscrição

- Toda inscrição nasce como **`PENDENTE`**. Uma inscrição pendente **não ocupa vaga**.
- **`PENDENTE` → `CONFIRMADA`**: só é permitido se o evento ainda tiver vagas **e** se o prazo de
  confirmação não tiver terminado (o prazo termina algumas horas antes do início do evento; veja
  "Funcionalidade inovadora"). A inscrição confirmada ocupa uma vaga e recebe o comprovante.
- **`PENDENTE` ou `CONFIRMADA` → `CANCELADA`**: aplica a política de cancelamento da modalidade do evento.
  Cancelar uma inscrição confirmada **libera a vaga**.
- **`CANCELADA`** é estado final: não pode ser confirmada nem cancelada de novo.
- Transições inválidas retornam `422` com a mensagem do motivo.
- Não é possível se inscrever em um evento cujo prazo de confirmação já terminou (`422`).
- Quando um **evento** é cancelado, todas as inscrições ativas dele passam para `CANCELADA`, e os
  participantes são notificados.

### Transições automáticas

| De | Para | Quando | Motivo registrado |
|---|---|---|---|
| `PENDENTE` | `CANCELADA` | O prazo de confirmação vence sem que a inscrição seja confirmada | `Prazo de confirmacao expirado` |
| `PENDENTE` | `CANCELADA` | A última vaga do evento é confirmada por outro participante | `Vagas esgotadas` |

Nos dois casos, o participante é notificado por todos os canais.

## Lotes e valor da inscrição

Cada evento tem um **valor base** (`valorBase`) e um **lote ativo** (`loteAtivo`). O valor final da
inscrição é calculado no momento da inscrição, com o desconto do lote ativo naquele momento:

| Lote | Desconto | Valor final para um valor base de R$ 100,00 |
|---|---|---|
| `PROMOCIONAL` | 20% | R$ 80,00 |
| `LOTE_1` | 10% | R$ 90,00 |
| `LOTE_2` | 0% | R$ 100,00 |

- Eventos `ABERTO` são sempre gratuitos: o valor final é `0`, qualquer que seja o lote.
- Quando o lote não é informado no cadastro, o evento fica com `LOTE_2`.
- Cada inscrição guarda o lote e o valor final.

## Notificações

O participante é notificado por todos os canais cadastrados nas seguintes situações:

| Situação | Quem recebe |
|---|---|
| Inscrição **confirmada** | O participante da inscrição |
| Inscrição **cancelada** (pelo participante, pelo cancelamento do evento ou por expiração do prazo) | O participante da inscrição |
| **Lembrete de prazo**, periódico, com o tempo restante para confirmar | Participantes com inscrição `PENDENTE` |
| **Vagas esgotadas**: a inscrição `PENDENTE` é cancelada com o motivo `Vagas esgotadas` | Participantes com inscrição `PENDENTE` no evento lotado |

- **Canais:** e-mail (`NotificadorEmail`) e SMS (`NotificadorSMS`). O envio é simulado: a mensagem é
  registrada no log da aplicação.
- **Tipos de mensagem:** todo canal implementa `notificar(...)` (mudança de status),
  `notificarPrazoConfirmacao(...)` (tempo restante para confirmar) e `notificarVagasEsgotadas(...)`.
- **Extensível:** todo canal implementa a interface `Notificador`. Para criar um novo canal, basta
  adicionar uma classe anotada com `@Component` que implemente essa interface, sem alterar os serviços.
- **Resiliente:** uma falha no envio não interrompe nem desfaz a mudança de status. Todos os envios
  passam por um único ponto do `NotificacaoService` (`dispararParaTodos`): a exceção de um canal é
  capturada e registrada no log, e os demais canais continuam sendo notificados.

Exemplo do que aparece no log ao confirmar uma inscrição:

```
[E-MAIL] Para: maria.silva@example.com | Ola, Maria da Silva! Sua inscricao 1 no evento 'Semana de Tecnologia' esta CONFIRMADA.
[SMS] Para: Maria da Silva | Inscricao 1 em 'Semana de Tecnologia': CONFIRMADA.
```

Para ver a resiliência funcionando, altere em `application.properties`:

```properties
notificacao.sms.simular-falha=true
```

Com isso, o envio por SMS falha, o erro aparece no log, e a inscrição continua salva normalmente.

Ao confirmar uma inscrição, por exemplo, o log mostra:

```
INFO  NotificadorEmail   : [E-MAIL] Para: maria.silva@example.com | Ola, Maria da Silva! Sua inscricao 1 no evento 'Semana de Tecnologia' esta CONFIRMADA.
ERROR NotificacaoService : Falha ao enviar mudanca de status (CONFIRMADA) via SMS para a inscricao 1: Servico de SMS indisponivel (falha simulada)
java.lang.IllegalStateException: Servico de SMS indisponivel (falha simulada)
	at com.example.plannereventos.notificacao.NotificadorSMS.notificar(NotificadorSMS.java:...)
	...
```

Mesmo com a falha no SMS:

- a requisição responde normalmente (`200 OK` na confirmação, `204 No Content` no cancelamento);
- a inscrição fica salva com o novo status;
- o e-mail é enviado, porque cada canal é tratado separadamente.

## Comprovantes

O comprovante é gerado automaticamente quando a inscrição é **confirmada** e vem no campo
`comprovante` da resposta.

- **Simples** (eventos `ABERTO`): resumo textual da inscrição, sem QR Code.
- **Digital completo** (eventos `EXCLUSIVO_ALUNOS` e `RESTRICAO_IDADE`): dados detalhados do evento e
  um QR Code. O `hash` do QR Code é um SHA-256 gerado a partir do identificador do evento, do identificador
  do participante e do momento da inscrição, e vai dentro do `payload`.
- Apenas inscrições **confirmadas** têm comprovante. Nas consultas, inscrições pendentes ou canceladas
  aparecem sem o campo `comprovante`.

## Funcionalidade inovadora

### Prazo de confirmação com lembretes periódicos e expiração automática

Toda inscrição nasce `PENDENTE` e só ocupa vaga depois de confirmada. Para evitar que o participante
esqueça de confirmar, e que inscrições esquecidas fiquem pendentes para sempre, o sistema:

1. define um **prazo** para a confirmação;
2. envia **lembretes periódicos** com o tempo restante enquanto a inscrição estiver pendente;
3. **cancela automaticamente** a inscrição quando o prazo vence, avisando o participante.

**Regra do prazo**

- Limite de confirmação = início do evento (`data` + `horaInicio`) − `notificacao.prazo.confirmacao-horas`.
- Dentro do prazo, `PATCH .../confirmar` funciona normalmente.
- Depois do limite, a confirmação é recusada com `422`.
- Inscrever-se quando o limite já passou também é recusado com `422`.

**Lembretes periódicos**

O `LembretePrazoConfirmacaoScheduler`, anotado com `@Scheduled`, roda a cada
`notificacao.prazo.intervalo-ms` e percorre as inscrições `PENDENTE`:

- se o participante ainda não recebeu nenhum lembrete, ou se o último foi enviado há pelo menos
  `notificacao.prazo.intervalo-lembrete-minutos`, ele recebe um novo lembrete por e-mail e SMS;
- a própria `Inscricao` guarda o horário do último lembrete e decide se já pode receber outro
  (`deveReceberLembrete`);
- eventos cancelados e eventos ou participantes inexistentes são ignorados.

O primeiro lembrete chega no primeiro ciclo do agendador depois da inscrição.

**Expiração automática**

Quando o prazo vence, a inscrição `PENDENTE` passa para `CANCELADA` com o motivo
`Prazo de confirmacao expirado`, e o participante recebe a notificação de cancelamento. A partir daí,
os lembretes param. Inscrições confirmadas não recebem lembretes e não expiram.

**Resiliência**

Os avisos usam os mesmos canais do restante do sistema, por meio do `NotificacaoService`. Se um canal
falhar, o erro é registrado no log e os demais continuam. Uma falha em uma inscrição não interrompe o
processamento das outras, e o lembrete que falhou é tentado de novo no ciclo seguinte.

**Formato do tempo restante**

`Xh e Ymin` (por exemplo, `2h e 30min`), apenas horas (`5h`), apenas minutos quando falta menos de 1
hora (`45min`) ou `menos de 1min`.

Exemplo do que aparece no log:

```
[E-MAIL] Para: maria.silva@example.com | Ola, Maria da Silva! Voce tem 45min para CONFIRMAR sua vaga no evento 'Semana de Tecnologia' (inscricao 1).
[SMS] Para: Maria da Silva | Confirme sua vaga em 'Semana de Tecnologia' em ate 45min (inscricao 1).
...
Inscricao 1 cancelada: prazo de confirmacao expirado.
[E-MAIL] Para: maria.silva@example.com | Ola, Maria da Silva! Sua inscricao 1 no evento 'Semana de Tecnologia' esta CANCELADA.
```

**Configuração**

Em `application.properties`:

```properties
# Horas antes do início do evento em que o prazo de confirmação termina
notificacao.prazo.confirmacao-horas=1

# Intervalo entre lembretes para a mesma inscrição, em minutos
notificacao.prazo.intervalo-lembrete-minutos=15

# Frequência com que o agendador verifica as inscrições pendentes, em milissegundos (90 s)
notificacao.prazo.intervalo-ms=90000
```

| Propriedade | Valor no projeto | Padrão no código | Sugestão para um cenário real |
|---|---|---|---|
| `notificacao.prazo.confirmacao-horas` | `1` | `24` | `24` |
| `notificacao.prazo.intervalo-lembrete-minutos` | `15` | `15` | `360` (a cada 6 horas) |
| `notificacao.prazo.intervalo-ms` | `90000` | — | `90000` |


**Como testar**

1. Em `application.properties`, use `notificacao.prazo.intervalo-ms=10000` (10 s) e
   `notificacao.prazo.intervalo-lembrete-minutos=1`, e reinicie a aplicação.
2. Cadastre um evento (`POST /api/eventos`) que comece daqui a cerca de **1h10**: com o prazo de 1 hora,
   a confirmação vence em uns 10 minutos.
3. Cadastre dois participantes (`POST /api/participantes`) e inscreva os dois
   (`POST /api/eventos/{eventoId}/inscricoes`).
4. Acompanhe o log: um lembrete por minuto para cada um, com o tempo diminuindo.
5. Confirme só um deles (`PATCH .../confirmar`): retorna `200`, e ele para de receber lembretes.
6. Quando o prazo vencer, o log mostra o cancelamento do outro participante e a notificação de
   `CANCELADA`. Consulte a inscrição (`GET .../inscricoes/{participanteId}`): status `CANCELADA` e
   motivo `Prazo de confirmacao expirado`. Tentar confirmá-la retorna `422`.
7. Para ver a recusa da inscrição, use um evento cujo prazo já passou (por exemplo, que comece daqui a
   30 minutos): a inscrição retorna `422`.

## Endpoints

### Eventos
| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| POST | `/api/eventos` | Cadastrar evento | `201` |
| GET | `/api/eventos` | Listar eventos | `200` |
| GET | `/api/eventos/{id}` | Buscar por id | `200` |
| PUT | `/api/eventos/{id}` | Atualizar evento | `200` |
| PATCH | `/api/eventos/{id}/cancelamento` | Cancelar evento (e as inscrições ativas dele) | `200` |
| GET | `/api/eventos/{id}/vagas` | Consultar vagas | `200` |

### Participantes
| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| POST | `/api/participantes` | Cadastrar participante | `201` |
| GET | `/api/participantes` | Listar participantes | `200` |
| GET | `/api/participantes/{id}` | Buscar por id | `200` |
| PUT | `/api/participantes/{id}` | Atualizar participante | `200` |
| GET | `/api/participantes/{participanteId}/inscricoes` | Listar inscrições do participante | `200` |

### Inscrições
| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| POST | `/api/eventos/{eventoId}/inscricoes` | Inscrever participante (status `PENDENTE`) | `201` |
| GET | `/api/eventos/{eventoId}/inscricoes` | Listar inscrições do evento (com o nome do participante) | `200` |
| GET | `/api/eventos/{eventoId}/inscricoes/{participanteId}` | Consultar inscrição | `200` |
| PATCH | `/api/eventos/{eventoId}/inscricoes/{participanteId}/confirmar` | Confirmar inscrição dentro do prazo de confirmação (emite o comprovante) | `200` |
| PATCH | `/api/eventos/{eventoId}/inscricoes/{participanteId}` | Cancelar inscrição | `204` |

## Corpo das Principais Requisições

### [POST] /api/eventos
```json
{
  "titulo": "Semana de Tecnologia",
  "descricao": "Evento acadêmico com palestras e oficinas.",
  "data": "2026-12-15",
  "horaInicio": "19:00",
  "horaFim": "22:30",
  "local": "Auditório Principal",
  "capacidadeMaxima": 120,
  "modalidade": "RESTRICAO_IDADE",
  "idadeMinima": 18,
  "valorBase": 100.0,
  "loteAtivo": "PROMOCIONAL"
}
```

- `modalidade` (opcional): `ABERTO` (padrão), `EXCLUSIVO_ALUNOS` ou `RESTRICAO_IDADE`.
- `idadeMinima`: obrigatória e maior que 0 quando a modalidade é `RESTRICAO_IDADE`.
- `valorBase` (opcional): padrão `0`. Não pode ser negativo.
- `loteAtivo` (opcional): `PROMOCIONAL`, `LOTE_1` ou `LOTE_2` (padrão).
- `data`: não pode ser anterior à data atual.

### [PUT] /api/eventos/{id}
Mesmo corpo do cadastro.

### [PATCH] /api/eventos/{id}/cancelamento
```json
{
  "motivoCancelamento": "Falta de energia no local"
}
```

### [POST] /api/participantes
```json
{
  "nome": "Maria da Silva",
  "email": "maria.silva@example.com",
  "matricula": "2026001",
  "matriculaAtiva": true,
  "dataNascimento": "2000-05-10"
}
```

### [PUT] /api/participantes/{id}
```json
{
  "nome": "Maria da Silva",
  "email": "maria.silva@example.com",
  "matricula": "2026001",
  "matriculaAtiva": true,
  "dataNascimento": "2000-05-10"
}
```

### [POST] /api/eventos/{eventoId}/inscricoes
```json
{
  "participanteId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### [PATCH] /api/eventos/{eventoId}/inscricoes/{participanteId}/confirmar
Sem corpo.

### [PATCH] /api/eventos/{eventoId}/inscricoes/{participanteId}
```json
{
  "motivoCancelamento": "Conflito de horário"
}
```

O motivo é obrigatório para eventos `RESTRICAO_IDADE` e opcional nas outras modalidades.

## Exemplos de Respostas

### [POST] /api/eventos/{eventoId}/inscricoes
Evento `RESTRICAO_IDADE` com valor base de R$ 100,00 no lote `PROMOCIONAL`:

```json
{
  "id": 1,
  "eventoId": 1,
  "participanteId": "550e8400-e29b-41d4-a716-446655440000",
  "nomeParticipante": "Maria da Silva",
  "dataCriacao": "2026-11-16T09:30:00",
  "status": "PENDENTE",
  "loteAtivo": "PROMOCIONAL",
  "valorFinal": 80.0,
  "motivoCancelamento": null
}
```

### [PATCH] /api/eventos/{eventoId}/inscricoes/{participanteId}/confirmar
Evento `ABERTO`:

```json
{
  "id": 1,
  "eventoId": 1,
  "participanteId": "550e8400-e29b-41d4-a716-446655440000",
  "nomeParticipante": "Maria da Silva",
  "dataCriacao": "2026-11-16T09:30:00",
  "status": "CONFIRMADA",
  "loteAtivo": "LOTE_2",
  "valorFinal": 0.0,
  "motivoCancelamento": null,
  "comprovante": {
    "tipo": "SIMPLES",
    "inscricaoId": 1,
    "eventoId": 1,
    "participanteId": "550e8400-e29b-41d4-a716-446655440000",
    "dataInscricao": "2026-11-16T09:30:00",
    "resumo": "COMPROVANTE DE INSCRICAO | Inscricao: 1 (CONFIRMADA) | Participante: Maria da Silva | Evento: Semana de Tecnologia | Data: 15/12/2026 as 19:00 | Local: Auditório Principal"
  }
}
```

### [GET] /api/eventos/{id}/vagas
Somente as inscrições `CONFIRMADA` ocupam vaga.

```json
{
  "id": 1,
  "capacidadeMaxima": 120,
  "inscricoesConfirmadas": 37,
  "vagasDisponiveis": 83
}
```

## Tratamento de Erros

Todas as respostas de erro seguem o mesmo formato:

```json
{
  "timestamp": "2026-11-16T19:30:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "mensagens": [
    "O evento 1 nao possui vagas disponiveis."
  ],
  "path": "/api/eventos/1/inscricoes/550e8400-e29b-41d4-a716-446655440000/confirmar"
}
```

| Status | Quando acontece |
|---|---|
| `400` | Dados obrigatórios ausentes ou em formato inválido (validação da requisição) |
| `404` | Evento, participante ou inscrição não encontrados |
| `422` | Violação de regra de negócio: evento sem vagas, inscrição duplicada, evento cancelado ou já iniciado, participante não elegível, cancelamento fora do prazo, motivo obrigatório, transição de status inválida, prazo de confirmação encerrado (na inscrição ou na confirmação) |
| `500` | Erro inesperado (a mensagem interna não é exposta) |

Exemplo de mensagem quando o prazo de confirmação já terminou:

```json
"mensagens": [
  "O prazo para confirmar a vaga terminou (ate 1h antes do inicio do evento)."
]
```

O número de horas da mensagem acompanha o valor de `notificacao.prazo.confirmacao-horas`.

## Arquitetura e princípios SOLID

O projeto segue a separação em camadas: **controllers** (entrada HTTP), **services** (orquestração das
regras), **model** (entidades e regras do próprio domínio), **repositories** (armazenamento em memória)
e **strategies** (regras que variam por modalidade).

| Princípio | Onde aparece no projeto |
|---|---|
| **SRP** (Responsabilidade Única) | Controllers só tratam HTTP; services orquestram; a entidade `Inscricao` protege as próprias transições de status; o `NotificacaoService` só dispara notificações; o `QrCodeHashService` só gera o hash; o `LembretePrazoConfirmacaoScheduler` só orquestra o ciclo do prazo: a regra de reenvio fica na `Inscricao`, o cálculo do tempo restante no `EventoService` e o envio no `NotificacaoService`. |
| **OCP** (Aberto/Fechado) | Elegibilidade, cancelamento, comprovante e cálculo por lote são estratégias escolhidas por modalidade, por meio de um `Map`. Uma modalidade nova ou um novo canal de notificação entram com uma classe nova, sem alterar os serviços. |
| **LSP** (Substituição de Liskov) | Qualquer implementação de `ElegibilidadeStrategy`, `PoliticaCancelamentoStrategy`, `EmissorComprovanteStrategy`, `CalculoValorFinalStrategy` ou `Notificador` pode substituir a interface sem quebrar o comportamento esperado. |
| **ISP** (Segregação de Interfaces) | A geração de QR Code fica numa interface separada (`GeradorQrCode`). O emissor de comprovante simples não é obrigado a implementá-la, então nunca lança `UnsupportedOperationException`. |
| **DIP** (Inversão de Dependência) | Os serviços recebem listas das interfaces por injeção de dependência (`List<ElegibilidadeStrategy>`, `List<Notificador>` e outras) e não dependem das implementações concretas. |