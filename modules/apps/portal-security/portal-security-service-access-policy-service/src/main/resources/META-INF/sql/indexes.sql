create index IX_6D669D6F on SAPEntry (companyId, defaultSAPEntry);
create index IX_1428367C on SAPEntry (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_7C82E49 on SAPEntry (uuid_[$COLUMN_LENGTH:75$]);