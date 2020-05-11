package model.services;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.SellerDAO;
import model.entities.Seller;

public class SellerServices {
    
	private SellerDAO dao = DAOFactory.createSellerDAO();
	
	public List<Seller> findAll(){
	     return dao.findAll();
	}
	
	public void saveOrUpdate(Seller obj) {
		if (obj.getId() == null) { // se for id nulo, significa que será inserido um novo departamento
			dao.insert(obj);
		}
		else {
			dao.update(obj); // caso seja diferente de nulo, ele irá apenas atualizar
		}
	}
	
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
	}
}
