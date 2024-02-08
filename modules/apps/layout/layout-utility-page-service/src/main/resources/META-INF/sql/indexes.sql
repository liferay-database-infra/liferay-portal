create unique index IX_CA8014F0 on LayoutUtilityPageEntry (ctCollectionId, plid);
create index IX_5FB5641 on LayoutUtilityPageEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_4DFB63A5 on LayoutUtilityPageEntry (groupId, type_[$COLUMN_LENGTH:75$], ctCollectionId, name[$COLUMN_LENGTH:75$]);
create index IX_CD0973B5 on LayoutUtilityPageEntry (groupId, type_[$COLUMN_LENGTH:75$], defaultLayoutUtilityPageEntry);
create index IX_602C0338 on LayoutUtilityPageEntry (uuid_[$COLUMN_LENGTH:75$]);