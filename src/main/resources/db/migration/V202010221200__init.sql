create table "user"(
  id bigint primary key,
  username text unique,
  password text,
  email text,
  phone_number text,
  created_at timestamp with time zone
);

create sequence user_pk_seq START WITH 1000000000000001 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
