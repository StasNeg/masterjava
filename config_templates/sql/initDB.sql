DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS user_seq;
DROP TYPE IF EXISTS user_flag;
DROP TABLE IF EXISTS cities;
DROP SEQUENCE IF EXISTS city_seq;

DROP TABLE IF EXISTS groups;
DROP SEQUENCE IF EXISTS group_seq;
DROP TYPE IF EXISTS GROUP_TYPE;

DROP TABLE IF EXISTS projects;
DROP SEQUENCE IF EXISTS project_seq;


CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE TYPE GROUP_TYPE AS ENUM ('REGISTERING', 'CURRENT', 'FINISHED');

CREATE SEQUENCE user_seq START 100000;
CREATE SEQUENCE city_seq START 100000;
CREATE SEQUENCE group_seq START 100000;
CREATE SEQUENCE project_seq START 100000;

CREATE TABLE projects (
  id   INTEGER PRIMARY KEY DEFAULT nextval('project_seq'),
  name TEXT NOT NULL UNIQUE
);
CREATE TABLE groups (
  id      INTEGER PRIMARY KEY DEFAULT nextval('group_seq'),
  type    GROUP_TYPE NOT NULL,
  name    TEXT  nOT NULL UNIQUE ,
  project TEXT REFERENCES projects (name) ON DELETE CASCADE

);
CREATE UNIQUE INDEX groups_idx
  ON groups (name);

CREATE TABLE cities (
  id        INTEGER PRIMARY KEY DEFAULT nextval('city_seq'),
  full_name TEXT NOT NULL,
  cityId    TEXT NOT NULL
);
CREATE UNIQUE INDEX cities_idx
  ON cities (cityId);

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT      NOT NULL,
  email     TEXT      NOT NULL,
  flag      USER_FLAG NOT NULL,
  city      TEXT REFERENCES cities (cityId) ON DELETE CASCADE
);
CREATE UNIQUE INDEX email_idx ON users (email);




