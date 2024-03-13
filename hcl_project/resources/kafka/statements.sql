--to enter ksqldb cli and execute this: docker exec -it ksqldb-cli ksql http://ksqldb-server:8088 -f /app/statements.sql
--what this is supposed to do: join the orders stream with their status, so we can query orders by statuses later

CREATE STREAM IF NOT EXISTS orders (
  orderid BIGINT,
  ordertime BIGINT,
  ordereditems MAP<STRING, INT>,
  address VARCHAR)
WITH (kafka_topic='order',
      value_format='json');

CREATE STREAM IF NOT EXISTS order_updates (
  orderid BIGINT,
  status VARCHAR
) WITH (
  kafka_topic='order_updates',
  value_format='json'
);

CREATE STREAM IF NOT EXISTS updated_orders AS
SELECT o.*, ou.status
FROM orders AS o
INNER JOIN order_updates AS ou WITHIN 1 HOURS GRACE PERIOD 15 MINUTES ON o.orderid = ou.orderid;

-- CREATE TABLE orders_table AS -- materialized view, it doesn't accept map fields for some reason, need to study it
-- SELECT
--     o_orderid,
--     o_ordertime,
--     o_ordereditems,
--     o_address,
--     status
-- FROM updated_orders
-- EMIT CHANGES;

CREATE TABLE IF NOT EXISTS orders_table (
      o_orderid BIGINT PRIMARY KEY ,
      o_ordertime BIGINT,
      o_ordereditems MAP<STRING, INT>,
      o_address VARCHAR,
      status VARCHAR )
WITH (
    kafka_topic='UPDATED_ORDERS',
    value_format='json'
);

CREATE TABLE IF NOT EXISTS QUERYABLE_ORDERS_TABLE AS SELECT * FROM ORDERS_TABLE;