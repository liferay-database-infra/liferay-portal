create index IX_C606354 on CPDVirtualSettingFileEntry (CPDefinitionVirtualSettingId);
create index IX_B758F6BF on CPDVirtualSettingFileEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_19B2FD20 on CPDefinitionVirtualSetting (classNameId, classPK);
create index IX_9C3129B4 on CPDefinitionVirtualSetting (uuid_[$COLUMN_LENGTH:75$]);