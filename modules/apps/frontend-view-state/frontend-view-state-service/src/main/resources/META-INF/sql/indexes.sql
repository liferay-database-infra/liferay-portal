create unique index IX_21A62EEF on FVSActiveEntry (userId, clayDataSetDisplayId[$COLUMN_LENGTH:75$], plid, portletId[$COLUMN_LENGTH:200$]);
create index IX_F04D9330 on FVSActiveEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_4089C4DB on FVSCustomEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C5FC4FCA on FVSEntry (uuid_[$COLUMN_LENGTH:75$]);