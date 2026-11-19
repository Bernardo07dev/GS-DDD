# Fila Invisível — API RESTful (GS Domain Driven Design)

API RESTful em **Java 21 + Spring Boot 3.3.4** conectada ao **Oracle Database FIAP**, desenvolvida para a **FIAP Global Solution 2026.2 (CitizenTech)**.

O **Fila Invisível** recebe registros de movimentação de fila e previsões de lotação (geradas pelo módulo de Data Science em Python), persiste no Oracle e orienta o cidadão — como a persona **Dona Marlene (68 anos)** — com o melhor horário para ir à Unidade Básica de Saúde (UBS) por meio de um semáforo simples (**VERDE**, **AMARELO**, **VERMELHO**), alinhado aos **ODS 3, 10 e 11**.

---

## Integrantes da Equipe

- **Bernardo Moreira** — RM 564103
- **Pedro Batista** — RM 563220
- **Renan Jordão** — RM 560618
- **Larissa Shiba** — RM 560462

---

## Stack Técnica

- **Java 21**
- **Spring Boot 3.3.4** (Maven Wrapper)
- **Spring Web** (API RESTful JSON)
- **Spring JDBC (`JdbcTemplate`)** (Camada DAO com SQL explícito)
- **Oracle JDBC (`ojdbc11`)** (Banco de Dados Oracle FIAP)
- **Jakarta Bean Validation** (Validação declarativa nos DTOs)

---

## Arquitetura e Padrões de Projeto

```text
src/main/java/br/com/fiap/filainvisivel/
├── FilaInvisivelApplication.java
├── config/             # WebConfig (CORS)
├── model/              # Ubs, Especialidade, RegistroFila, Previsao, Alerta, Semaforo (Enum)
├── factory/            # PrevisaoFactory (cálculo do semáforo) e AlertaFactory (anonimização)
├── dto/
│   ├── request/        # Records de entrada validados
│   └── response/       # Records de saída (inclui MelhorHorarioResponse)
├── dao/                # Interfaces DAO
│   └── impl/           # Implementações JdbcTemplate + RowMapper
├── bo/                 # Camada Business Object com regras de negócio
├── controller/         # Controllers RESTful (GET, POST, PUT, DELETE)
└── exception/          # GlobalExceptionHandler (@RestControllerAdvice) e exceções customizadas
```

### Regras de Negócio (Camada BO + Factory)
- **Semáforo configurável (`PrevisaoFactory`):** `probLotacao < 0.40` → `VERDE`; `0.40 <= probLotacao < 0.70` → `AMARELO`; `probLotacao >= 0.70` → `VERMELHO`.
- **Registros válidos (`RegistroFilaBo`):** rejeita `pessoasNaFila` ou `tempoEsperaMin` negativos (`400 Bad Request`).
- **Previsão única por UBS e horário (`PrevisaoBo`):** impede duplicidade no mesmo `(ubsId, dataHora)` (`409 Conflict`).
- **Privacidade por padrão (`AlertaFactory` + `AlertaBo`):** contato do cidadão é sempre anonimizado antes da persistência (`anon-<hash>-***<sufixo>`), sem armazenar dados pessoais reais.
- **Recomendação ao cidadão (`UbsBo`):** `GET /api/ubs/{id}/melhor-horario` seleciona o horário com menor probabilidade de lotação e menor tempo de espera estimado.

---

## Endpoints da API (`/api`)

### UBS
| Verbo | URI | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/ubs` | Lista todas as UBS com especialidades | `200 OK` |
| `GET` | `/api/ubs/{id}` | Busca UBS por ID | `200 OK` |
| `GET` | `/api/ubs/{id}/melhor-horario` | Retorna o melhor horário recomendado com semáforo | `200 OK` |
| `POST` | `/api/ubs` | Cadastra nova UBS e vínculos de especialidades | `201 Created` |
| `PUT` | `/api/ubs/{id}` | Atualiza UBS e especialidades | `200 OK` |
| `DELETE` | `/api/ubs/{id}` | Remove UBS e dependentes em cascata | `204 No Content` |

### Especialidades
| Verbo | URI | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/especialidades` | Lista todas as especialidades | `200 OK` |
| `GET` | `/api/especialidades/{id}` | Busca especialidade por ID | `200 OK` |
| `POST` | `/api/especialidades` | Cadastra especialidade | `201 Created` |
| `PUT` | `/api/especialidades/{id}` | Atualiza especialidade | `200 OK` |
| `DELETE` | `/api/especialidades/{id}` | Remove especialidade | `204 No Content` |

### Registros de Fila
| Verbo | URI | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/registros-fila` | Lista todos os registros de fila | `200 OK` |
| `GET` | `/api/registros-fila/{id}` | Busca registro por ID | `200 OK` |
| `GET` | `/api/ubs/{ubsId}/registros-fila` | Lista registros de fila por UBS | `200 OK` |
| `POST` | `/api/registros-fila` | Registra medição de fila | `201 Created` |
| `PUT` | `/api/registros-fila/{id}` | Atualiza registro de fila | `200 OK` |
| `DELETE` | `/api/registros-fila/{id}` | Remove registro de fila | `204 No Content` |

### Previsões
| Verbo | URI | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/previsoes` | Lista todas as previsões | `200 OK` |
| `GET` | `/api/previsoes/{id}` | Busca previsão por ID | `200 OK` |
| `GET` | `/api/ubs/{ubsId}/previsoes` | Lista previsões por UBS | `200 OK` |
| `POST` | `/api/previsoes` | Cadastra previsão calculando o semáforo | `201 Created` |
| `PUT` | `/api/previsoes/{id}` | Atualiza previsão e recalcula semáforo | `200 OK` |
| `DELETE` | `/api/previsoes/{id}` | Remove previsão | `204 No Content` |

### Alertas
| Verbo | URI | Descrição | Sucesso |
|---|---|---|---|
| `GET` | `/api/alertas` | Lista todos os alertas | `200 OK` |
| `GET` | `/api/alertas/{id}` | Busca alerta por ID | `200 OK` |
| `GET` | `/api/ubs/{ubsId}/alertas` | Lista alertas por UBS | `200 OK` |
| `POST` | `/api/alertas` | Cadastra alerta com anonimização automática | `201 Created` |
| `PUT` | `/api/alertas/{id}` | Atualiza alerta | `200 OK` |
| `DELETE` | `/api/alertas/{id}` | Remove alerta | `204 No Content` |

Respostas de erro padronizadas via `GlobalExceptionHandler`: `400 Bad Request`, `404 Not Found`, `405 Method Not Allowed`, `409 Conflict` e `500 Internal Server Error`.

---

## Como Executar

### Pré-requisitos
- JDK 21+ configurado no `PATH` ou `JAVA_HOME`
- Acesso à rede para o servidor Oracle da FIAP (`oracle.fiap.com.br:1521:ORCL`)

### Comandos

```powershell
# Rodar testes unitários
.\mvnw.cmd test

# Subir a API localmente (porta 8080)
.\mvnw.cmd spring-boot:run

# Gerar pacote JAR
.\mvnw.cmd clean package
```

### Variáveis de Ambiente (Oracle / Deploy)
As configurações em [application.properties](src/main/resources/application.properties) usam variáveis de ambiente com fallback para o ambiente FIAP:
- `PORT` (padrão `8080`)
- `DB_URL` (padrão `jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL`)
- `DB_USER`
- `DB_PASSWORD`

### Deploy no Render.com
- **URL base em produção:** [https://gs-ddd-upj0.onrender.com](https://gs-ddd-upj0.onrender.com)
- **Exemplo GET em produção:** `https://gs-ddd-upj0.onrender.com/api/ubs`

---

## Banco de Dados e Documentação

- DDL completo para Oracle: [schema.sql](schema.sql)
- Documentação completa da entrega (PDF único): [Fila-Invisivel-Documentacao-GS-DDD.pdf](Fila-Invisivel-Documentacao-GS-DDD.pdf)
