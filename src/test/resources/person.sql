create table person(
	name VARCHAR(30) PRIMARY KEY,
	age INT
);

create table address(
	person VARCHAR(30),
    street VARCHAR(30)
);