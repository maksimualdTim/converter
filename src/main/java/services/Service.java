package services;

import java.sql.SQLException;
import java.util.List;

import exceptions.AlreadyExistsException;

abstract class Service<T> {
    protected boolean isUniqueConstraintViolation(SQLException e) {
        return "23000".equals(e.getSQLState()) && e.getErrorCode() == 1062;
    }
    public abstract T create(T model) throws SQLException, AlreadyExistsException;
    
    public abstract List<T> findAll() throws SQLException;
}
