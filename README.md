docker run --name trade-optimizer \
-e POSTGRES_DB=tradeopt \
-e POSTGRES_USER=user \
-e POSTGRES_PASSWORD=password \
-p 5433:5432 \
-d postgres:16
