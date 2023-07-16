
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL COMMENT '用户编号',
  `order_status` tinyint(4) NOT NULL COMMENT '0：待支付 1 ：支付成功',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for t_points
-- ----------------------------
DROP TABLE IF EXISTS `t_points`;
CREATE TABLE `t_points` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `order_id` bigint(20) NOT NULL COMMENT '订单编号',
  `points` int(4) NOT NULL COMMENT '积分',
  `remarks` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '备注',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_order_Id` (`order_id`) USING BTREE COMMENT '订单唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for t_transaction_log
-- ----------------------------
DROP TABLE IF EXISTS `t_transaction_log`;
CREATE TABLE `t_transaction_log` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '事务ID',
  `biz_id` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '对应业务表中的业务编号',
  `biz_type` tinyint(4) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

SET FOREIGN_KEY_CHECKS = 1;