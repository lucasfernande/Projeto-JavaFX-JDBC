package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartServices;

public class DepartFormController implements Initializable {
    
	private Department entity;
    private DepartServices service;	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelError;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		try {
		entity = getFormData();
		service.saveOrUpdate(entity);
		notifyDataChangeListeners();
		Utils.currentStage(event).close();
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving department", null, e.getMessage(), AlertType.ERROR);
		}
		catch (ValidationException e) {
			setErrorMessages(e.getError());
		}
	}
	
	public void setDepartment (Department entity) {
		this.entity = entity;
	}
	
	public void setDepartService (DepartServices service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener (DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	public void updateFormDate() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() {
         Department obj = new Department();
         
         ValidationException exception = new ValidationException("Validation error");
         
         obj.setId(Utils.tryParseToInt(txtId.getText())); // pegando o id do textField
         
         if (txtName.getText() == null || txtName.getText().trim().equals("")) { // o trim() remove qualquer espaço em branco do início e do final
             exception.addError("name", "Field can't be empty"); // caso o if seja verdadeiro, significa que o campo está vazio
         }
         
         obj.setName(txtName.getText()); // pegando o nome do textField
         
         if (exception.getError().size() > 0) { // testando se existe pelo menos um erro
        	 throw exception;
         }
         
         return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
    
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId); // o campo id só pode ter números inteiros
		Constraints.setTextFieldMaxLength(txtName, 30); // colocando um limite de 30 caracteres no nome do departamento
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) { // testando se o erro de campo vazio existe
			labelError.setText(errors.get("name")); // setando a mensagem de erro correspondente a chave "name" no labelerror da tela de cadastro
		}
	}
}
