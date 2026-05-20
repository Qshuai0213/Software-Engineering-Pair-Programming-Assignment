图书馆座位预约系统

系统部署说明

| 学号 | 姓名 |
| --- | --- |
| 233417030323 | 曲佳明 |
| 233401010401 | 梁奕佳 |
| 项目地址 | https://github.com/Qshuai0213/Software-Engineering-Pair-Programming-Assignment.git |

提示：此文档直接作为你们Git仓库根目录的 README.md

技术栈声明

后端：Java 17 + Spring Boot 3 + MyBatis + MySQL + Redis + JWT

前端： Vue 3 + Vite + Vue Router 4 + Pinia + Axios + Element Plus

本地部署步骤（可以参照下面格式写，也可根据自己项目类型撰写部署步骤）

数据库初始化

座位项目使用 MySQL 数据库：

agentforge

访问数据库用户名：agentforge

访问数据库密码：1234

目前全部都是读取环境变量，而非硬编码，所以需要先配置环境变量

当前业务表：

1. user         用户表

2. seat         座位表

3. reservation 预约表

服务器当前数据量：

user:        2 条

seat:        10 条

reservation: 0 条

建库语句：

CREATE DATABASE `agentforge`

DEFAULT CHARACTER SET utf8mb4

COLLATE utf8mb4_general_ci;

USE `agentforge`;

建表语句

SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `reservation`;

DROP TABLE IF EXISTS `seat`;

DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (

`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',

`username` VARCHAR(50) NOT NULL COMMENT '用户名',

`password` VARCHAR(100) NOT NULL COMMENT '密码，课程项目阶段可先使用明文，后续可替换为加密存储',

`role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：user/admin',

`status` VARCHAR(20) NOT NULL DEFAULT 'normal' COMMENT '状态：normal/blocked',

`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

PRIMARY KEY (`id`),

UNIQUE KEY `uk_user_username` (`username`),

KEY `idx_user_role` (`role`),

KEY `idx_user_status` (`status`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `seat` (

`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '座位ID',

`seat_no` VARCHAR(20) NOT NULL COMMENT '座位编号，例如 A01',

`location` VARCHAR(50) NOT NULL COMMENT '座位位置，例如 图书馆三楼',

`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

PRIMARY KEY (`id`),

UNIQUE KEY `uk_seat_no` (`seat_no`),

KEY `idx_seat_location` (`location`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='座位表';

CREATE TABLE `reservation` (

`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',

`user_id` BIGINT NOT NULL COMMENT '用户ID',

`seat_id` BIGINT NOT NULL COMMENT '座位ID',

`start_time` DATETIME NOT NULL COMMENT '预约开始时间',

`end_time` DATETIME NOT NULL COMMENT '预约结束时间',

`status` VARCHAR(20) NOT NULL DEFAULT 'reserved' COMMENT '状态：reserved/checked_in/cancelled/expired',

`check_in_time` DATETIME NULL COMMENT '签到时间',

`cancel_time` DATETIME NULL COMMENT '取消时间',

`create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

`update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

PRIMARY KEY (`id`),

KEY `idx_reservation_user_id` (`user_id`),

KEY `idx_reservation_seat_id` (`seat_id`),

KEY `idx_reservation_status` (`status`),

KEY `idx_reservation_time_range` (`seat_id`, `start_time`, `end_time`),

KEY `idx_reservation_user_active` (`user_id`, `status`, `end_time`),

CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),

CONSTRAINT `fk_reservation_seat` FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

初始化插入数据

INSERT INTO `user` (`username`, `password`, `role`) VALUES

('admin', 'admin123', 'admin'),

('test', 'test123', 'user');

INSERT INTO `seat` (`seat_no`, `location`) VALUES

('A01', '图书馆三楼'),

('A02', '图书馆三楼'),

('A03', '图书馆三楼'),

('A04', '图书馆三楼'),

('A05', '图书馆三楼'),

('B01', '图书馆三楼'),

('B02', '图书馆三楼'),

('B03', '图书馆三楼'),

('B04', '图书馆三楼'),

('B05', '图书馆三楼');

Redis启动

如果本地 Windows 已安装 Redis，并且 redis-server 在 PATH 中：

redis-server

如果本地 Redis 有配置文件：

redis-server redis.conf

后端默认读取 Redis 配置：

REDIS_HOST=localhost

REDIS_PORT=6379

Redis database=1

2. 后端启动

后端默认端口：8080

后端默认数据库配置来自 application.yml：

DB_NAME 默认 agentforge

DB_USERNAME 默认 root

DB_PASSWORD 默认空

REDIS_HOST 默认 localhost

REDIS_PORT 默认 6379

PowerShell 启动后端：

cd "D:\课程学习\软件工程\结对作业\人人作业\code"

$env:DB_NAME="agentforge"

$env:DB_USERNAME="root"

$env:DB_PASSWORD="你的MySQL密码"

$env:REDIS_HOST="localhost"

$env:REDIS_PORT="6379"

mvn spring-boot:run

如果本地 8080 被占用，可以临时换端口：

cd “下载后的项目路径”

$env:DB_NAME="agentforge"

$env:DB_USERNAME="root"

$env:DB_PASSWORD="你的MySQL密码"

$env:REDIS_HOST="localhost"

$env:REDIS_PORT="6379"

mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8082"

如果已经打包成 jar，也可以这样启动：

cd "下载后的项目路径"

mvn clean package -DskipTests

java -jar target\seat-reservation-1.0.0.jar --server.port=8080

3. 前端启动

前端是 Vue + Vite 项目，首次启动先安装依赖：

cd "下载后的项目路径"

npm install

启动开发服务器：

cd "下载后的项目路径"

npm run dev

构建生产静态文件：

cd "下载后的项目路径"

npm run build

4.测试账号

管理员：

账号：admin

密码：admin123

普通学生：

账号：test

密码：test123

PS：目前项目已经部署，可以直接通过 http://49.232.147.154/ 访问
