create index IX_B041F1F5 on LayoutClassedModelUsage (classNameId, classPK, type_);
create index IX_83F4441C on LayoutClassedModelUsage (classNameId, companyId, cmExternalReferenceCode[$COLUMN_LENGTH:75$], type_);
create index IX_65B533A0 on LayoutClassedModelUsage (classNameId, containerType, companyId);
create unique index IX_C9FFA599 on LayoutClassedModelUsage (classNameId, containerType, plid, classPK, cmExternalReferenceCode[$COLUMN_LENGTH:75$], containerKey[$COLUMN_LENGTH:200$], ctCollectionId);
create index IX_FFFA949E on LayoutClassedModelUsage (containerType, plid, containerKey[$COLUMN_LENGTH:200$]);
create index IX_19448DD6 on LayoutClassedModelUsage (plid);
create index IX_210402F4 on LayoutClassedModelUsage (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_D466ACA4 on LayoutLocalization (plid, languageId[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_81F1FDF6 on LayoutLocalization (uuid_[$COLUMN_LENGTH:75$]);