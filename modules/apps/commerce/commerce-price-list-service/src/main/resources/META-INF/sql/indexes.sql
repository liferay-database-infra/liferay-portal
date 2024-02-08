create unique index IX_8EF03EDA on CPLCommerceGroupAccountRel (commercePriceListId, commerceAccountGroupId, ctCollectionId);
create index IX_B61A3D88 on CPLCommerceGroupAccountRel (uuid_[$COLUMN_LENGTH:75$]);

create index IX_91FF16C0 on CommercePriceEntry (CPInstanceUuid[$COLUMN_LENGTH:75$], quantity, unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_CA7A2D0D on CommercePriceEntry (commercePriceListId);
create index IX_5E36B51E on CommercePriceEntry (companyId);
create index IX_7D4CAB9E on CommercePriceEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_F474B0CB on CommercePriceEntry (status, CPInstanceUuid[$COLUMN_LENGTH:75$], commercePriceListId);
create index IX_B9AEC410 on CommercePriceEntry (status, displayDate);
create index IX_255AF6E1 on CommercePriceEntry (status, expirationDate);
create index IX_3D05B295 on CommercePriceEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_473B4D8D on CommercePriceList (commerceCurrencyId);
create index IX_2AA1AF56 on CommercePriceList (companyId);
create index IX_5680C5D6 on CommercePriceList (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_3AE5B429 on CommercePriceList (groupId, catalogBasePriceList);
create index IX_B2378CA on CommercePriceList (groupId, companyId, status, type_[$COLUMN_LENGTH:75$]);
create index IX_F0A63DB9 on CommercePriceList (groupId, type_[$COLUMN_LENGTH:75$], catalogBasePriceList);
create index IX_863045BB on CommercePriceList (parentCommercePriceListId);
create index IX_72305848 on CommercePriceList (status, displayDate);
create index IX_7D653CCD on CommercePriceList (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_5FBCA042 on CommercePriceListAccountRel (commercePriceListId, commerceAccountId, ctCollectionId);
create index IX_CB9F801 on CommercePriceListAccountRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_6B436902 on CommercePriceListChannelRel (commercePriceListId, commerceChannelId, ctCollectionId);
create index IX_50112D7 on CommercePriceListChannelRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_7D707AEE on CommercePriceListDiscountRel (commercePriceListId, commerceDiscountId, ctCollectionId);
create index IX_FCE03A6D on CommercePriceListDiscountRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4EA60BE2 on CommercePriceListOrderTypeRel (commercePriceListId, commerceOrderTypeId, ctCollectionId);
create index IX_F1A7E07C on CommercePriceListOrderTypeRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4072830C on CommerceTierPriceEntry (commercePriceEntryId, minQuantity, ctCollectionId);
create index IX_F5D5725C on CommerceTierPriceEntry (companyId);
create index IX_DCF6F0DC on CommerceTierPriceEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_A24CFC08 on CommerceTierPriceEntry (status, commercePriceEntryId, minQuantity);
create index IX_CB288BCE on CommerceTierPriceEntry (status, displayDate);
create index IX_D00E2E63 on CommerceTierPriceEntry (status, expirationDate);
create index IX_CAAE33D3 on CommerceTierPriceEntry (uuid_[$COLUMN_LENGTH:75$]);