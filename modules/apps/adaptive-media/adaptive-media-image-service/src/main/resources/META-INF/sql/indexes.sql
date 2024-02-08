create index IX_6DDD0C5F on AMImageEntry (companyId);
create index IX_D3F151E5 on AMImageEntry (configurationUuid[$COLUMN_LENGTH:75$], companyId);
create unique index IX_D1C9B282 on AMImageEntry (configurationUuid[$COLUMN_LENGTH:75$], fileVersionId, ctCollectionId);
create index IX_E879919E on AMImageEntry (fileVersionId);
create index IX_65AB1EA1 on AMImageEntry (groupId);
create index IX_763F67D6 on AMImageEntry (uuid_[$COLUMN_LENGTH:75$]);