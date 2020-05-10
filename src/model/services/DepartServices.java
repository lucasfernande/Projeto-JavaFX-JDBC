package model.services;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartServices {
    
	private DepartmentDAO dao = DAOFactory.createDepartmentDAO();
	
	public List<Department> findAll(){
	     return dao.findAll();
	}
	
}
