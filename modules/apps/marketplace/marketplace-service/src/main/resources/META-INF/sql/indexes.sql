create index IX_DD6000D6 on Marketplace_App (category[$COLUMN_LENGTH:255$]);
create index IX_865B7BD5 on Marketplace_App (companyId);
create index IX_20F14D93 on Marketplace_App (remoteAppId);
create index IX_A84C2B4C on Marketplace_App (uuid_[$COLUMN_LENGTH:75$]);

create index IX_BFA70F2C on Marketplace_Module (appId, bundleSymbolicName[$COLUMN_LENGTH:500$], bundleVersion[$COLUMN_LENGTH:75$]);
create index IX_D2B7F30F on Marketplace_Module (appId, contextName[$COLUMN_LENGTH:75$]);
create index IX_1651CD05 on Marketplace_Module (bundleSymbolicName[$COLUMN_LENGTH:500$]);
create index IX_D84EB54F on Marketplace_Module (contextName[$COLUMN_LENGTH:75$]);
create index IX_BE9CAAF9 on Marketplace_Module (uuid_[$COLUMN_LENGTH:75$]);