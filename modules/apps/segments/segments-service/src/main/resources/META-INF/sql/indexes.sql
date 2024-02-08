create index IX_F6225631 on SegmentsEntry (active_);
create index IX_175FC150 on SegmentsEntry (companyId);
create index IX_2E0C3F77 on SegmentsEntry (groupId, active_);
create unique index IX_3803A1C on SegmentsEntry (groupId, segmentsEntryKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_F548A60C on SegmentsEntry (groupId, source[$COLUMN_LENGTH:75$]);
create index IX_CD749112 on SegmentsEntry (source[$COLUMN_LENGTH:75$]);
create index IX_3BC41AC7 on SegmentsEntry (uuid_[$COLUMN_LENGTH:75$]);

create index IX_64CBABA8 on SegmentsEntryRel (classNameId, classPK, groupId);
create unique index IX_E418FCB9 on SegmentsEntryRel (classNameId, classPK, segmentsEntryId, ctCollectionId);
create index IX_AB286250 on SegmentsEntryRel (segmentsEntryId);

create unique index IX_A38DCAC8 on SegmentsEntryRole (roleId, segmentsEntryId, ctCollectionId);

create index IX_EBCFE1C4 on SegmentsExperience (groupId, plid, active_);
create unique index IX_6E29AF1B on SegmentsExperience (groupId, plid, ctCollectionId, priority);
create unique index IX_8FC2FC8D on SegmentsExperience (groupId, plid, ctCollectionId, segmentsExperienceKey[$COLUMN_LENGTH:75$]);
create index IX_3A0FEF1 on SegmentsExperience (groupId, plid, segmentsEntryId, active_);
create index IX_E90B4ACD on SegmentsExperience (segmentsEntryId);
create index IX_B684890F on SegmentsExperience (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_4516B4A9 on SegmentsExperiment (groupId, ctCollectionId, segmentsExperienceId, plid);
create unique index IX_CC27B4D4 on SegmentsExperiment (groupId, segmentsExperimentKey[$COLUMN_LENGTH:75$], ctCollectionId);
create index IX_F17B83A on SegmentsExperiment (segmentsExperimentKey[$COLUMN_LENGTH:75$]);
create index IX_CBAED35C on SegmentsExperiment (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_3CCCDB25 on SegmentsExperimentRel (segmentsExperienceId, segmentsExperimentId, ctCollectionId);