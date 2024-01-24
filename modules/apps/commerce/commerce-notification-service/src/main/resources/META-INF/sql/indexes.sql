create unique index IX_CB3B24DA on CNTemplateCAccountGroupRel (commerceAccountGroupId, commerceNotificationTemplateId);

create index IX_6E9D8183 on CNotificationAttachment (CNotificationQueueEntryId);
create index IX_1EC49728 on CNotificationAttachment (uuid_[$COLUMN_LENGTH:75$]);

create index IX_F9149FC on CommerceNotificationQueueEntry (commerceNotificationTemplateId);
create index IX_78E78086 on CommerceNotificationQueueEntry (sent, groupId, classNameId, classPK);
create index IX_80026CA7 on CommerceNotificationQueueEntry (sentDate);

create index IX_36E741F3 on CommerceNotificationTemplate (groupId, enabled, type_[$COLUMN_LENGTH:75$]);
create index IX_BA25DBF9 on CommerceNotificationTemplate (uuid_[$COLUMN_LENGTH:75$]);