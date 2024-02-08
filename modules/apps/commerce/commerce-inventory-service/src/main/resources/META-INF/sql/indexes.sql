create index IX_FF246F06 on CIAudit (companyId, sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$]);
create index IX_E7D143D9 on CIAudit (createDate);

create index IX_33BF9CB0 on CIBookedQuantity (expirationDate);
create index IX_B0E71F4A on CIBookedQuantity (sku[$COLUMN_LENGTH:75$], companyId, unitOfMeasureKey[$COLUMN_LENGTH:75$]);

create index IX_F588314 on CIReplenishmentItem (availabilityDate);
create index IX_967CACA8 on CIReplenishmentItem (commerceInventoryWarehouseId);
create index IX_E601D1 on CIReplenishmentItem (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E5793059 on CIReplenishmentItem (sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$], availabilityDate);
create index IX_52651DAC on CIReplenishmentItem (sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$], companyId);
create index IX_4E7FCEC8 on CIReplenishmentItem (uuid_[$COLUMN_LENGTH:75$]);

create index IX_ED5C5C8A on CIWarehouse (companyId, countryTwoLettersISOCode[$COLUMN_LENGTH:75$], active_);
create index IX_18830145 on CIWarehouse (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_64D2363C on CIWarehouse (uuid_[$COLUMN_LENGTH:75$]);

create index IX_8C9705F5 on CIWarehouseGroupRel (groupId, primary_, commerceWarehouseId);

create index IX_613DDF72 on CIWarehouseItem (externalReferenceCode[$COLUMN_LENGTH:75$]);
create unique index IX_2116EDCE on CIWarehouseItem (sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$], commerceInventoryWarehouseId);
create index IX_F4A9CBEB on CIWarehouseItem (sku[$COLUMN_LENGTH:75$], unitOfMeasureKey[$COLUMN_LENGTH:75$], companyId);
create index IX_8610E69 on CIWarehouseItem (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_A743341B on CIWarehouseRel (CIWarehouseId, classNameId, classPK);