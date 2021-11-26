CREATE TABLE thumbnail_cache (
	filename	VARCHAR(256)	PRIMARY KEY,
	filesystem	VARCHAR(256) NOT NULL,
	file_timestamp TIMESTAMP,
	thumbnail_created TIMESTAMP NOT NULL,
	stream		BLOB NOT NULL
);
