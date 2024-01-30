create index IX_48CB043 on AccountEntry (companyId, status);
create index IX_79B958DD on AccountEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_52A4F1FB on AccountEntry (userId, type_[$COLUMN_LENGTH:75$]);
create index IX_DD1ABDD4 on AccountEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_B233281D on AccountEntryOrganizationRel (organizationId, accountEntryId);

create index IX_EED84268 on AccountEntryUserRel (accountUserId, accountEntryId);

create index IX_38BDB33 on AccountGroup (companyId, defaultAccountGroup);
create index IX_EB8A19A on AccountGroup (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_A56C7FD0 on AccountGroup (companyId, type_[$COLUMN_LENGTH:75$]);
create index IX_15785BF0 on AccountGroup (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_DE7046E7 on AccountGroup (uuid_[$COLUMN_LENGTH:75$]);

create index IX_617C42A3 on AccountGroupRel (classNameId, classPK, accountGroupId);

create index IX_9BCBCB2B on AccountRole (accountEntryId, companyId);
create index IX_714A358E on AccountRole (roleId);