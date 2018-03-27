-- fpempn (decimal), fppunc (decimal), 
-- fptype (character), fpclck (character)
-- fpbadg (decimal), fpkey (character)
-- fppcod (decimal), fstatus (character)

create table krnwh (
	fpempn decimal(9,0),
	fppunc decimal(14,0),
	fptype varchar(1),
	fpclck varchar(15),
	fpbadg decimal(8,0),
	fpfkey varchar(15),
	fppcod decimal(15,3),
	fstatus varchar(1)
);
