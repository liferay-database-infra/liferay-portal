create index IX_C83537E on CVirtualOrderItemFileEntry (commerceVirtualOrderItemId, fileEntryId);
create index IX_810681FC on CVirtualOrderItemFileEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_44EADF9A on CommerceVirtualOrderItem (commerceOrderItemId);
create index IX_4D1B21E8 on CommerceVirtualOrderItem (uuid_[$COLUMN_LENGTH:75$]);