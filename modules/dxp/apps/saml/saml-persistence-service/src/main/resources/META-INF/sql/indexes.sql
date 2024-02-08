create index IX_52963469 on SamlIdpSpConnection (companyId, samlSpEntityId[$COLUMN_LENGTH:1024$]);

create index IX_545F7B35 on SamlIdpSpSession (createDate);
create index IX_8EDF9D43 on SamlIdpSpSession (samlIdpSsoSessionId);

create index IX_E5D1CDD3 on SamlIdpSsoSession (createDate);
create index IX_BB1DAD64 on SamlIdpSsoSession (samlIdpSsoSessionKey[$COLUMN_LENGTH:75$]);

create index IX_8ED317F on SamlPeerBinding (companyId, deleted, samlNameIdValue[$COLUMN_LENGTH:1024$]);
create index IX_9495B7E6 on SamlPeerBinding (companyId, deleted, userId, samlPeerEntityId[$COLUMN_LENGTH:1024$]);

create index IX_49073861 on SamlSpAuthRequest (createDate);
create index IX_4E092962 on SamlSpAuthRequest (samlIdpEntityId[$COLUMN_LENGTH:1024$], samlSpAuthRequestKey[$COLUMN_LENGTH:75$]);

create index IX_E50C06CB on SamlSpIdpConnection (companyId, samlIdpEntityId[$COLUMN_LENGTH:1024$]);

create index IX_31762094 on SamlSpMessage (expirationDate);
create index IX_BB186B6 on SamlSpMessage (samlIdpEntityId[$COLUMN_LENGTH:1024$], samlIdpResponseKey[$COLUMN_LENGTH:75$]);

create index IX_9F470F5 on SamlSpSession (companyId, sessionIndex[$COLUMN_LENGTH:200$]);
create index IX_BDB5B96E on SamlSpSession (jSessionId[$COLUMN_LENGTH:200$]);
create index IX_5C25BCF on SamlSpSession (samlPeerBindingId);
create unique index IX_2B4A2284 on SamlSpSession (samlSpSessionKey[$COLUMN_LENGTH:75$]);