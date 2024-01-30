create index IX_6DA5084D on BatchEngineExportTask (companyId);
create index IX_460BF447 on BatchEngineExportTask (executeStatus[$COLUMN_LENGTH:75$]);
create index IX_614A82CD on BatchEngineExportTask (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_929DC7C4 on BatchEngineExportTask (uuid_[$COLUMN_LENGTH:75$]);

create index IX_CEAC687C on BatchEngineImportTask (companyId);
create index IX_23D17776 on BatchEngineImportTask (executeStatus[$COLUMN_LENGTH:75$]);
create index IX_E76D66FC on BatchEngineImportTask (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_8814E9F3 on BatchEngineImportTask (uuid_[$COLUMN_LENGTH:75$]);

create index IX_863EDEA9 on BatchEngineImportTaskError (batchEngineImportTaskId);