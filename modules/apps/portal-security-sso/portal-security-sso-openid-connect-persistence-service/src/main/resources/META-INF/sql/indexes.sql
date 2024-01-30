create index IX_396C5BCB on OpenIdConnectSession (accessTokenExpirationDate);
create index IX_8CF9E4B5 on OpenIdConnectSession (authServerWellKnownURI[$COLUMN_LENGTH:256$], clientId[$COLUMN_LENGTH:256$], companyId);
create unique index IX_A44B0ACD on OpenIdConnectSession (authServerWellKnownURI[$COLUMN_LENGTH:256$], clientId[$COLUMN_LENGTH:256$], userId);