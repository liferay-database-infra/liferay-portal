create index IX_5D2FDA7E on AssetListEntry (groupId, assetEntryType[$COLUMN_LENGTH:255$], assetEntrySubtype[$COLUMN_LENGTH:255$]);
create unique index IX_94F88BF8 on AssetListEntry (groupId, ctCollectionId, assetListEntryKey[$COLUMN_LENGTH:75$]);
create index IX_7CF7D3AB on AssetListEntry (groupId, title[$COLUMN_LENGTH:75$], assetEntryType[$COLUMN_LENGTH:255$], assetEntrySubtype[$COLUMN_LENGTH:255$]);
create unique index IX_D24215B1 on AssetListEntry (groupId, title[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_4FE08A35 on AssetListEntry (groupId, type_);
create index IX_EED63315 on AssetListEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_FAAE938C on AssetListEntryAssetEntryRel (assetListEntryId, segmentsEntryId, position, ctCollectionId);
create index IX_3C277846 on AssetListEntryAssetEntryRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_BD6BD27D on AssetListEntrySegmentsEntryRel (segmentsEntryId, assetListEntryId, ctCollectionId);
create index IX_926D95CE on AssetListEntrySegmentsEntryRel (uuid_[$COLUMN_LENGTH:75$]);

create index IX_3289872C on AssetListEntryUsage (classNameId, key_[$COLUMN_LENGTH:255$], companyId);
create index IX_14EA0167 on AssetListEntryUsage (classNameId, key_[$COLUMN_LENGTH:255$], groupId, type_);
create unique index IX_247385BF on AssetListEntryUsage (classNameId, key_[$COLUMN_LENGTH:255$], plid, groupId, containerType, containerKey[$COLUMN_LENGTH:255$], ctCollectionId);
create index IX_8D0D6BEC on AssetListEntryUsage (plid, containerType, containerKey[$COLUMN_LENGTH:255$]);
create index IX_552494BC on AssetListEntryUsage (uuid_[$COLUMN_LENGTH:75$]);