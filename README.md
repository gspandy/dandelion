## Dandelion（浦公英） - 灵活轻量的数据库访问工具

##### Spring Jdbc具有强大的参数解析、简化执行过程、返回值封装等特性，加上所有执行过程都调试可达，修改即时生效，使得开发和维护具有极高的效率。
##### 该工具作为Spring jdbc的增强，以原生SQL为核心，自动生成对象操作的SQL，简化分页和查询，同时可使用其中的JdbcTemplate，无后顾之忧。

##### 1、对象基本CRUD操作，可轻松选取或屏蔽部分属性，支持定义临时属性，支持批量操作。
##### 2、支持生成全局唯一ID，支持多ID组成的联合主键，手动赋值和自动生成两种方式可选。
##### 3、该工具使用原生SQL，可使用SQL生成器生成主体SQL，少量编码即可实现自定义查询。
##### 4、使用LIMIT OFFSET分页语法，支持MySql、PostgreSQL和H2等数据库，其他数据库须简单修改。
##### 5、清晰有序的方法命名、完善的文档注释、使用原生SQL，极低的学习成本。

-----

## 该工具遵循以下约定

##### 1、积极的属性：非null的对象类型（包括包装类型），基本类型大于0或为true的值，反之则为消极的属性，系统默认初始化的值都是消极属性。
##### 2、积极或消极的属性可以作为属性名单配置，用于选取或屏蔽对象的部分属性，故不建议在实体类中使用默认值，也不建议使用基本类型。
##### 3、实体属性名默认与数据库表的列名相同，如需使用下划线风格，请将SqlGenerator.UNDERSCORE修改为true，如使用MySql不建议修改。
##### 4、主键ID为20位36进制数组成的字符串，多机器部署、单机多实例都ID不重复、保持递增趋势、尾数分布均匀，可分库分表可移植数据库。
##### 5、除分页查询使用LIMIT语句外，其他语句均为标准SQL语句，理论上支持所有关系型数据库。

-----

## 相关依赖及使用说明

##### 相关依赖：Spring framework、Spring jdbc，4.0.0或以上的版本，日志扩展slf4j，可自行选择日志实现包。
#### 三种方式引入到项目：
##### 1、直接作为源码引入，进行二次定制化开发。
##### 2、使用Maven打成Jar包引入（mvn package），也可以直接下载 Jar 包。
##### 3、以Maven依赖方式引入（先执行mvn install，也可以直接 install Jar 包）。
#### 轻松在项目中使用：
#####  Spring Boot 项目：添加 spring-boot-starter-jdbc 依赖，参考或者复制 src/test/java 下的 com.ewing.boot.BootDaoConfig 类到项目中，并使Spring能扫描到该配置类。
##### 普通 Spring 项目：先配置一个数据源 DataSource，参考或者复制 src/test/java 下的 com.ewing.normal.NormalDaoConfig 类到项目中，并使Spring能扫描到该配置类。
##### 原理：该工具需要配置JdbcOperations和NamedParameterJdbcOperations的实现，即JdbcTemplate和NamedParameterJdbcTemplate（这两个类依赖DataSource）。

-----

## 接口及实现类包说明

#### 源码有详细注释，可生成Java文档，使用案例可参考测试用例中的UserDaoTests、UserDaoImpl和CommonDaoTest类。

##### GenericDao接口及实现类GenericBaseDao，普通DAO类通过继承该接口，可以让该DAO类具有特定实体专用的CRUD方法。
##### CommonDao接口及实现类CommonBaseDao，该类可以通过传递class参数来对任意实体对象进行操作，不需要单独继承泛型接口。
##### SqlGenerator可以生成任意实体的查询SQL语句主体，可自由使用原生SQL追加条件和参数，灵活度非常高。
##### annotation包中定义注解。Identity可以标记属性为ID，参数generate表示是否生成ID值，默认为否，支持多个ID（联合主键）。
##### Temporary注解标记的属性在生成Sql语句时被忽略，成为临时属性，但不影响Spring Jdbc使用该属性，常用于附加关联数据。
##### SqlName注解可以自定义对象类型在Sql中的名称，即数据库的表名，通常用于带前缀的表名。

##### 注1：可配合Spring Cache使用，使用注解声明式的本地缓存或Redis共享缓存，可很好地降低数据库访问次数。
##### 注2：可使用Maven的MyBatisGenerator插件从数据库生成对象模型，插件<overwrite>配置设为false可避免覆盖。

------

## 相关测试结果（因机器而异）

#### Sql生成器使用了缓存机制，效率大幅提升。轻松通过了10000个线程，每个线程执行1000次的高并发压力测试，理论上限在于机器。

##### 1、Sql生成器测试，实体属性13个，使用代码覆盖最多的方法进行测试，各执行100万次，插入语句总计约1.5秒，查询、更新、删除均在1秒以内，耗时几乎可以忽略。
##### 2、全局ID生成效率，同时使用1000个线程、每个线程获取1000个ID，一共100万个，耗时约1.5秒，100万线程也通过了测试，能满足任意单个节点新增数据的需求。
