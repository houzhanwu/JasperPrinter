DROP TABLE IF EXISTS authorize;
DROP TABLE IF EXISTS cogfig;
DROP TABLE IF EXISTS jaspercount;
create table authorize(id INTEGER PRIMARY KEY AUTOINCREMENT  ,item varchar(20),value varchar(30));
