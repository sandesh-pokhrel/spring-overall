drop table if exists address;
drop table if exists employee;
drop table if exists video_game;
create table employee (id int not null primary key auto_increment, name varchar(100), salary int);
create table address (id int not null primary key auto_increment, city varchar(50), country varchar(50),
    employee_id int, constraint fk_address_emp foreign key (employee_id) references Employee (id));
create table video_game (id int not null primary key, name varchar(400), platform varchar(400), year int, genre varchar(400),
    publisher varchar(400), na_sales float, eu_sales float, jp_sales float, other_sales float, global_sales float);
