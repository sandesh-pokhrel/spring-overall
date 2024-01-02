drop table if exists Address;
drop table if exists Employee;
create table Employee (id int not null primary key auto_increment, name varchar(100), salary int);
create table Address (id int not null primary key auto_increment, city varchar(50), country varchar(50),
    employee_id int, constraint fk_address_emp foreign key (employee_id) references Employee (id));
