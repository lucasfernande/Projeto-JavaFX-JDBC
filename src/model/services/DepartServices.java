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
	
	public void saveOrUpdate(Department obj) {
		if (obj.getId() == null) { // se for id nulo, significa que será inserido um novo departamento
			dao.insert(obj);
		}
		else {
			dao.update(obj); // caso seja diferente de nulo, ele irá apenas atualizar
		}
	}
	
	public void remove(Department obj) {
		dao.deleteById(obj.getId());
	}
}
