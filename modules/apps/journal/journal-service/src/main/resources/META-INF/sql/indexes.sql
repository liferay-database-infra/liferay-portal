create index IX_745E04FA on JournalArticle (DDMStructureId);
create index IX_829E783C on JournalArticle (DDMTemplateKey[$COLUMN_LENGTH:75$]);
create index IX_350627A3 on JournalArticle (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_3048AF7A on JournalArticle (groupId, DDMStructureId);
create index IX_50AE2BC on JournalArticle (groupId, DDMTemplateKey[$COLUMN_LENGTH:75$]);
create index IX_6D117C1E on JournalArticle (groupId, classNameId, DDMStructureId);
create index IX_372F560 on JournalArticle (groupId, classNameId, DDMTemplateKey[$COLUMN_LENGTH:75$]);
create index IX_9CE6E0FA on JournalArticle (groupId, classNameId, classPK);
create index IX_A25FBBAD on JournalArticle (groupId, classNameId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_373DCC43 on JournalArticle (groupId, classNameId, userId);
create index IX_5CD17502 on JournalArticle (groupId, folderId);
create index IX_EBDCB709 on JournalArticle (groupId, layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_2E9D2FE1 on JournalArticle (groupId, status, articleId[$COLUMN_LENGTH:75$]);
create index IX_BCAFC000 on JournalArticle (groupId, status, classNameId, folderId);
create index IX_9D8D768 on JournalArticle (groupId, status, folderId);
create index IX_127C3873 on JournalArticle (groupId, status, urlTitle[$COLUMN_LENGTH:255$]);
create index IX_BA69FF19 on JournalArticle (groupId, urlTitle[$COLUMN_LENGTH:255$]);
create index IX_D19C1B9F on JournalArticle (groupId, userId);
create unique index IX_F68A5847 on JournalArticle (groupId, version, articleId[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_21970C89 on JournalArticle (layoutUuid[$COLUMN_LENGTH:75$]);
create index IX_89FF8B06 on JournalArticle (resourcePrimKey, indexable);
create index IX_EF9B7028 on JournalArticle (smallImageId);
create index IX_2AA511D5 on JournalArticle (status, displayDate);
create index IX_BCE7DFEC on JournalArticle (status, resourcePrimKey, indexable);
create index IX_BDC9FD6B on JournalArticle (status, version, companyId);
create index IX_9279D89A on JournalArticle (uuid_[$COLUMN_LENGTH:75$]);
create index IX_E5236285 on JournalArticle (version, companyId);

create unique index IX_56289F53 on JournalArticleLocalization (articlePK, languageId[$COLUMN_LENGTH:75$], ctCollectionId);

create unique index IX_55E54293 on JournalArticleResource (groupId, articleId[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_CE0D762C on JournalArticleResource (uuid_[$COLUMN_LENGTH:75$]);

create index IX_A126AE9C on JournalContentSearch (articleId[$COLUMN_LENGTH:75$]);
create index IX_42F51F38 on JournalContentSearch (companyId);
create unique index IX_B632BFEC on JournalContentSearch (portletId[$COLUMN_LENGTH:200$], groupId, articleId[$COLUMN_LENGTH:75$], privateLayout, layoutId, ctCollectionId);
create index IX_2A049B92 on JournalContentSearch (portletId[$COLUMN_LENGTH:200$], groupId, privateLayout, layoutId);

create unique index IX_96EBC505 on JournalFeed (groupId, feedId[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_AC25CE4 on JournalFeed (uuid_[$COLUMN_LENGTH:75$]);

create index IX_E6E2725D on JournalFolder (companyId);
create index IX_D7DBACDD on JournalFolder (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_E135711C on JournalFolder (groupId, name[$COLUMN_LENGTH:100$]);
create unique index IX_C5FC5573 on JournalFolder (groupId, parentFolderId, name[$COLUMN_LENGTH:100$], ctCollectionId);
create index IX_EFD9CAC on JournalFolder (groupId, parentFolderId, status);
create index IX_8D6902B7 on JournalFolder (status, companyId);
create index IX_526511D4 on JournalFolder (uuid_[$COLUMN_LENGTH:75$]);