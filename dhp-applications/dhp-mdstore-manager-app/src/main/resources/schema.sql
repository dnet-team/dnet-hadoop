DROP VIEW  IF EXISTS mdstores_with_info;
DROP TABLE IF EXISTS mdstore_current_versions;
DROP TABLE IF EXISTS mdstore_versions;
DROP TABLE IF EXISTS mdstores;

CREATE TABLE mdstores (
	id              text PRIMARY KEY,
	format          text,
	layout          text,
	interpretation  text,
	datasource_id   text,
	api_id          text
);

CREATE TABLE mdstore_versions (
	id              text PRIMARY KEY,
	mdstore         text REFERENCES mdstores(id),
	writing         boolean,
	readcount       int,
	lastupdate      timestamp,
	size            bigint
);

CREATE TABLE mdstore_current_versions (
	mdstore         text PRIMARY KEY REFERENCES mdstores(id),
	current_version text REFERENCES mdstore_versions(id)
);

CREATE VIEW mdstores_with_info AS SELECT
	md.id               AS id,
	md.format           AS format,
	md.layout           AS layout,
	md.interpretation   AS interpretation,
	md.datasource_id    AS datasource_id,
	md.api_id           AS api_id, 
	cv.current_version  AS current_version,
	v.lastupdate        AS lastupdate,
	v.size              AS size
FROM
	mdstores md
	LEFT OUTER JOIN mdstore_current_versions cv ON (md.id = cv.mdstore)
	LEFT OUTER JOIN mdstore_versions v ON (cv.current_version = v.id);
