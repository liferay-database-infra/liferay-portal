create index IX_68E2B208 on SiteNavigationMenu (companyId);
create index IX_1D786176 on SiteNavigationMenu (groupId, auto_);
create unique index IX_8535F092 on SiteNavigationMenu (groupId, name[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_1125400B on SiteNavigationMenu (groupId, type_);
create index IX_837E9B7F on SiteNavigationMenu (uuid_[$COLUMN_LENGTH:75$]);

create index IX_B88C2AB5 on SiteNavigationMenuItem (companyId);
create index IX_16CE0535 on SiteNavigationMenuItem (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_75495C39 on SiteNavigationMenuItem (parentSiteNavigationMenuItemId);
create index IX_68A39980 on SiteNavigationMenuItem (siteNavigationMenuId, name[$COLUMN_LENGTH:255$]);
create index IX_2294C622 on SiteNavigationMenuItem (siteNavigationMenuId, parentSiteNavigationMenuItemId);
create index IX_84031A2C on SiteNavigationMenuItem (uuid_[$COLUMN_LENGTH:75$]);