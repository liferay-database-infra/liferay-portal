create index IX_E7DE5F9C on ClientExtensionEntry (companyId, type_[$COLUMN_LENGTH:75$]);
create index IX_96767BA4 on ClientExtensionEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_7AEC4E9B on ClientExtensionEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_3DA7496A on ClientExtensionEntryRel (classNameId, classPK, type_[$COLUMN_LENGTH:75$]);
create index IX_37B55201 on ClientExtensionEntryRel (companyId, cetExternalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_5FBFAB87 on ClientExtensionEntryRel (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_9CDFA47E on ClientExtensionEntryRel (uuid_[$COLUMN_LENGTH:75$]);