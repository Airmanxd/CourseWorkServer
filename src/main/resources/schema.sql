CREATE TABLE IF NOT EXISTS users (
id int NOT NULL PRIMARY KEY,
email varchar NOT NULL,
username varchar(30) NOT NULL,
password varchar NOT NULL,
enabled boolean NOT NULL DEFAULT FALSE,
role varchar
);

CREATE TABLE IF NOT EXISTS videos (
videoId int NOT NULL PRIMARY KEY,
name varchar(30) NOT NULL,
category varchar(20) NOT NULL,
likes bigint NOT NULL,
source VARCHAR
);

CREATE TABLE IF NOT EXISTS user_channel
(
    user_id INT NOT NULL,
    video_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (video_id) REFERENCES videos (video_id),
    UNIQUE (user_id, video_id)
);

CREATE TABLE IF NOT EXISTS liked_videos
(
    user_id INT NOT NULL,
    video_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (video_id) REFERENCES videos (video_id),
    UNIQUE (user_id, video_id)
);



