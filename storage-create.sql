CREATE TABLE resources (Id BIGINT AUTO_INCREMENT NOT NULL, DTYPE VARCHAR(31), Alias VARCHAR(255), PRIMARY KEY (Id))
CREATE TABLE users (ResourceId BIGINT NOT NULL, Birth DATE, Confirmed TINYINT(1) default 0, Email VARCHAR(255) NOT NULL, Identity VARCHAR(255) NOT NULL, Name VARCHAR(255), Origin VARCHAR(255) NOT NULL, Phone VARCHAR(255), Profile LONGTEXT, Pwdhash VARCHAR(255), Random INTEGER, Registered DATETIME, SecondaryEmail VARCHAR(255), PRIMARY KEY (ResourceId))
CREATE UNIQUE INDEX INDEX_users_Origin_Identity_Email ON users (Origin, Identity, Email)
CREATE UNIQUE INDEX INDEX_users_Phone ON users (Phone)
CREATE TABLE users_footprints (ResourceId BIGINT NOT NULL, Registered DATETIME NOT NULL, Type INTEGER NOT NULL, UserId BIGINT NOT NULL, PRIMARY KEY (ResourceId))
CREATE TABLE grants (ResourceId BIGINT NOT NULL, Action VARCHAR(255), Pattern VARCHAR(255), UserId BIGINT NOT NULL, PRIMARY KEY (ResourceId))
CREATE UNIQUE INDEX INDEX_grants_Pattern_Action ON grants (Pattern, Action)
CREATE TABLE sessions (ResourceId BIGINT NOT NULL, Signature INTEGER, UserId BIGINT NOT NULL, PRIMARY KEY (ResourceId))
CREATE TABLE users_tokens (ResourceId BIGINT NOT NULL, Token INTEGER NOT NULL, Type INTEGER NOT NULL, UserId BIGINT NOT NULL, PRIMARY KEY (ResourceId))
CREATE TABLE subscriptions (ResourceId BIGINT NOT NULL, Published DATETIME, Email VARCHAR(255), PRIMARY KEY (ResourceId))
CREATE UNIQUE INDEX INDEX_subscriptions_Email ON subscriptions (Email)
CREATE TABLE aspects (RelateId BIGINT, Aspect VARCHAR(255))
ALTER TABLE users ADD CONSTRAINT FK_users_ResourceId FOREIGN KEY (ResourceId) REFERENCES resources (Id) ON DELETE CASCADE
ALTER TABLE users_footprints ADD CONSTRAINT FK_users_footprints_UserId FOREIGN KEY (UserId) REFERENCES resources (Id)
ALTER TABLE users_footprints ADD CONSTRAINT FK_users_footprints_ResourceId FOREIGN KEY (ResourceId) REFERENCES resources (Id) ON DELETE CASCADE
ALTER TABLE grants ADD CONSTRAINT FK_grants_ResourceId FOREIGN KEY (ResourceId) REFERENCES resources (Id) ON DELETE CASCADE
ALTER TABLE grants ADD CONSTRAINT FK_grants_UserId FOREIGN KEY (UserId) REFERENCES resources (Id) ON DELETE CASCADE
ALTER TABLE sessions ADD CONSTRAINT FK_sessions_UserId FOREIGN KEY (UserId) REFERENCES resources (Id) ON DELETE CASCADE
ALTER TABLE sessions ADD CONSTRAINT FK_sessions_ResourceId FOREIGN KEY (ResourceId) REFERENCES resources (Id) ON DELETE CASCADE
ALTER TABLE users_tokens ADD CONSTRAINT FK_users_tokens_ResourceId FOREIGN KEY (ResourceId) REFERENCES resources (Id) ON DELETE CASCADE
ALTER TABLE users_tokens ADD CONSTRAINT FK_users_tokens_UserId FOREIGN KEY (UserId) REFERENCES resources (Id)
ALTER TABLE subscriptions ADD CONSTRAINT FK_subscriptions_ResourceId FOREIGN KEY (ResourceId) REFERENCES resources (Id) ON DELETE CASCADE
ALTER TABLE aspects ADD CONSTRAINT FK_aspects_RelateId FOREIGN KEY (RelateId) REFERENCES resources (Id) ON DELETE CASCADE
