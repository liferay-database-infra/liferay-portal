create unique index IX_C2E33E86 on CTermEntryLocalization (commerceTermEntryId, languageId[$COLUMN_LENGTH:75$]);

create index IX_E73B0D12 on CommerceTermEntry (companyId, active_);
create unique index IX_3E1C7541 on CommerceTermEntry (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_D6266820 on CommerceTermEntry (companyId, type_[$COLUMN_LENGTH:75$], active_);
create unique index IX_AF1EE561 on CommerceTermEntry (companyId, type_[$COLUMN_LENGTH:75$], priority);
create index IX_33A29657 on CommerceTermEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_25217F89 on CommerceTermEntry (status, displayDate);
create index IX_1E15CC8 on CommerceTermEntry (status, expirationDate);
create index IX_F9122F4E on CommerceTermEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_2AA8B117 on CommerceTermEntryRel (commerceTermEntryId, classNameId, classPK);