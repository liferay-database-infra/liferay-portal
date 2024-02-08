create index IX_CDE7B2CD on ObjectAction (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_9600B8B2 on ObjectAction (objectDefinitionId, active_, name[$COLUMN_LENGTH:75$], objectActionTriggerKey[$COLUMN_LENGTH:75$]);
create index IX_E2D85DC8 on ObjectAction (objectDefinitionId, active_, objectActionTriggerKey[$COLUMN_LENGTH:75$]);
create index IX_E146A86 on ObjectAction (objectDefinitionId, name[$COLUMN_LENGTH:75$]);
create index IX_BF9AF7C4 on ObjectAction (uuid_[$COLUMN_LENGTH:75$]);

create index IX_2B2CA94C on ObjectDefinition (accountEntryRestricted);
create index IX_AE0D0978 on ObjectDefinition (companyId, className[$COLUMN_LENGTH:255$]);
create index IX_967BFBFA on ObjectDefinition (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_7D686D13 on ObjectDefinition (companyId, status, active_);
create index IX_12BECBE8 on ObjectDefinition (companyId, system_, modifiable);
create index IX_F8B95773 on ObjectDefinition (companyId, system_, status, active_);
create index IX_46266E50 on ObjectDefinition (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_8D232754 on ObjectDefinition (objectFolderId);
create index IX_55C39BCE on ObjectDefinition (system_, status);
create index IX_F34F1947 on ObjectDefinition (uuid_[$COLUMN_LENGTH:75$]);

create index IX_5F0D0DAF on ObjectEntry (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_D814E30A on ObjectEntry (objectDefinitionId, status, groupId);
create index IX_68B7FB2 on ObjectEntry (objectDefinitionId, userId, createDate);
create index IX_714656A6 on ObjectEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_F87BB227 on ObjectField (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_6DCE835D on ObjectField (listTypeDefinitionId, state_);
create index IX_85FBF974 on ObjectField (objectDefinitionId, dbTableName[$COLUMN_LENGTH:75$]);
create index IX_4B384896 on ObjectField (objectDefinitionId, indexed, dbType[$COLUMN_LENGTH:75$]);
create index IX_2D0537E9 on ObjectField (objectDefinitionId, localized);
create index IX_EAB4F4EC on ObjectField (objectDefinitionId, name[$COLUMN_LENGTH:75$]);
create index IX_4A69C63E on ObjectField (objectDefinitionId, system_);
create index IX_B2BCEB1E on ObjectField (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_5A3C0A35 on ObjectFieldSetting (objectFieldId, name[$COLUMN_LENGTH:75$]);
create index IX_9FF59944 on ObjectFieldSetting (uuid_[$COLUMN_LENGTH:75$]);

create index IX_B3C95F49 on ObjectFilter (objectFieldId);
create index IX_4D8509C2 on ObjectFilter (uuid_[$COLUMN_LENGTH:75$]);

create index IX_7F1EF4FF on ObjectFolder (companyId, name[$COLUMN_LENGTH:75$]);
create index IX_A569CF95 on ObjectFolder (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_393FA48C on ObjectFolder (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_5BE8DA03 on ObjectFolderItem (objectFolderId, objectDefinitionId);
create index IX_F0CED4B9 on ObjectFolderItem (uuid_[$COLUMN_LENGTH:75$]);

create index IX_FD0CCE8A on ObjectLayout (objectDefinitionId, defaultObjectLayout);
create index IX_D8848F50 on ObjectLayout (uuid_[$COLUMN_LENGTH:75$]);

create index IX_5F97F7CF on ObjectLayoutBox (objectLayoutTabId);
create index IX_ABDD8BB7 on ObjectLayoutBox (uuid_[$COLUMN_LENGTH:75$]);

create index IX_E992BFE1 on ObjectLayoutColumn (objectFieldId);
create index IX_46CE5537 on ObjectLayoutColumn (objectLayoutRowId);
create index IX_DE0A465A on ObjectLayoutColumn (uuid_[$COLUMN_LENGTH:75$]);

create index IX_FA14DE56 on ObjectLayoutRow (objectLayoutBoxId);
create index IX_2E34DE08 on ObjectLayoutRow (uuid_[$COLUMN_LENGTH:75$]);

create index IX_F01F1EEA on ObjectLayoutTab (objectLayoutId);
create index IX_4CC508B8 on ObjectLayoutTab (objectRelationshipId);
create index IX_BB0562D on ObjectLayoutTab (uuid_[$COLUMN_LENGTH:75$]);

create index IX_6240F94B on ObjectRelationship (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_97E37468 on ObjectRelationship (objectDefinitionId1, edge);
create index IX_468CF4A1 on ObjectRelationship (objectDefinitionId1, name[$COLUMN_LENGTH:75$]);
create index IX_FA1DF4EC on ObjectRelationship (objectDefinitionId1, objectDefinitionId2, type_[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:75$]);
create index IX_F8100582 on ObjectRelationship (objectDefinitionId1, reverse, deletionType[$COLUMN_LENGTH:75$]);
create index IX_297E8F0 on ObjectRelationship (objectDefinitionId1, reverse, objectDefinitionId2, type_[$COLUMN_LENGTH:75$], name[$COLUMN_LENGTH:75$]);
create index IX_5F788225 on ObjectRelationship (objectDefinitionId1, reverse, type_[$COLUMN_LENGTH:75$]);
create index IX_DE3EBEF8 on ObjectRelationship (objectDefinitionId2);
create index IX_F1DC092D on ObjectRelationship (objectFieldId2);
create index IX_820C98BE on ObjectRelationship (parameterObjectFieldId);
create index IX_EFBB2E21 on ObjectRelationship (reverse, dbTableName[$COLUMN_LENGTH:75$]);
create index IX_EB132500 on ObjectRelationship (reverse, objectDefinitionId2, type_[$COLUMN_LENGTH:75$]);
create index IX_909CFA42 on ObjectRelationship (uuid_[$COLUMN_LENGTH:75$]);

create index IX_D8444F12 on ObjectState (objectStateFlowId, listTypeEntryId);
create index IX_BCD0E2E7 on ObjectState (uuid_[$COLUMN_LENGTH:75$]);

create index IX_AE828160 on ObjectStateFlow (objectFieldId);
create index IX_CF60C159 on ObjectStateFlow (uuid_[$COLUMN_LENGTH:75$]);

create index IX_DB56B27E on ObjectStateTransition (objectStateFlowId);
create index IX_9C3FAB55 on ObjectStateTransition (sourceObjectStateId);
create index IX_FB9AC71F on ObjectStateTransition (targetObjectStateId);
create index IX_C0598012 on ObjectStateTransition (uuid_[$COLUMN_LENGTH:75$]);

create index IX_129E6F4E on ObjectValidationRule (externalReferenceCode[$COLUMN_LENGTH:75$]);
create index IX_C476B36E on ObjectValidationRule (objectDefinitionId, active_);
create index IX_F3C1F4A on ObjectValidationRule (objectDefinitionId, engine[$COLUMN_LENGTH:255$]);
create index IX_31777DF5 on ObjectValidationRule (objectDefinitionId, outputType[$COLUMN_LENGTH:75$]);
create index IX_6234D645 on ObjectValidationRule (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_D4C96E23 on ObjectValidationRuleSetting (name[$COLUMN_LENGTH:75$], value[$COLUMN_LENGTH:75$], objectValidationRuleId);
create index IX_741D243D on ObjectValidationRuleSetting (uuid_[$COLUMN_LENGTH:75$]);

create index IX_6AF6C9EA on ObjectView (objectDefinitionId, defaultObjectView);
create index IX_911FB9F5 on ObjectView (uuid_[$COLUMN_LENGTH:75$]);

create index IX_43762B4E on ObjectViewColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_5482213F on ObjectViewColumn (uuid_[$COLUMN_LENGTH:75$]);

create index IX_C6503FB6 on ObjectViewFilterColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_E060EDA7 on ObjectViewFilterColumn (uuid_[$COLUMN_LENGTH:75$]);

create index IX_846D44D0 on ObjectViewSortColumn (objectViewId, objectFieldName[$COLUMN_LENGTH:75$]);
create index IX_CC3F20C1 on ObjectViewSortColumn (uuid_[$COLUMN_LENGTH:75$]);