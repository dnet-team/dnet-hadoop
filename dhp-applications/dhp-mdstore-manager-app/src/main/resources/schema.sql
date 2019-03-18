CREATE TABLE mdstores (
	id              text PRIMARY KEY,
	format          text,
	layout          text,
	interpretation  text,
	datasource_id   text,
	api_id          text
);

CREATE TABLE transactions (
	id              text PRIMARY KEY,
	mdstore         text REFERENCES mdstores(id),
	active          boolean,
	lastupdate      timestamp,
	size            int
);
