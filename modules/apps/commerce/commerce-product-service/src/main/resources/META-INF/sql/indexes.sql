create index IX_38280B2E on CChannelAccountEntryRel (accountEntryId);
create unique index IX_286C8F97 on CChannelAccountEntryRel (classNameId, classPK, type_, accountEntryId, commerceChannelId, ctCollectionId);
create index IX_19DD836D on CChannelAccountEntryRel (classNameId, classPK, type_, commerceChannelId);

create index IX_49C09A20 on CPAttachmentFileEntry (classNameId, classPK, cdnURL[$COLUMN_LENGTH:4000$]);
create index IX_DD114140 on CPAttachmentFileEntry (classNameId, classPK, fileEntryId);
create index IX_F34F24D9 on CPAttachmentFileEntry (classNameId, classPK, status, displayDate);
create index IX_5F3A96F1 on CPAttachmentFileEntry (classNameId, classPK, status, type_, galleryEnabled);
create index IX_6A165A0B on CPAttachmentFileEntry (classNameId, fileEntryId, groupId);
create index IX_EB8C641C on CPAttachmentFileEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_5B2A1075 on CPAttachmentFileEntry (fileEntryId);
create index IX_E153EF0E on CPAttachmentFileEntry (status, displayDate);
create index IX_97122713 on CPAttachmentFileEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_95975FB4 on CPDSpecificationOptionValue (CPDefinitionId, CPOptionCategoryId);
create index IX_173E8E91 on CPDSpecificationOptionValue (CPDefinitionId, CPSpecificationOptionId);
create index IX_4F4EDBA5 on CPDSpecificationOptionValue (CPOptionCategoryId);
create index IX_573BE140 on CPDSpecificationOptionValue (CPSpecificationOptionId);
create index IX_8DA57014 on CPDSpecificationOptionValue (groupId);
create index IX_A0316289 on CPDSpecificationOptionValue (uuid_[$COLUMN_LENGTH:75$]);

create index IX_3D5A0021 on CPDefinition (CPTaxCategoryId);
create index IX_F1AEC8A7 on CPDefinition (CProductId, version);
create index IX_217AF702 on CPDefinition (companyId);
create index IX_99C4ED10 on CPDefinition (groupId, subscriptionEnabled);
create index IX_5BC4BE67 on CPDefinition (status, CProductId);
create index IX_E504F8F4 on CPDefinition (status, displayDate);
create index IX_B77A7FDE on CPDefinition (status, groupId);
create index IX_F0F1AC79 on CPDefinition (uuid_[$COLUMN_LENGTH:75$]);

create index IX_62BEA79A on CPDefinitionLink (status, displayDate);
create index IX_155AEF17 on CPDefinitionLink (status, expirationDate);
create index IX_B632A729 on CPDefinitionLink (status, type_[$COLUMN_LENGTH:75$], CPDefinitionId);
create index IX_B81A3CB5 on CPDefinitionLink (status, type_[$COLUMN_LENGTH:75$], CProductId);
create unique index IX_C46BCC48 on CPDefinitionLink (type_[$COLUMN_LENGTH:75$], CProductId, CPDefinitionId, ctCollectionId);
create index IX_497B469F on CPDefinitionLink (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E7123C7E on CPDefinitionLocalization (CPDefinitionId, languageId[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_64314388 on CPDefinitionOptionRel (CPDefinitionId, ctCollectionId, CPOptionId);
create unique index IX_346F75D6 on CPDefinitionOptionRel (CPDefinitionId, ctCollectionId, key_[$COLUMN_LENGTH:75$]);
create index IX_BDB8420C on CPDefinitionOptionRel (CPDefinitionId, required);
create index IX_749E99EB on CPDefinitionOptionRel (CPDefinitionId, skuContributor);
create index IX_449BFCFE on CPDefinitionOptionRel (companyId);
create index IX_A65BAB00 on CPDefinitionOptionRel (groupId);
create index IX_6A9F3A75 on CPDefinitionOptionRel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_DD74AF09 on CPDefinitionOptionValueRel (CPDefinitionOptionRelId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_4A77D282 on CPDefinitionOptionValueRel (CPDefinitionOptionRelId, preselected);
create index IX_9AAE065F on CPDefinitionOptionValueRel (CPInstanceUuid[$COLUMN_LENGTH:75$]);
create index IX_44C2E505 on CPDefinitionOptionValueRel (companyId);
create index IX_695AE8C7 on CPDefinitionOptionValueRel (groupId);
create index IX_ED0D5F42 on CPDefinitionOptionValueRel (key_[$COLUMN_LENGTH:75$]);
create index IX_849B347C on CPDefinitionOptionValueRel (uuid_[$COLUMN_LENGTH:75$]);

create index IX_290BF7BA on CPDisplayLayout (classNameId, classPK);
create unique index IX_7F728A18 on CPDisplayLayout (groupId, classNameId, classPK, ctCollectionId);
create index IX_87D8FA30 on CPDisplayLayout (groupId, layoutPageTemplateEntryUuid[$COLUMN_LENGTH:75$]);
create index IX_5A96CDC9 on CPDisplayLayout (groupId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_62EBF5A on CPDisplayLayout (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E596CA71 on CPInstance (CPDefinitionId, CPInstanceUuid[$COLUMN_LENGTH:75$], ctCollectionId);
create unique index IX_A41B08BF on CPInstance (CPDefinitionId, sku[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_FF405804 on CPInstance (CPInstanceUuid[$COLUMN_LENGTH:75$]);
create index IX_4389FA40 on CPInstance (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_C1F8242 on CPInstance (groupId);
create index IX_1655F718 on CPInstance (sku[$COLUMN_LENGTH:75$], companyId);
create index IX_50870703 on CPInstance (status, CPDefinitionId, displayDate);
create index IX_BD04B832 on CPInstance (status, displayDate);
create index IX_75478E1C on CPInstance (status, groupId);
create index IX_E116A00D on CPInstance (status, replacementCPInstanceUuid[$COLUMN_LENGTH:75$], replacementCProductId);
create index IX_23A88537 on CPInstance (uuid_[$COLUMN_LENGTH:75$]);

create index IX_E551D3AA on CPInstanceOptionValueRel (CPDefinitionOptionRelId);
create unique index IX_C7B0D143 on CPInstanceOptionValueRel (CPInstanceId, CPDefinitionOptionRelId, CPDefinitionOptionValueRelId, ctCollectionId);
create index IX_D3B702C2 on CPInstanceOptionValueRel (CPInstanceId, CPDefinitionOptionValueRelId);
create index IX_9350E8BA on CPInstanceOptionValueRel (uuid_[$COLUMN_LENGTH:75$]);

create index IX_4351C7A1 on CPInstanceUOM (CPInstanceId, active_);
create unique index IX_ECB84885 on CPInstanceUOM (CPInstanceId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_611154B9 on CPInstanceUOM (CPInstanceId, primary_);
create index IX_77D52E48 on CPInstanceUOM (companyId, key_[$COLUMN_LENGTH:75$], sku[$COLUMN_LENGTH:75$]);
create index IX_14E97BE7 on CPInstanceUOM (companyId, sku[$COLUMN_LENGTH:75$]);
create index IX_3B1D5A28 on CPInstanceUOM (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_675E5BAC on CPMeasurementUnit (companyId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_F0C14577 on CPMeasurementUnit (companyId, type_, primary_);
create index IX_DDBEF2CF on CPMeasurementUnit (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_DA67BC6 on CPMeasurementUnit (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_534012BD on CPOption (companyId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_32161B20 on CPOption (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_4D526617 on CPOption (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_165829DF on CPOptionCategory (companyId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_200023B9 on CPOptionCategory (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_7E52DDA3 on CPOptionValue (CPOptionId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_C95EFDB3 on CPOptionValue (companyId);
create index IX_33E56033 on CPOptionValue (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_117C312A on CPOptionValue (uuid_[$COLUMN_LENGTH:75$]);

create index IX_421ED80 on CPSpecificationOption (CPOptionCategoryId);
create unique index IX_D6F55A34 on CPSpecificationOption (companyId, key_[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_399A944E on CPSpecificationOption (uuid_[$COLUMN_LENGTH:75$]);

create index IX_64046706 on CPTaxCategory (companyId);
create index IX_4398BD86 on CPTaxCategory (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_9542947D on CPTaxCategory (uuid_[$COLUMN_LENGTH:75$]);

create index IX_106B0376 on CProduct (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_77F5B8F8 on CProduct (groupId);
create index IX_ADBEBA6D on CProduct (uuid_[$COLUMN_LENGTH:75$]);

create index IX_7A691498 on CommerceCatalog (accountEntryId);
create index IX_65864AFC on CommerceCatalog (companyId, system_);
create index IX_97A06F44 on CommerceCatalog (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_64B1823B on CommerceCatalog (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C2C38B02 on CommerceChannel (accountEntryId);
create index IX_482E401A on CommerceChannel (companyId);
create index IX_F6F9469A on CommerceChannel (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E1ECD95 on CommerceChannel (siteGroupId);
create index IX_51DFC591 on CommerceChannel (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_839D8AF on CommerceChannelRel (commerceChannelId, classNameId, classPK, ctCollectionId);