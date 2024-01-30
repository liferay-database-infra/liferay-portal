create index IX_F3DC928B on FriendlyURLEntry (groupId, classNameId, classPK);
create index IX_94E0FAAB on FriendlyURLEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_BFA6E36A on FriendlyURLEntryLocalization (friendlyURLEntryId);
create index IX_8E09D780 on FriendlyURLEntryLocalization (groupId, classNameId, languageId[$COLUMN_LENGTH:75$], classPK);
create unique index IX_7A500459 on FriendlyURLEntryLocalization (groupId, classNameId, languageId[$COLUMN_LENGTH:75$], urlTitle[$COLUMN_LENGTH:255$], ctCollectionId);
create index IX_74392ED on FriendlyURLEntryLocalization (groupId, classNameId, urlTitle[$COLUMN_LENGTH:255$]);
create unique index IX_AD5EBD7A on FriendlyURLEntryLocalization (languageId[$COLUMN_LENGTH:75$], friendlyURLEntryId, ctCollectionId);

create unique index IX_5BE324B9 on FriendlyURLEntryMapping (classNameId, classPK, ctCollectionId);