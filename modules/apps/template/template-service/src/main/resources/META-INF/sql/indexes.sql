create index IX_9B9729B4 on TemplateEntry (ddmTemplateId);
create index IX_876F53C1 on TemplateEntry (groupId, infoItemClassName[$COLUMN_LENGTH:75$], infoItemFormVariationKey[$COLUMN_LENGTH:75$]);
create index IX_B2C1E921 on TemplateEntry (uuid_[$COLUMN_LENGTH:75$]);