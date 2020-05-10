package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartServices {

	public List<Department> findAll(){
		List<Department> list = new ArrayList<>(); // lista de departamentos apenas para teste, ainda não é do banco de dados
	    list.add(new Department(1, "Books"));
	    list.add(new Department(2, "Electronics"));
	    list.add(new Department(3, "Computers"));
	    return list;
	}
	
}
