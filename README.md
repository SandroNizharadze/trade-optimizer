# Trade Optimizer

Spring Boot backend for selecting the combination of candidate trades that maximizes expected P&L without exceeding the available margin.

### Requirements

- Java 17+
- Docker
- Maven Wrapper included


###  Clone the repo:

`
git clone https://github.com/SandroNizharadze/trade-optimizer.git
`

`cd trade-optimizer
`

### Run

Start PostgreSQL:


`docker compose up -d
`

Build and test:


`./mvnw clean test
`

Run:

`./mvnw spring-boot:run
`

Or build and run the JAR:


`./mvnw clean package`

`java -jar target/trade-optimizer-*.jar
`

The API runs on `http://localhost:8080`.

## API

### Optimize trades

```bash
curl -X POST http://localhost:8080/api/v1/trades/optimize \
  -H "Content-Type: application/json" \
  -d '{
    "maxMargin": 15,
    "candidateTrades": [
      {"tradeName":"Trade Alpha","marginRequired":5,"expectedPnl":120},
      {"tradeName":"Trade Beta","marginRequired":10,"expectedPnl":200},
      {"tradeName":"Trade Gamma","marginRequired":3,"expectedPnl":80},
      {"tradeName":"Trade Delta","marginRequired":8,"expectedPnl":160}
    ]
  }' | jq
```

Successful optimization returns `201 Created`. If no trade fits, an empty selection with totals of zero is returned with `200 OK`.

### Get optimization

```bash
curl http://localhost:8080/api/v1/trades/{requestId} | jq
```

Returns `404` when the request ID does not exist.

### Audit history

```bash
curl "http://localhost:8080/api/v1/trades?page=0&size=20" | jq
```

Runs are returned newest first.
ce behavior. Controller tests cover HTTP validation/status codes. A Testcontainers integration test verifies the full POST → PostgreSQL → GET flow using a real PostgreSQL instance.