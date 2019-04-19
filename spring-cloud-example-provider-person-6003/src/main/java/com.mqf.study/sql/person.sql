-- 不存在就执行
CREATE DATABASE cloudBD03;


DROP TABLE IF EXISTS person;

CREATE TABLE person
(
	id BIGINT(20) NOT NULL COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	dbSource VARCHAR(50),
	PRIMARY KEY (id)
);

DELETE FROM person;

INSERT INTO person (id, name, age, email,dbSource) VALUES
(1, 'Jone', 18, 'test1@163.com',DATABASE()),
(2, 'Jack', 20, 'test2@163.com',DATABASE()),
(3, 'Tom', 28, 'test3@163.com',DATABASE()),
(4, 'Sandy', 21, 'test4@163.com',DATABASE()),
(5, 'Billie', 24, 'test5@163.com',DATABASE());