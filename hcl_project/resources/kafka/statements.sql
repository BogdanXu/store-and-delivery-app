--to enter ksqldb cli and execute this: docker exec -it ksqldb-cli ksql http://ksqldb-server:8088 -f /app/statements.sql
--what this is supposed to do: join the orders stream with their status, so we can query orders by statuses in a live stream

CREATE STREAM orders (
  order_id BIGINT KEY,
  order_time BIGINT,
  ordered_items MAP<STRING, INT>,
  address VARCHAR
) WITH (
  kafka_topic='order',
  value_format='json'
);

CREATE STREAM order_updates (
  order_id BIGINT,
  new_status VARCHAR
) WITH (
  kafka_topic='order_updates',
  value_format='json'
);

CREATE OR REPLACE STREAM updated_orders AS
SELECT o.*, ou.new_status
FROM orders AS o
INNER JOIN order_updates AS ou WITHIN 1 HOURS GRACE PERIOD 15 MINUTES ON o.order_id = ou.order_id;

