create index IX_98365DA4 on CommerceWishList (groupId);
create index IX_6680B6BE on CommerceWishList (userId, createDate);
create index IX_3CBFC78C on CommerceWishList (userId, groupId, defaultWishList);
create index IX_93180C19 on CommerceWishList (uuid_[$COLUMN_LENGTH:75$]);

create index IX_53542BBF on CommerceWishListItem (CPInstanceUuid[$COLUMN_LENGTH:75$], CProductId, commerceWishListId);