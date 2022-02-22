CREATE DATABASE pizzle;

CREATE TABLE user (
 id bigint(20) NOT NULL AUTO_INCREMENT,
 username varchar(64) DEFAULT NULL,
 status int(5) NOT NULL,
 PRIMARY KEY (id),
 KEY UK_USERNAME (username) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE puzzle (
 id bigint(20) NOT NULL AUTO_INCREMENT,
 n int(5) NOT NULL,
 type int(5) NOT NULL,
 img_src varchar(64) NOT NULL,
 answer text NOT NULL,
 puzzle_g text NOT NULL,
 PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
