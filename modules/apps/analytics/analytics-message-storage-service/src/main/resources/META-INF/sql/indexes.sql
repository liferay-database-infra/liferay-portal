create index IX_3D99B72E on AnalyticsAssociation (companyId, associationClassName[$COLUMN_LENGTH:75$], associationClassPK);
create index IX_CB1FDDD3 on AnalyticsAssociation (companyId, associationClassName[$COLUMN_LENGTH:75$], modifiedDate);

create index IX_3BF42B97 on AnalyticsDeleteMessage (companyId, modifiedDate);

create index IX_3A69CC81 on AnalyticsMessage (companyId);