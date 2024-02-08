create unique index IX_14ADB3BA on BatchPlannerMapping (batchPlannerPlanId, externalFieldName[$COLUMN_LENGTH:75$], internalFieldName[$COLUMN_LENGTH:75$]);

create index IX_C5ED2A8B on BatchPlannerPlan (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_67393877 on BatchPlannerPlan (companyId, template, export);
create index IX_874FA8DB on BatchPlannerPlan (companyId, userId);

create unique index IX_F3C010A on BatchPlannerPolicy (batchPlannerPlanId, name[$COLUMN_LENGTH:75$]);