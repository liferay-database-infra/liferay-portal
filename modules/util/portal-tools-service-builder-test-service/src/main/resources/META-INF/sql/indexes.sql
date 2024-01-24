create index IX_838D8DFC on BigDecimalEntries_LVEntries (companyId);
create index IX_67100507 on BigDecimalEntries_LVEntries (lvEntryId);

create index IX_867C5A9 on BigDecimalEntry (bigDecimalValue);

create unique index IX_2D99FD84 on CacheDisabledEntry (name[$COLUMN_LENGTH:75$]);

create index IX_4F11FECA on CacheFieldEntry (groupId);

create index IX_AF444AB7 on ERCCompanyEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_A868A3AE on ERCCompanyEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_7A2392B9 on ERCGroupEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_928C2FB0 on ERCGroupEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_36BD3E9C on EagerBlobEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_D3E89AB2 on FinderWhereClauseEntry (name[$COLUMN_LENGTH:75$]);

create unique index IX_6E260760 on LVEntry (groupId, head, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create unique index IX_50CAD09D on LVEntry (headId);
create index IX_102A8611 on LVEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_FC1C4C16 on LVEntryLocalization (headId);
create unique index IX_6D6DEA3E on LVEntryLocalization (lvEntryId, languageId[$COLUMN_LENGTH:75$]);

create index IX_584B3E8 on LVEntryLocalizationVersion (lvEntryId, languageId[$COLUMN_LENGTH:75$]);
create unique index IX_43435844 on LVEntryLocalizationVersion (lvEntryId, version, languageId[$COLUMN_LENGTH:75$]);
create index IX_142D1FEF on LVEntryLocalizationVersion (lvEntryLocalizationId);
create unique index IX_2EDFD541 on LVEntryLocalizationVersion (version, lvEntryLocalizationId);

create index IX_CF81EDE4 on LVEntryVersion (groupId, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create unique index IX_CCC43084 on LVEntryVersion (groupId, version, uniqueGroupKey[$COLUMN_LENGTH:75$]);
create index IX_1287D6FD on LVEntryVersion (lvEntryId);
create index IX_4BEC3569 on LVEntryVersion (uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_47B1B7A1 on LVEntryVersion (version, lvEntryId);

create index IX_29878C18 on LazyBlobEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_356ADEAE on LocalizedEntryLocalization (localizedEntryId, languageId[$COLUMN_LENGTH:75$]);

create unique index IX_94217124 on NullConvertibleEntry (name[$COLUMN_LENGTH:75$]);

create unique index IX_6F141E3F on RedundantIndexEntry (companyId, name[$COLUMN_LENGTH:75$]);

create index IX_D9CA14EC on RenameFinderColumnEntry (columnToRename[$COLUMN_LENGTH:75$]);

create index IX_6770C47D on VersionedEntry (groupId, head);
create unique index IX_AAA6F330 on VersionedEntry (headId);

create index IX_B20BEA65 on VersionedEntryVersion (version, groupId);
create unique index IX_3129EDCF on VersionedEntryVersion (version, versionedEntryId);