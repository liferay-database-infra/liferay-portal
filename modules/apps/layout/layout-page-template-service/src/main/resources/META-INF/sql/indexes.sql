create index IX_5A1F4BFC on LayoutPageTemplateCollection (groupId, parentLPTCollectionId);
create unique index IX_4EA208D0 on LayoutPageTemplateCollection (groupId, type_, ctCollectionId, lptCollectionKey[$COLUMN_LENGTH:75$]);
create unique index IX_B4CCF18A on LayoutPageTemplateCollection (groupId, type_, ctCollectionId, name[$COLUMN_LENGTH:75$]);
create index IX_3B04B428 on LayoutPageTemplateCollection (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_E4BCB00E on LayoutPageTemplateEntry (ctCollectionId, plid);
create index IX_A6459477 on LayoutPageTemplateEntry (groupId, classNameId, classTypeId, defaultTemplate);
create index IX_F2B223DD on LayoutPageTemplateEntry (groupId, classNameId, status, classTypeId, defaultTemplate);
create unique index IX_908B7582 on LayoutPageTemplateEntry (groupId, ctCollectionId, layoutPageTemplateEntryKey[$COLUMN_LENGTH:75$]);
create index IX_E7CC5585 on LayoutPageTemplateEntry (groupId, layoutPageTemplateCollectionId);
create index IX_2EBCD96F on LayoutPageTemplateEntry (groupId, name[$COLUMN_LENGTH:75$], layoutPageTemplateCollectionId);
create index IX_DB1B076B on LayoutPageTemplateEntry (groupId, status, layoutPageTemplateCollectionId);
create index IX_7F557D5 on LayoutPageTemplateEntry (groupId, status, name[$COLUMN_LENGTH:75$], layoutPageTemplateCollectionId);
create index IX_F406284D on LayoutPageTemplateEntry (groupId, type_, classNameId, defaultTemplate);
create index IX_B052B115 on LayoutPageTemplateEntry (groupId, type_, classNameId, name[$COLUMN_LENGTH:75$], classTypeId);
create index IX_E093796F on LayoutPageTemplateEntry (groupId, type_, classNameId, status, name[$COLUMN_LENGTH:75$], classTypeId);
create index IX_CD9D4A70 on LayoutPageTemplateEntry (groupId, type_, layoutPageTemplateCollectionId);
create unique index IX_EA4F6A18 on LayoutPageTemplateEntry (groupId, type_, name[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_9C2D0C95 on LayoutPageTemplateEntry (groupId, type_, status, defaultTemplate);
create index IX_A185457E on LayoutPageTemplateEntry (layoutPrototypeId);
create index IX_FDC9AADA on LayoutPageTemplateEntry (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_D822FD2D on LayoutPageTemplateStructure (groupId, plid, ctCollectionId);
create index IX_42CF1FF9 on LayoutPageTemplateStructure (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_AFE18D91 on LayoutPageTemplateStructureRel (segmentsExperienceId, layoutPageTemplateStructureId, ctCollectionId);
create index IX_90E9EE60 on LayoutPageTemplateStructureRel (uuid_[$COLUMN_LENGTH:75$]);