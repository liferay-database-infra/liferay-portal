create index IX_70682877 on ListTypeDefinition (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_4280016E on ListTypeDefinition (uuid_[$COLUMN_LENGTH:75$]);

create index IX_DBCB5728 on ListTypeEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_B9B39619 on ListTypeEntry (listTypeDefinitionId, key_[$COLUMN_LENGTH:75$]);
create index IX_BFD0B21F on ListTypeEntry (uuid_[$COLUMN_LENGTH:75$]);