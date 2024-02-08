create index IX_28923D9C on OSBFaro_FaroChannel (groupId, userId);
create unique index IX_F2863AD8 on OSBFaro_FaroChannel (workspaceGroupId, channelId[$COLUMN_LENGTH:75$]);

create index IX_110AC641 on OSBFaro_FaroNotification (createTime, groupId, ownerId, type_[$COLUMN_LENGTH:75$], subtype[$COLUMN_LENGTH:75$], read_);

create unique index IX_12C47BB1 on OSBFaro_FaroPreferences (groupId, ownerId);

create unique index IX_AECCEFE7 on OSBFaro_FaroProject (corpProjectUuid[$COLUMN_LENGTH:75$]);
create unique index IX_3D4C0F8C on OSBFaro_FaroProject (groupId);
create index IX_92F9AB55 on OSBFaro_FaroProject (serverLocation[$COLUMN_LENGTH:75$]);
create index IX_DC26D918 on OSBFaro_FaroProject (userId);
create unique index IX_6CC18047 on OSBFaro_FaroProject (weDeployKey[$COLUMN_LENGTH:75$]);

create index IX_82F8539E on OSBFaro_FaroProjectEmailDomain (faroProjectId);
create index IX_7D13235C on OSBFaro_FaroProjectEmailDomain (groupId);

create unique index IX_56DCE48F on OSBFaro_FaroUser (groupId, emailAddress[$COLUMN_LENGTH:75$]);
create index IX_FCDBAA3E on OSBFaro_FaroUser (groupId, liveUserId);
create index IX_1B6F355D on OSBFaro_FaroUser (groupId, roleId);
create index IX_79F1D4DE on OSBFaro_FaroUser (groupId, status);
create unique index IX_627E2231 on OSBFaro_FaroUser (key_[$COLUMN_LENGTH:75$]);
create index IX_2F11A26F on OSBFaro_FaroUser (status, emailAddress[$COLUMN_LENGTH:75$]);
create index IX_6A34101E on OSBFaro_FaroUser (status, liveUserId);