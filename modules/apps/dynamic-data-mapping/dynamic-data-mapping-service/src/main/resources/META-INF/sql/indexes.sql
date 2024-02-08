create index IX_24F33B18 on DDMContent (ctCollectionId, companyId);
create index IX_4FBFA79A on DDMContent (ctCollectionId, groupId);
create index IX_636FE039 on DDMContent (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_35B520FB on DDMContent (ctCollectionId, uuid_[$COLUMN_LENGTH:75$], groupId);

create index IX_DB54A6E5 on DDMDataProviderInstance (companyId);
create index IX_1333A2A7 on DDMDataProviderInstance (groupId);
create index IX_7E2D365C on DDMDataProviderInstance (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_3EEBC3FE on DDMDataProviderInstanceLink (structureId, dataProviderInstanceId, ctCollectionId);

create index IX_82E80BEC on DDMField (companyId, fieldType[$COLUMN_LENGTH:255$]);
create index IX_803F5479 on DDMField (storageId, fieldName[$COLUMN_LENGTH:255$]);
create unique index IX_C8D44CE0 on DDMField (storageId, instanceId[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_DE90A287 on DDMField (structureVersionId);

create unique index IX_CE5ED938 on DDMFieldAttribute (attributeName[$COLUMN_LENGTH:255$], languageId[$COLUMN_LENGTH:75$], fieldId, ctCollectionId);
create index IX_352F4548 on DDMFieldAttribute (attributeName[$COLUMN_LENGTH:255$], smallAttributeValue[$COLUMN_LENGTH:255$]);
create index IX_1B17CB0D on DDMFieldAttribute (attributeName[$COLUMN_LENGTH:255$], storageId);
create index IX_390E28C3 on DDMFieldAttribute (storageId, languageId[$COLUMN_LENGTH:75$]);

create index IX_9E1C31FE on DDMFormInstance (groupId);
create index IX_30973DF3 on DDMFormInstance (uuid_[$COLUMN_LENGTH:75$]);

create index IX_5BC982B on DDMFormInstanceRecord (companyId);
create index IX_76144ED5 on DDMFormInstanceRecord (formInstanceId, formInstanceVersion[$COLUMN_LENGTH:75$]);
create index IX_3C8DBDFF on DDMFormInstanceRecord (formInstanceId, userId);
create index IX_F305DBA2 on DDMFormInstanceRecord (uuid_[$COLUMN_LENGTH:75$]);

create index IX_B5A3FAC6 on DDMFormInstanceRecordVersion (formInstanceRecordId, status);
create unique index IX_2AD34871 on DDMFormInstanceRecordVersion (formInstanceRecordId, version[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_A9E8357F on DDMFormInstanceRecordVersion (status, formInstanceId, formInstanceVersion[$COLUMN_LENGTH:75$], userId);

create index IX_953190E8 on DDMFormInstanceReport (formInstanceId);

create index IX_EB92EF26 on DDMFormInstanceVersion (formInstanceId, status);
create unique index IX_EDE79011 on DDMFormInstanceVersion (formInstanceId, version[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_6979A733 on DDMStorageLink (classPK, ctCollectionId);
create index IX_81776090 on DDMStorageLink (structureId);
create index IX_14DADA22 on DDMStorageLink (structureVersionId);
create index IX_930CDC11 on DDMStorageLink (uuid_[$COLUMN_LENGTH:75$]);

create index IX_FC299886 on DDMStructure (classNameId, companyId);
create unique index IX_4A767F79 on DDMStructure (groupId, classNameId, structureKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_43395316 on DDMStructure (groupId, parentStructureId);
create index IX_657899A8 on DDMStructure (parentStructureId);
create index IX_F9622837 on DDMStructure (structureKey[$COLUMN_LENGTH:75$]);
create index IX_C6E4BBB3 on DDMStructure (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E826A6F9 on DDMStructureLayout (groupId, classNameId, structureLayoutKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_C72DCE6E on DDMStructureLayout (groupId, classNameId, structureVersionId);
create index IX_FA4B3E37 on DDMStructureLayout (structureLayoutKey[$COLUMN_LENGTH:75$]);
create index IX_B7158C0A on DDMStructureLayout (structureVersionId);
create index IX_AFA9B529 on DDMStructureLayout (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_F13BE80B on DDMStructureLink (structureId, classNameId, classPK, ctCollectionId);

create index IX_17B3C96C on DDMStructureVersion (structureId, status);
create unique index IX_CBBDC98B on DDMStructureVersion (structureId, version[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_BD8AAC95 on DDMTemplate (classNameId, groupId, templateKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_F3EE2D5F on DDMTemplate (classNameId, type_[$COLUMN_LENGTH:75$], groupId, classPK, mode_[$COLUMN_LENGTH:75$]);
create index IX_E4A9E4E4 on DDMTemplate (language[$COLUMN_LENGTH:75$]);
create index IX_127A35B0 on DDMTemplate (smallImageId);
create index IX_9664DC13 on DDMTemplate (templateKey[$COLUMN_LENGTH:75$]);
create index IX_99BE35B3 on DDMTemplate (type_[$COLUMN_LENGTH:75$]);
create index IX_32565012 on DDMTemplate (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_79ED5CFA on DDMTemplateLink (classNameId, classPK, ctCollectionId);
create index IX_85278170 on DDMTemplateLink (templateId);

create index IX_66382FC6 on DDMTemplateVersion (templateId, status);
create unique index IX_212C3371 on DDMTemplateVersion (templateId, version[$COLUMN_LENGTH:75$], ctCollectionId);