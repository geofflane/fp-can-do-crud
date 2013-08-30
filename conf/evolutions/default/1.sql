# Contact schema

# --- !Ups

CREATE TABLE Contact (
    id bigint auto_increment,
    name varchar(100) NOT NULL,
    email varchar(40) NOT NULL,
    isFavorite boolean NOT NULL,
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Contact;

