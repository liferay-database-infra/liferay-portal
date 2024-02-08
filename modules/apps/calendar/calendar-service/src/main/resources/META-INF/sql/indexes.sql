create index IX_97FC174E on Calendar (groupId, calendarResourceId, defaultCalendar);
create index IX_4DAD837B on Calendar (uuid_[$COLUMN_LENGTH:75$]);

create unique index IX_455DB3EB on CalendarBooking (calendarId, ctCollectionId, vEventUid[$COLUMN_LENGTH:255$]);
create unique index IX_BD5AA0AC on CalendarBooking (calendarId, parentCalendarBookingId, ctCollectionId);
create index IX_470170B4 on CalendarBooking (calendarId, status);
create index IX_B198FFC on CalendarBooking (calendarResourceId);
create index IX_F7B8A941 on CalendarBooking (parentCalendarBookingId, status);
create index IX_14ADC52E on CalendarBooking (recurringCalendarBookingId);
create index IX_F4879CDE on CalendarBooking (uuid_[$COLUMN_LENGTH:75$]);

create index IX_3E51EA98 on CalendarNotificationTemplate (calendarId, notificationType[$COLUMN_LENGTH:75$], notificationTemplateType[$COLUMN_LENGTH:75$]);
create index IX_E2BC09F6 on CalendarNotificationTemplate (uuid_[$COLUMN_LENGTH:75$]);

create index IX_A8D2FA92 on CalendarResource (active_, code_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_CD46CB85 on CalendarResource (classNameId, classPK, ctCollectionId);
create index IX_40678371 on CalendarResource (groupId, active_);
create index IX_10D56595 on CalendarResource (groupId, code_[$COLUMN_LENGTH:75$]);
create index IX_681CB00D on CalendarResource (uuid_[$COLUMN_LENGTH:75$]);