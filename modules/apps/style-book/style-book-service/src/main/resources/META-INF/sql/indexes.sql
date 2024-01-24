create unique index IX_EC8C186B on StyleBookEntry (ctCollectionId, headId);
create index IX_957FE3BD on StyleBookEntry (groupId, defaultStyleBookEntry);
create index IX_9EFBE469 on StyleBookEntry (groupId, head, defaultStyleBookEntry);
create index IX_D3F1D92A on StyleBookEntry (groupId, head, name[$COLUMN_LENGTH:75$]);
create unique index IX_7B5B00A0 on StyleBookEntry (groupId, head, styleBookEntryKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_1E9FA956 on StyleBookEntry (groupId, name[$COLUMN_LENGTH:75$]);
create index IX_EA23C596 on StyleBookEntry (groupId, styleBookEntryKey[$COLUMN_LENGTH:75$]);
create index IX_357284A1 on StyleBookEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_E9CC1685 on StyleBookEntryVersion (groupId, defaultStyleBookEntry);
create index IX_EDE9218E on StyleBookEntryVersion (groupId, name[$COLUMN_LENGTH:75$]);
create index IX_5881685E on StyleBookEntryVersion (groupId, styleBookEntryKey[$COLUMN_LENGTH:75$]);
create index IX_D77F24C5 on StyleBookEntryVersion (groupId, version, defaultStyleBookEntry);
create index IX_45D06B4E on StyleBookEntryVersion (groupId, version, name[$COLUMN_LENGTH:75$]);
create unique index IX_1065D0FC on StyleBookEntryVersion (groupId, version, styleBookEntryKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_6C02234D on StyleBookEntryVersion (styleBookEntryId);
create index IX_D1F3B4D9 on StyleBookEntryVersion (uuid_[$COLUMN_LENGTH:75$]);
create unique index IX_2033C367 on StyleBookEntryVersion (version, styleBookEntryId, ctCollectionId);