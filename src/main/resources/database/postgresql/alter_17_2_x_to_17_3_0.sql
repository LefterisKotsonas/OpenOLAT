
-- Projects
create table o_proj_project (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   p_external_ref varchar(128),
   p_status varchar(32),
   p_title varchar(128),
   p_teaser varchar(150),
   p_description text,
   fk_creator int8 not null,
   fk_group int8 not null,
   primary key (id)
);
create table o_proj_project_to_org (
   id bigserial,
   creationdate timestamp not null,
   fk_project int8 not null,
   fk_organisation int8 not null,
   primary key (id)
);
create table o_proj_project_user_info (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   p_last_visit_date timestamp,
   fk_project int8 not null,
   fk_identity int8 not null,
   primary key (id)
);
create table o_proj_artefact (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   p_type varchar(32),
   p_content_modified_date timestamp not null,
   fk_content_modified_by int8 not null,
   p_status varchar(32),
   fk_project int8 not null,
   fk_creator int8 not null,
   fk_group int8 not null,
   primary key (id)
);
create table o_proj_artefact_to_artefact (
   id bigserial,
   creationdate timestamp not null,
   fk_artefact1 int8 not null,
   fk_artefact2 int8 not null,
   fk_project int8 not null,
   fk_creator int8 not null,
   primary key (id)
);
create table o_proj_file (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   fk_metadata int8 not null,
   fk_artefact int8 not null,
   primary key (id)
);
create table o_proj_note (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   p_title varchar(128),
   p_text text,
   fk_artefact int8 not null,
   primary key (id)
);
create table o_proj_appointment (
   id bigserial,
   creationdate timestamp not null,
   lastmodified timestamp not null,
   p_identifier varchar(64) not null,
   p_event_id varchar(64) not null,
   p_recurrence_id varchar(500),
   p_start_date timestamp,
   p_end_date timestamp,
   p_subject varchar(256),
   p_description text,
   p_location varchar(1024),
   p_color varchar(50),
   p_all_day bool default false not null,
   p_recurrence_rule varchar(100),
   p_recurrence_exclusion varchar(4000),
   fk_artefact int8 not null,
   primary key (id)
);
create table o_proj_activity (
   id bigserial,
   creationdate timestamp not null,
   p_action varchar(32) not null,
   p_action_target varchar(32) not null,
   p_before text,
   p_after text,
   p_temp_identifier varchar(100),
   fk_doer int8 not null,
   fk_project int8 not null,
   fk_artefact int8,
   fk_artefact_reference int8,
   fk_member int8,
   fk_organisation int8,
   primary key (id)
);

alter table o_proj_project add constraint project_creator_idx foreign key (fk_creator) references o_bs_identity(id);
create index idx_proj_project_creator_idx on o_proj_project (fk_creator);
alter table o_proj_project add constraint project_to_group_idx foreign key (fk_group) references o_bs_group (id);
create index idx_proj_project_group_idx on o_proj_project (fk_group);
alter table o_proj_project_to_org add constraint rel_pto_project_idx foreign key (fk_project) references o_proj_project(id);
create index idx_rel_pto_project_idx on o_proj_project_to_org (fk_project);
alter table o_proj_project_to_org add constraint rel_pto_org_idx foreign key (fk_organisation) references o_org_organisation(id);
create index idx_rel_pto_org_idx on o_proj_project_to_org (fk_organisation);
alter table o_proj_project_user_info add constraint rel_pui_project_idx foreign key (fk_project) references o_proj_project(id);
create index idx_rel_pui_project_idx on o_proj_project_user_info (fk_project);
alter table o_proj_project_user_info add constraint rel_pui_idenity_idx foreign key (fk_identity) references o_bs_identity(id);
create index idx_rel_pui_identity_idx on o_proj_project_user_info (fk_identity);

alter table o_proj_artefact add constraint artefact_modby_idx foreign key (fk_content_modified_by) references o_bs_identity(id);
create index idx_artefact_modby_idx on o_proj_artefact (fk_content_modified_by);
alter table o_proj_artefact add constraint artefact_project_idx foreign key (fk_project) references o_proj_project(id);
create index idx_artefact_project_idx on o_proj_artefact (fk_project);
alter table o_proj_artefact add constraint artefact_creator_idx foreign key (fk_creator) references o_bs_identity(id);
create index idx_artefact_creator_idx on o_proj_artefact (fk_creator);
alter table o_proj_artefact add constraint artefact_group_idx foreign key (fk_group) references o_bs_group (id);
create index idx_artefact_group_idx on o_proj_artefact (fk_group);

alter table o_proj_artefact_to_artefact add constraint projata_artefact1_idx foreign key (fk_artefact1) references o_proj_artefact(id);
create index idx_projata_artefact1_idx on o_proj_artefact_to_artefact (fk_artefact1);
alter table o_proj_artefact_to_artefact add constraint projata_artefact2_idx foreign key (fk_artefact2) references o_proj_artefact(id);
create index idx_projata_artefact2_idx on o_proj_artefact_to_artefact (fk_artefact2);
alter table o_proj_artefact_to_artefact add constraint projata_project_idx foreign key (fk_project) references o_proj_project(id);
create index idx_projata_project_idx on o_proj_artefact_to_artefact (fk_project);
alter table o_proj_artefact_to_artefact add constraint projata_creator_idx foreign key (fk_creator) references o_bs_identity(id);
create index idx_projata_creator_idx on o_proj_artefact_to_artefact (fk_creator);

alter table o_proj_file add constraint file_artefact_idx foreign key (fk_artefact) references o_proj_artefact(id);
create index idx_file_artefact_idx on o_proj_file (fk_artefact);
alter table o_proj_file add constraint file_metadata_idx foreign key (fk_metadata) references o_vfs_metadata(id);
create index idx_file_metadata_idx on o_proj_file (fk_metadata);

alter table o_proj_note add constraint note_artefact_idx foreign key (fk_artefact) references o_proj_artefact(id);
create index idx_note_artefact_idx on o_proj_file (fk_artefact);

alter table o_proj_appointment add constraint appointment_artefact_idx foreign key (fk_artefact) references o_proj_artefact(id);
create index idx_appointment_artefact_idx on o_proj_file (fk_artefact);
create unique index idx_appointment_ident_idx on o_proj_appointment (p_identifier);

alter table o_proj_activity add constraint activity_doer_idx foreign key (fk_doer) references o_bs_identity(id);
create index idx_activity_doer_idx on o_proj_activity (fk_doer);
alter table o_proj_activity add constraint activity_project_idx foreign key (fk_project) references o_proj_project(id);
create index idx_activity_project_idx on o_proj_activity (fk_project);
alter table o_proj_activity add constraint activity_artefact_idx foreign key (fk_artefact) references o_proj_artefact(id);
create index idx_activity_artefact_idx on o_proj_activity (fk_artefact);
alter table o_proj_activity add constraint activity_artefact_reference_idx foreign key (fk_artefact_reference) references o_proj_artefact(id);
create index idx_activity_artefact_reference_idx on o_proj_activity (fk_artefact_reference);
alter table o_proj_activity add constraint activity_member_idx foreign key (fk_member) references o_bs_identity(id);
create index idx_activity_member_idx on o_proj_activity (fk_member);
alter table o_proj_activity add constraint activity_organisation_idx foreign key (fk_organisation) references o_org_organisation (id);
create index idx_activity_organisation_idx on o_proj_activity (fk_organisation);
create index idx_activity_temp_ident_idx on o_proj_activity (p_temp_identifier);

