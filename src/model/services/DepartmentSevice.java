package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentSevice {
	
	public List<Department> findAll() {
		
		List<Department> list = new ArrayList<>();
		list.add(new Department(1,"books"));
		list.add(new Department(2,"music"));
		list.add(new Department(3,"food"));
		return list;
		
	}

}
