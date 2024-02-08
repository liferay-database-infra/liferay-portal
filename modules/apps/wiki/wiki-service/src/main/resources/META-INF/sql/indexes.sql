create index IX_57461270 on WikiNode (externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_42251A7A on WikiNode (groupId, name[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_23325358 on WikiNode (groupId, status);
create index IX_7F41314A on WikiNode (status, companyId);
create index IX_5B7AFD67 on WikiNode (uuid_[$COLUMN_LENGTH:75$]);

create index IX_B65BBC83 on WikiPage (companyId);
create index IX_8DB2DF03 on WikiPage (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_6168051B on WikiPage (format[$COLUMN_LENGTH:75$]);
create index IX_A3986C67 on WikiPage (nodeId, head, parentTitle[$COLUMN_LENGTH:255$]);
create index IX_777E999 on WikiPage (nodeId, head, redirectTitle[$COLUMN_LENGTH:255$]);
create index IX_E30FDBD1 on WikiPage (nodeId, head, resourcePrimKey);
create index IX_AF5169AD on WikiPage (nodeId, head, title[$COLUMN_LENGTH:255$], groupId);
create index IX_EB895013 on WikiPage (nodeId, parentTitle[$COLUMN_LENGTH:255$]);
create index IX_16BE9245 on WikiPage (nodeId, redirectTitle[$COLUMN_LENGTH:255$]);
create unique index IX_5AD01561 on WikiPage (nodeId, resourcePrimKey, version, ctCollectionId);
create index IX_6F9E3908 on WikiPage (nodeId, status, groupId, userId);
create index IX_83F5E3FD on WikiPage (nodeId, status, head, parentTitle[$COLUMN_LENGTH:255$], groupId);
create index IX_9AC4D2FF on WikiPage (nodeId, status, head, redirectTitle[$COLUMN_LENGTH:255$]);
create index IX_D1F6BA7F on WikiPage (nodeId, status, resourcePrimKey);
create index IX_66B3942F on WikiPage (nodeId, status, title[$COLUMN_LENGTH:255$]);
create index IX_EF476996 on WikiPage (nodeId, status, userId);
create unique index IX_FC40103D on WikiPage (nodeId, title[$COLUMN_LENGTH:255$], version, ctCollectionId);
create index IX_85E7CC76 on WikiPage (resourcePrimKey);
create index IX_5D2E2B50 on WikiPage (status, resourcePrimKey);
create index IX_23414FFA on WikiPage (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4B97CF55 on WikiPageResource (nodeId, title[$COLUMN_LENGTH:255$], ctCollectionId);
create index IX_11178D8C on WikiPageResource (uuid_[$COLUMN_LENGTH:75$]);