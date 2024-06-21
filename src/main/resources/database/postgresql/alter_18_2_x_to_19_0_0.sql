-- Export
create table o_ex_export_metadata (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   e_archive_type varchar(32),
   e_title varchar(255),
   e_description varchar(4000),
   e_file_name varchar(255),
   e_file_path varchar(1024),
   e_only_administrators bool default false,
   e_expiration_date timestamp,
   fk_entry int8,
   e_sub_ident varchar(2048),
   fk_task int8,
   fk_creator int8,
   fk_metadata int8,
   primary key (id)
);

alter table o_ex_export_metadata add constraint export_to_entry_idx foreign key (fk_entry) references o_repositoryentry (repositoryentry_id);
create index idx_export_to_entry_idx on o_ex_export_metadata(fk_entry);
alter table o_ex_export_metadata add constraint export_to_creator_idx foreign key (fk_creator) references o_bs_identity (id);
create index idx_export_to_creator_idx on o_ex_export_metadata(fk_creator);
alter table o_ex_export_metadata add constraint export_to_task_idx foreign key (fk_task) references o_ex_task (id);
create index idx_export_to_task_idx on o_ex_export_metadata(fk_task);
create index idx_export_sub_ident_idx on o_ex_export_metadata(e_sub_ident);
alter table o_ex_export_metadata add constraint export_to_vfsdata_idx foreign key (fk_metadata) references o_vfs_metadata(id);
create index idx_export_to_vfsdata_idx on o_ex_export_metadata(fk_metadata);

-- Content Editor
alter table o_ce_page_part add column p_storage_path varchar(255);

-- Identity
alter table o_bs_identity add column plannedinactivationdate timestamp;
alter table o_bs_identity add column planneddeletiondate timestamp;

-- VFS
alter table o_vfs_metadata add column f_deleted_date timestamp;
alter table o_vfs_metadata add column fk_deleted_by int8;

-- Media to Page Part (Content Editor)
create table o_media_to_page_part (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   pos int8 default null,
   fk_media int8 not null,
   fk_media_version int8 default null,
   fk_identity int8 default null,
   fk_page_part int8 not null,
   primary key (id)
);

alter table o_media_to_page_part add constraint media_to_page_part_media_idx foreign key (fk_media) references o_media (id);
create index idx_media_to_page_part_media_idx on o_media_to_page_part (fk_media);
alter table o_media_to_page_part add constraint media_to_page_part_media_version_idx foreign key (fk_media_version) references o_media_version (id);
create index idx_media_to_page_part_media_version_idx on o_media_to_page_part (fk_media_version);
alter table o_media_to_page_part add constraint media_to_page_part_identity_idx foreign key (fk_identity) references o_bs_identity (id);
create index idx_media_to_page_part_identity_idx on o_media_to_page_part (fk_identity);
alter table o_media_to_page_part add constraint media_to_page_part_page_part_idx foreign key (fk_page_part) references o_ce_page_part (id);
create index idx_media_to_page_part_page_part_idx on o_media_to_page_part (fk_page_part);

-- Reminder
alter table o_rem_reminder add r_email_copy_only bool default false;

-- Peer review
alter table o_gta_task add column g_peerreview_due_date timestamp;

alter table o_gta_task add column fk_survey int8;

alter table o_gta_task add constraint gtask_survey_idx foreign key (fk_survey) references o_eva_form_survey (id);
create index idx_gtask_survey_idx on o_gta_task(fk_survey);

create table o_gta_review_assignment (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   g_assigned bool not null default true,
   g_status varchar(32) not null default 'open',
   g_rating decimal default null,
   fk_task int8 not null,
   fk_assignee int8 not null,
   fk_participation int8,
   primary key (id)
);

alter table o_gta_review_assignment add constraint assignment_to_gtask_idx foreign key (fk_task) references o_gta_task (id);
create index idx_assignment_to_gtask_idx on o_gta_review_assignment(fk_task);

alter table o_gta_review_assignment add constraint assignee_to_gtask_idx foreign key (fk_assignee) references o_bs_identity (id);
create index idx_assignee_to_gtask_idx on o_gta_review_assignment(fk_assignee);

alter table o_gta_review_assignment add constraint assignment_to_fpart_idx foreign key (fk_participation) references o_eva_form_participation (id);
create index idx_assignment_to_fpart_idx on o_gta_review_assignment(fk_participation);

-- Open Badges
create table o_badge_organization (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   b_type varchar(64) not null,
   b_organization_key varchar(80) not null,
   b_organization_value text not null,
   primary key (id)
);

alter table o_badge_class add fk_badge_organization int8;
alter table o_badge_class add constraint badge_class_organization_idx foreign key (fk_badge_organization) references o_badge_organization (id);
create index idx_badge_class_organization_idx on o_badge_class(fk_badge_organization);
