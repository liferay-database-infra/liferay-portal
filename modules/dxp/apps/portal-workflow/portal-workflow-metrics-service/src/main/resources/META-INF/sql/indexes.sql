create index IX_6C443ED2 on WMSLADefinition (active_, wmSLADefinitionId);
create index IX_6C053F8 on WMSLADefinition (companyId, active_, processId, name[$COLUMN_LENGTH:75$]);
create index IX_CBA6C3C on WMSLADefinition (companyId, active_, processId, status, processVersion[$COLUMN_LENGTH:75$]);
create index IX_73175D43 on WMSLADefinition (companyId, status);
create index IX_60A86AD4 on WMSLADefinition (uuid_[$COLUMN_LENGTH:75$]);

create index IX_914AFF46 on WMSLADefinitionVersion (uuid_[$COLUMN_LENGTH:75$]);
create index IX_3E2A939C on WMSLADefinitionVersion (wmSLADefinitionId, version[$COLUMN_LENGTH:75$]);