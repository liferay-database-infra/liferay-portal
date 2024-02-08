create index IX_5667FA64 on CommerceCurrency (companyId, active_, primary_);
create unique index IX_8F171977 on CommerceCurrency (companyId, code_[$COLUMN_LENGTH:75$]);
create index IX_212C856D on CommerceCurrency (uuid_[$COLUMN_LENGTH:75$]);