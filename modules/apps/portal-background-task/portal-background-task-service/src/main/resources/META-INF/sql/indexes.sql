create index IX_C5A6C78F on BackgroundTask (companyId);
create index IX_FBF5FAA2 on BackgroundTask (completed);
create index IX_C71C3B7 on BackgroundTask (groupId, status);
create index IX_5DEA9B92 on BackgroundTask (groupId, taskExecutorClassName[$COLUMN_LENGTH:200$], completed, name[$COLUMN_LENGTH:255$]);
create index IX_3DF75DEB on BackgroundTask (groupId, taskExecutorClassName[$COLUMN_LENGTH:200$], name[$COLUMN_LENGTH:255$]);
create index IX_8079D5D7 on BackgroundTask (groupId, taskExecutorClassName[$COLUMN_LENGTH:200$], status);
create index IX_75638CDF on BackgroundTask (status);
create index IX_65C082FF on BackgroundTask (taskExecutorClassName[$COLUMN_LENGTH:200$], status);