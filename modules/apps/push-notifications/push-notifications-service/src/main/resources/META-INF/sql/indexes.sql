create unique index IX_BA853868 on PushNotificationsDevice (token[$COLUMN_LENGTH:4000$]);
create index IX_589A08D6 on PushNotificationsDevice (userId, platform[$COLUMN_LENGTH:75$]);