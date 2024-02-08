create index IX_3D095957 on DDLRecord (className[$COLUMN_LENGTH:300$], classPK);
create index IX_6A6C1C85 on DDLRecord (companyId);
create index IX_AB8895BF on DDLRecord (recordSetId, recordSetVersion[$COLUMN_LENGTH:75$]);
create index IX_AAC564D3 on DDLRecord (recordSetId, userId);
create index IX_BE56BFC on DDLRecord (uuid_[$COLUMN_LENGTH:75$]);

create index IX_6705D180 on DDLRecordSet (DDMStructureId);
create unique index IX_98537AEA on DDLRecordSet (groupId, recordSetKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_D8371C54 on DDLRecordSet (uuid_[$COLUMN_LENGTH:75$]);

create index IX_1C4E1CC9 on DDLRecordSetVersion (recordSetId, status);
create unique index IX_A32E3C4E on DDLRecordSetVersion (recordSetId, version[$COLUMN_LENGTH:75$], ctCollectionId);

create index IX_762ADC7 on DDLRecordVersion (recordId, status);
create unique index IX_DCAB1210 on DDLRecordVersion (recordId, version[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_7B9EF675 on DDLRecordVersion (status, recordSetId, recordSetVersion[$COLUMN_LENGTH:75$], userId);