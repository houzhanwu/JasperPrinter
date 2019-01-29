package org.ltd3000.jasperprinter.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T> {
    public abstract T mapRow(ResultSet rs, int index) throws SQLException;
}