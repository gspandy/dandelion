CREATE TABLE IF NOT EXISTS MyUser (
  userId      VARCHAR(22) NOT NULL,
  name        VARCHAR(64)    DEFAULT NULL,
  boolValue   BOOLEAN        DEFAULT NULL,
  dateValue   DATETIME       DEFAULT NULL,
  bigDecimal  DECIMAL(32, 0) DEFAULT NULL,
  intValue    INT(11)        DEFAULT NULL,
  longValue   BIGINT         DEFAULT NULL,
  floatValue  REAL           DEFAULT NULL,
  doubleValue DOUBLE         DEFAULT NULL,
  shortValue  SMALLINT       DEFAULT NULL,
  bytesValue  BINARY(255)    DEFAULT NULL,
  PRIMARY KEY (userId)
);