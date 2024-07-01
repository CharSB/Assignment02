
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

CREATE TABLE IF NOT EXISTS 'movie_genre'(
	genre_id INTEGER,
  	movie_id INTEGER,
  	FOREIGN KEY(genre_id) REFERENCES genre(id),
  	FOREIGN KEY(movie_id) REFERENCES movie(id)
);

CREATE TABLE IF NOT EXISTS 'genre' (
    id INTEGER PRIMARY KEY,
    genre TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS 'movie_cast'(
	actor_id INTEGER,
  	movie_id INTEGER,
  	FOREIGN KEY(actor_id) REFERENCES actor(id),
  	FOREIGN KEY(movie_id) REFERENCES movie(id)
);
    
CREATE TABLE IF NOT EXISTS 'movie_award' (
  	id INTEGER PRIMARY KEY,
  	name TEXT,
  	movie_id INTEGER,
    year_awarded TEXT, 
	winner INTEGER,
  	FOREIGN KEY(movie_id) REFERENCES movie(id)
);
    
CREATE TABLE IF NOT EXISTS 'actor_award' (
  	id INTEGER PRIMARY KEY,
  	name TEXT,
  	actor_id INTEGER,
    year_awarded TEXT, 
	movie TEXT,
	winner INTEGER,
  	FOREIGN KEY(actor_id) REFERENCES actor(id)
);
    