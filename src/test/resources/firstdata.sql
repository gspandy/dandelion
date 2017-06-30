/*初始化用户表*/
CREATE TABLE IF NOT EXISTS MyUser (
  userId      VARCHAR(22) NOT NULL,
  name        VARCHAR(64)    DEFAULT NULL,
  level       INT(11)        DEFAULT NULL,
  description VARCHAR(64)    DEFAULT NULL,
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

/*下划线风格测试*/
CREATE TABLE IF NOT EXISTS My_User (
  user_id      VARCHAR(22) NOT NULL,
  name         VARCHAR(64)    DEFAULT NULL,
  level        INT(11)        DEFAULT NULL,
  description  VARCHAR(64)    DEFAULT NULL,
  bool_value   BOOLEAN        DEFAULT NULL,
  date_value   DATETIME       DEFAULT NULL,
  big_decimal  DECIMAL(32, 0) DEFAULT NULL,
  int_value    INT(11)        DEFAULT NULL,
  long_value   BIGINT         DEFAULT NULL,
  float_value  REAL           DEFAULT NULL,
  double_value DOUBLE         DEFAULT NULL,
  short_value  SMALLINT       DEFAULT NULL,
  bytes_value  BINARY(255)    DEFAULT NULL,
  PRIMARY KEY (user_id)
);

/*包含多ID的团队表*/
CREATE TABLE IF NOT EXISTS Team (
  myId        VARCHAR(22) NOT NULL,
  yourId      VARCHAR(22) NOT NULL,
  hisId       VARCHAR(22) NOT NULL,
  name        VARCHAR(64) DEFAULT NULL,
  description VARCHAR(64) DEFAULT NULL,
  createTime  DATETIME    DEFAULT NULL,
  PRIMARY KEY (myId, yourId, hisId)
);
