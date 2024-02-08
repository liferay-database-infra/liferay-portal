create index IX_65CA6CC9 on KaleoProcess (DDLRecordSetId);
create index IX_A29A06D5 on KaleoProcess (groupId);
create index IX_C2E02B0A on KaleoProcess (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_F94547BB on KaleoProcessLink (kaleoProcessId, workflowTaskName[$COLUMN_LENGTH:75$]);