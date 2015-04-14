create table person(
	name VARCHAR(30),
	age INT
);

create table address(
	person VARCHAR(30),
	/* test */
	street VARCHAR(30)
);

// data
insert into person(name,age) values('fredde',44);
insert into person(name,age) values('fredde;kivi',44);