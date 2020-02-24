drop table if exists customers;
CREATE TABLE customers(
   id   INT              NOT NULL AUTO_INCREMENT,
   name   varchar (255),
   PRIMARY KEY (id)
);

drop table if exists accounts;
CREATE TABLE accounts(
   id   INT              NOT NULL AUTO_INCREMENT,
   balance   DECIMAL (18, 2),
   customer_id int,
   PRIMARY KEY (id),
   FOREIGN KEY (customer_id) REFERENCES customers(id)
);

insert into customers (id, name) values (1, 'tugrul aslan');
insert into accounts (id, balance, customer_id) values (1, 2500, 1);

insert into customers (id, name) values (2, 'ramazan girgin');
insert into accounts (id, balance, customer_id) values (2, 4500, 2);