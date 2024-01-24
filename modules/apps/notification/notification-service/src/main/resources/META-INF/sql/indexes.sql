create index IX_42E60133 on NQueueEntryAttachment (notificationQueueEntryId);

create unique index IX_8F1205E1 on NTemplateAttachment (notificationTemplateId, objectFieldId);

create index IX_83DBCE06 on NotificationQueueEntry (notificationTemplateId);
create index IX_3B9F9C6C on NotificationQueueEntry (sentDate);
create index IX_D4C13ED4 on NotificationQueueEntry (type_[$COLUMN_LENGTH:75$], status);

create index IX_470340CF on NotificationRecipient (classPK);
create index IX_18E7378B on NotificationRecipient (uuid_[$COLUMN_LENGTH:75$]);

create index IX_2CAD899B on NotificationRecipientSetting (notificationRecipientId, name[$COLUMN_LENGTH:75$]);
create index IX_EABDE837 on NotificationRecipientSetting (uuid_[$COLUMN_LENGTH:75$]);

create index IX_2AF3A49D on NotificationTemplate (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_DE48994 on NotificationTemplate (uuid_[$COLUMN_LENGTH:75$]);