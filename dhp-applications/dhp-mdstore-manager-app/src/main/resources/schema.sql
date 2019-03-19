DROP VIEW  IF EXISTS mdstores_with_info;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS mdstores;

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
	current         boolean,
	active          boolean,
	readcount       int,
	lastupdate      timestamp,
	size            int
);

CREATE VIEW mdstores_with_info AS SELECT
	md.id              AS id,
	md.format          AS format,
	md.layout          AS layout,
	md.interpretation  AS interpretation,
	md.datasource_id   AS datasource_id,
	md.api_id          AS api_id, 
	t.id               AS current_version,
	t.lastupdate       AS lastupdate,
	t.size             AS size
FROM
	mdstores md
	LEFT OUTER JOIN transactions t ON (md.id = t.mdstore)
WHERE
	t.current = TRUE;
