create unique index IX_D9799B2A on CPMethodGroupRelQualifier (CPaymentMethodGroupRelId, classNameId, classPK);

create index IX_B481AC65 on CommercePaymentEntry (companyId, classNameId, classPK, type_);
create index IX_762595DB on CommercePaymentEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);

create index IX_8BE29B30 on CommercePaymentEntryAudit (commercePaymentEntryId);

create index IX_98EF79EB on CommercePaymentMethodGroupRel (groupId, active_);
create unique index IX_D12353CE on CommercePaymentMethodGroupRel (groupId, paymentIntegrationKey[$COLUMN_LENGTH:75$]);