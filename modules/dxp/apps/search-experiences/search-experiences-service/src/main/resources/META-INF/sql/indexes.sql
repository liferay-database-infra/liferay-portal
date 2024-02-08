create index IX_F6C6095A on SXPBlueprint (companyId);
create index IX_621C0FDA on SXPBlueprint (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_7C3D0ED1 on SXPBlueprint (uuid_[$COLUMN_LENGTH:75$]);

create index IX_62CF31E7 on SXPElement (companyId, readOnly);
create index IX_2F49914A on SXPElement (companyId, type_, status);
create index IX_96B50D1 on SXPElement (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_51831DC8 on SXPElement (uuid_[$COLUMN_LENGTH:75$]);