create index IX_99628DD1 on DEDataDefinitionFieldLink (classNameId, classPK);
create unique index IX_BE1B953E on DEDataDefinitionFieldLink (ddmStructureId, classNameId, fieldName[$COLUMN_LENGTH:255$], classPK, ctCollectionId);
create index IX_F41BAE57 on DEDataDefinitionFieldLink (ddmStructureId, fieldName[$COLUMN_LENGTH:255$]);
create index IX_79C55FA3 on DEDataDefinitionFieldLink (uuid_[$COLUMN_LENGTH:75$]);

create index IX_81B6947 on DEDataListView (ddmStructureId, groupId, companyId);
create index IX_6E5B5B8B on DEDataListView (uuid_[$COLUMN_LENGTH:75$]);