create unique index IX_17D56F1B on CPDAvailabilityEstimate (CProductId);
create index IX_E560850D on CPDAvailabilityEstimate (commerceAvailabilityEstimateId);
create index IX_A7FB249F on CPDAvailabilityEstimate (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_2A31024F on CPDefinitionInventory (CPDefinitionId, ctCollectionId);
create index IX_84781BFD on CPDefinitionInventory (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E3084F12 on CSOptionAccountEntryRel (commerceChannelId, accountEntryId);
create index IX_87F95FE7 on CSOptionAccountEntryRel (commerceShippingOptionKey[$COLUMN_LENGTH:75$]);

create unique index IX_9BBCF0DD on CommerceAddressRestriction (countryId, classNameId, classPK);

create index IX_72527224 on CommerceAvailabilityEstimate (companyId);
create index IX_802A39B on CommerceAvailabilityEstimate (uuid_[$COLUMN_LENGTH:75$]);

create index IX_12131FC1 on CommerceOrder (billingAddressId);
create index IX_F43624EF on CommerceOrder (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_46C730E3 on CommerceOrder (groupId, commercePaymentMethodKey[$COLUMN_LENGTH:75$]);
create index IX_9DCAC8C4 on CommerceOrder (orderStatus, commerceAccountId, createDate);
create index IX_B926D22A on CommerceOrder (orderStatus, groupId, commerceAccountId);
create index IX_E6C5722D on CommerceOrder (orderStatus, groupId, userId);
create index IX_7759000F on CommerceOrder (orderStatus, userId, createDate);
create index IX_4B11FAD8 on CommerceOrder (shippingAddressId);
create index IX_EFAA753 on CommerceOrder (userId);
create index IX_805EDE6 on CommerceOrder (uuid_[$COLUMN_LENGTH:75$]);

create index IX_654BB574 on CommerceOrderItem (CIBookedQuantityId);
create index IX_2E1BB39D on CommerceOrderItem (CPInstanceId);
create index IX_F9E8D927 on CommerceOrderItem (CProductId);
create index IX_415AF3E3 on CommerceOrderItem (commerceOrderId, CPInstanceId);
create index IX_15B37023 on CommerceOrderItem (commerceOrderId, subscription);
create index IX_F0E98FC7 on CommerceOrderItem (customerCommerceOrderItemId);
create index IX_22031E1C on CommerceOrderItem (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_8E1472FB on CommerceOrderItem (parentCommerceOrderItemId);
create index IX_F63CE113 on CommerceOrderItem (uuid_[$COLUMN_LENGTH:75$]);

create index IX_CEB86C22 on CommerceOrderNote (commerceOrderId, restricted);
create index IX_1529C79D on CommerceOrderNote (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_F6C0AC94 on CommerceOrderNote (uuid_[$COLUMN_LENGTH:75$]);

create index IX_CF274005 on CommerceOrderPayment (commerceOrderId);

create index IX_72C90BD4 on CommerceOrderType (companyId, active_);
create index IX_7E651D55 on CommerceOrderType (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_B535907 on CommerceOrderType (status, displayDate);
create index IX_4EE2A8A on CommerceOrderType (status, expirationDate);
create index IX_E80E724C on CommerceOrderType (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_1110AF1B on CommerceOrderTypeRel (commerceOrderTypeId, classNameId, classPK);
create index IX_9291E036 on CommerceOrderTypeRel (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_94B7172D on CommerceOrderTypeRel (uuid_[$COLUMN_LENGTH:75$]);

create index IX_5A6DB78D on CommerceShipment (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_616BDD15 on CommerceShipment (groupId, commerceAddressId);
create index IX_68FBA2B5 on CommerceShipment (groupId, status);
create index IX_64427C84 on CommerceShipment (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_3A8FCFD8 on CommerceShipmentItem (commerceOrderItemId, commerceShipmentId, commerceInventoryWarehouseId);
create index IX_A6D431BA on CommerceShipmentItem (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_DB0BB83C on CommerceShipmentItem (groupId);
create index IX_AE34F0B1 on CommerceShipmentItem (uuid_[$COLUMN_LENGTH:75$]);

create index IX_42E5F6EF on CommerceShippingMethod (groupId, active_);
create unique index IX_A205DFE on CommerceShippingMethod (groupId, engineKey[$COLUMN_LENGTH:75$]);

create unique index IX_D7D137B1 on CommerceSubscriptionEntry (commerceOrderItemId);
create index IX_5F1D189C on CommerceSubscriptionEntry (companyId, groupId, userId);
create index IX_43E6F382 on CommerceSubscriptionEntry (companyId, userId);
create index IX_3961BCA on CommerceSubscriptionEntry (groupId);
create index IX_B496E103 on CommerceSubscriptionEntry (subscriptionStatus);
create index IX_808ECCBF on CommerceSubscriptionEntry (uuid_[$COLUMN_LENGTH:75$]);