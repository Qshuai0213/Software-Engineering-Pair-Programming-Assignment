-- 图书馆座位预约系统数据库初始化脚本
-- 说明：本脚本用于主分支底座初始化。执行前会删除旧表并重建基础表。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `reservation`;
DROP TABLE IF EXISTS `seat`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;

-- 用户表
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

-- 座位表
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

-- 预约表
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

-- 初始账号
INSERT INTO `user` (`username`, `password`, `role`) VALUES
('admin', 'admin123', 'admin'),
('test', 'test123', 'user');

-- 初始座位
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
