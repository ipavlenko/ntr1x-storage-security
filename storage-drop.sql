ALTER TABLE users DROP FOREIGN KEY FK_users_ResourceId
ALTER TABLE users_footprints DROP FOREIGN KEY FK_users_footprints_UserId
ALTER TABLE users_footprints DROP FOREIGN KEY FK_users_footprints_ResourceId
ALTER TABLE grants DROP FOREIGN KEY FK_grants_ResourceId
ALTER TABLE grants DROP FOREIGN KEY FK_grants_UserId
ALTER TABLE sessions DROP FOREIGN KEY FK_sessions_UserId
ALTER TABLE sessions DROP FOREIGN KEY FK_sessions_ResourceId
ALTER TABLE users_tokens DROP FOREIGN KEY FK_users_tokens_ResourceId
ALTER TABLE users_tokens DROP FOREIGN KEY FK_users_tokens_UserId
ALTER TABLE subscriptions DROP FOREIGN KEY FK_subscriptions_ResourceId
ALTER TABLE aspects DROP FOREIGN KEY FK_aspects_RelateId
DROP TABLE resources
DROP INDEX INDEX_users_Origin_Identity_Email ON users
DROP INDEX INDEX_users_Phone ON users
DROP TABLE users
DROP TABLE users_footprints
DROP INDEX INDEX_grants_Pattern_Action ON grants
DROP TABLE grants
DROP TABLE sessions
DROP TABLE users_tokens
DROP INDEX INDEX_subscriptions_Email ON subscriptions
DROP TABLE subscriptions
DROP TABLE aspects
