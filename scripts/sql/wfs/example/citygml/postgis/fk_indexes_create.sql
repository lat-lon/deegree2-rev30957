--SQL> select 'create index ' || TABLE_NAME || '_' || COLUMN_NAME || '_IDX on table ' || TABLE_NAME || '(' || COLUMN_NAME || ');' from all_tab_columns where owner = 'IKG3DLAERMDB' and COLUMN_NAME like 'FK_%' and TABLE_NAME not like 'BIN%' order by TABLE_NAME, COLUMN_NAME;
create index ADDRESS_FKFEATURE_IDX on ADDRESS(FK_FEATURE);
create index BUILDING_FKFEATURE_IDX on BUILDING(FK_FEATURE);
create index CityFurniture_FKFEATURE_IDX on CityFurniture(FK_FEATURE);
create index DATE_ATTRIBUTE_FKFEATURE_IDX on DATE_ATTRIBUTE(FK_FEATURE);
create index EXTERNALREF_FKFEATURE_IDX on EXTERNALREF(FK_FEATURE);
create index FLOAT_ATTRIBUTE_FKFEATURE_IDX on FLOAT_ATTRIBUTE(FK_FEATURE);
create index INT_ATTRIBUTE_FKFEATURE_IDX on INT_ATTRIBUTE(FK_FEATURE);
create index INNER_BOUNDARY_fksolid_IDX on INNER_BOUNDARY(fk_solid);
create index INNER_BOUNDARY_fkpoly_IDX on INNER_BOUNDARY(fk_polygon);
create index JT_LFG_LFG_fksource_IDX on JT_LFG_LFG(fk_source);
create index JT_LFG_LFG_fktarget_IDX on JT_LFG_LFG(fk_target);
create index LINK_FEAT_FEAT_fktarget_IDX on LINK_FEAT_FEAT(fk_target);
create index LINK_FEAT_GEOM_fkfeature_IDX on LINK_FEAT_GEOM(fk_feature);
create index POLYGON_fkmaterial_IDX on POLYGON(fk_material);
create index POLYGON_fktexture_IDX on POLYGON(fk_texture);
create index POLYGON_fkgeometry_IDX on POLYGON(fk_geometry);
create index POLYGON_FKSOLID_EX_IDX on POLYGON(FK_SOLID_EX);
create index POLYGON_FKSOLID_IN_IDX on POLYGON(FK_SOLID_IN);
create index Railway_FKFEATURE_IDX on Railway(FK_FEATURE);
create index Road_FKFEATURE_IDX on Road(FK_FEATURE);
create index SOLID_FKGEOMETRY_IDX on Solid(FK_GEOMETRY);
create index TEXT_ATTRIBUTE_FKFEATURE_IDX on TEXT_ATTRIBUTE(FK_FEATURE);
create index TraficArea_FKFEATURE_IDX on TraficArea(FK_FEATURE);
create index URI_ATTRIBUTE_FKFEATURE_IDX on URI_ATTRIBUTE(FK_FEATURE);
create index WPVSData_FKFEATURE_IDX on WPVSData(FK_FEATURE);

