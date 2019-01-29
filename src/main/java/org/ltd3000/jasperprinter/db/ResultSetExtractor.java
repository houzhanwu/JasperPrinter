package org.ltd3000.jasperprinter.db;

import java.sql.ResultSet;

public interface ResultSetExtractor<T> {
    
    public abstract T extractData(ResultSet rs);

}