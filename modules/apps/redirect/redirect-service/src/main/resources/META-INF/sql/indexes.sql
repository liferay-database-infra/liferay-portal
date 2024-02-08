create index IX_193688C on RedirectEntry (groupId, destinationURL[$COLUMN_LENGTH:4000$]);
create unique index IX_A48581BF on RedirectEntry (groupId, sourceURL[$COLUMN_LENGTH:4000$]);
create index IX_F9F2BC03 on RedirectEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_22DAEEB on RedirectNotFoundEntry (groupId, url[$COLUMN_LENGTH:4000$]);