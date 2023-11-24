create index IX_D0050DF7 on SavedContentEntry (classNameId, classPK, companyId);
create unique index IX_4715B10F on SavedContentEntry (classNameId, classPK, userId, companyId, ctCollectionId);
create unique index IX_CFA82491 on SavedContentEntry (classNameId, classPK, userId, groupId, ctCollectionId);
create index IX_26BC5C5E on SavedContentEntry (userId, groupId);
create index IX_AAA0B3AE on SavedContentEntry (uuid_[$COLUMN_LENGTH:75$]);