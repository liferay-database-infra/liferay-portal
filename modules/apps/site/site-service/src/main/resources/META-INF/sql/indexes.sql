create unique index IX_726E939A on SiteFriendlyURL (companyId, friendlyURL[$COLUMN_LENGTH:75$]);
create unique index IX_C2B01217 on SiteFriendlyURL (companyId, groupId, languageId[$COLUMN_LENGTH:75$]);
create index IX_AF7BCC5C on SiteFriendlyURL (uuid_[$COLUMN_LENGTH:75$]);