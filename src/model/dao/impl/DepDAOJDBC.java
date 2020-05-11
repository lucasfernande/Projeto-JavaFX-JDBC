package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepDAOJDBC implements DepartmentDAO {

	private Connection conn;

	public DepDAOJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("INSERT INTO department "
		                + "(Name) " 
					    + "VALUES "
		                + "(?) ",
					    Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, obj.getName());

			int rows = ps.executeUpdate();

			if (rows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected");
			}

		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(
				"UPDATE department " +
				"SET Name = ? " +
				"WHERE Id = ?");

			ps.setString(1, obj.getName());
			ps.setInt(2, obj.getId());

			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
        
		try {
			
			ps = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
			
			ps.setInt(1, id);
			
			ps.executeUpdate();
			
		}
		catch (SQLException e){
			throw new DbIntegrityException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
		}
		
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement("SELECT * FROM department WHERE Id = ?");
			
			ps.setInt(1, id);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				Department obj = new Department();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				return obj;
			}
			
			return null;
		}
		catch (SQLException e){
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement("SELECT * FROM department ORDER BY Id");
			
			rs = ps.executeQuery();
			
			List<Department> list = new ArrayList<>();

			while (rs.next()) {
				Department obj = new Department();
				obj.setId(rs.getInt("Id"));
				obj.setName(rs.getString("Name"));
				list.add(obj);
			}
			return list;
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
		    DB.closeStatement(ps);
		    DB.closeResultSet(rs);
		}
	}

}
