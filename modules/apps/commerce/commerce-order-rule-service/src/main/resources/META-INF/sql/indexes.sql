create index IX_A04ABE3F on COREntry (companyId, type_[$COLUMN_LENGTH:75$], active_);
create index IX_E6CD3F6 on COREntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_8599BE68 on COREntry (status, displayDate);
create index IX_4AFDBD89 on COREntry (status, expirationDate);
create index IX_F6718AED on COREntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_EA6EFFC3 on COREntryRel (COREntryId, classNameId, classPK);