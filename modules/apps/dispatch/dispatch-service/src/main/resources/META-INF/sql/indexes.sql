create index IX_36F4EB5F on DispatchLog (dispatchTriggerId, status);

create index IX_71D6AFE9 on DispatchTrigger (active_, dispatchTaskClusterMode);
create index IX_3E76C5EF on DispatchTrigger (companyId, dispatchTaskExecutorType[$COLUMN_LENGTH:75$]);
create unique index IX_F94D24CE on DispatchTrigger (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_F6ABBDDE on DispatchTrigger (companyId, userId);
create index IX_5022A824 on DispatchTrigger (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_77817B1B on DispatchTrigger (uuid_[$COLUMN_LENGTH:75$]);