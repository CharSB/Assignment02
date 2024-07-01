
DROP TABLE IF EXISTS movie;
DROP TABLE IF EXISTS actor;
DROP TABLE IF EXISTS movie_cast;
DROP TABLE IF EXISTS movie_award;
DROP TABLE IF EXISTS actor_award;

CREATE TABLE IF NOT EXISTS 'movie' ( 
  	id INTEGER PRIMARY KEY, 
  	title TEXT, 
  	release_year INTEGEREGER, 
  	runtime INTEGEREGER, 
  	director TEXT,
	plot TEXT,
  	rating REAL
);
    
CREATE TABLE IF NOT EXISTS 'actor' (
    id INTEGER PRIMARY KEY,
    first_name TEXT,
    last_name TEXT,
    dob INTEGER 
);