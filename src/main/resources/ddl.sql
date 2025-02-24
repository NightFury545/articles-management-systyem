DROP TABLE IF EXISTS articles;
CREATE TABLE IF NOT EXISTS articles
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    content    TEXT NOT NULL,
    image_url  TEXT,
    created_at DATE DEFAULT (datetime('now')),
    updated_at DATE DEFAULT (datetime('now'))
);

DROP TABLE IF EXISTS comments;
CREATE TABLE IF NOT EXISTS comments
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    content    TEXT NOT NULL,
    article_id INTEGER,
    created_at DATE DEFAULT (datetime('now')),
    updated_at DATE DEFAULT (datetime('now')),
    FOREIGN KEY (article_id) REFERENCES articles (id)
);