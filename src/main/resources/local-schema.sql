-- ===============================================================
-- Category SQL scripts
-- Release 1.0
-- ===============================================================
-- Database Object Creation Script (Version 1.0)
-- ===============================================================

create database IF NOT EXISTS cat_database;

use cat_database;

DROP USER 'appuser'@'localhost';

create user IF NOT EXISTS 'appuser'@'localhost' identified by 'appuser2018';

grant all on cat_database.* to 'appuser'@'localhost'; -- Gives all the privileges to the new user on the newly created database

-- ---------------------------------------------------------------
-- Regression
-- ---------------------------------------------------------------

--drop table if exists category;

/*==============================================================*/
/* Table: category                                          */
/*==============================================================*/
/*
create table category (
   id                   BINARY(16)    NOT NULL,
   name                 VARCHAR(50)   NULL,
   slug                 VARCHAR(50)   NOT NULL,
   parent_category_id   BINARY(16)    NULL,
   is_visible           BOOLEAN       NOT NULL
   PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

*/