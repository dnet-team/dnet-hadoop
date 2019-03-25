DROP VIEW  IF EXISTS mdstores_with_info;
DROP TABLE IF EXISTS mdstore_current_versions;
DROP TABLE IF EXISTS mdstore_versions;
DROP TABLE IF EXISTS mdstores;

CREATE TABLE mdstores (
	id              text PRIMARY KEY,
	format          text,
	layout          text,
	interpretation  text,
	datasource_name text,
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
	md.datasource_name  AS datasource_name,
	md.datasource_id    AS datasource_id,
	md.api_id           AS api_id, 
	cv.current_version  AS current_version,
	v1.lastupdate       AS lastupdate,
	v1.size             AS size,
	count(v2.id)        AS n_versions
FROM
	mdstores md
	LEFT OUTER JOIN mdstore_current_versions cv ON (md.id = cv.mdstore)
	LEFT OUTER JOIN mdstore_versions v1 ON (cv.current_version = v1.id)
	LEFT OUTER JOIN mdstore_versions v2 ON (md.id = v2.mdstore)
GROUP BY md.id,
	md.format,
	md.layout,
	md.interpretation,
	md.datasource_name,
	md.datasource_id,
	md.api_id, 
	cv.current_version,
	v1.lastupdate,
	v1.size;