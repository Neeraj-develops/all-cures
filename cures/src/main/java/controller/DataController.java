package controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dao.DataDaoImpl;

@RestController
@RequestMapping(path = "/data")
public class DataController {

	@Autowired
	private DataDaoImpl dataDaoImpl;

	

	@RequestMapping(value = "/create", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody int addDataDetails(@RequestBody HashMap dataMasterMap) {
		return dataDaoImpl.addDataDetails(dataMasterMap);
	}
	
	@RequestMapping(value = "/categories", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List Categories() {
		return dataDaoImpl.viewCategories();
	}
	
	@RequestMapping(value = "/medicines", produces = "application/json", method = RequestMethod.GET)
	public @ResponseBody List Medicines() {
		return dataDaoImpl.viewMedicines();
	}
	
	@RequestMapping(value = "/deactivate/{usr_id}", produces = "application/json", method = RequestMethod.POST)
	public @ResponseBody int delete_update(@PathVariable Integer usr_id, HttpServletRequest request,@RequestParam(required = false) String reason) throws SQLException {
		
		System.out.println("Request to deactivate");
		return DeleteDaoImpl.Delete_Update(usr_id,reason);
	
		
	}

}
