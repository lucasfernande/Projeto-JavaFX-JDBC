package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartServices;
import model.services.SellerServices;

public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerServices service;
	private DepartServices depService; // vendedor est� associado a um departamento
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker txtBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepart;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

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
		} catch (DbException e) {
			Alerts.showAlert("Error saving seller", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getError());
		}
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerServices service, DepartServices depService) {
		this.service = service;
		this.depService = depService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			txtBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if (entity.getDepart() == null) { // se o departamento for nulo, significa que um novo vendedor est� sendo
											// cadastrado
			comboBoxDepart.getSelectionModel().selectFirst(); // m�todo para exibir o primeiro item do combobox
		} else {
			comboBoxDepart.setValue(entity.getDepart());
		}
	}

	public void loadAssociatedObject() {
		if (depService == null) {
			throw new IllegalStateException("Department service was null");
		}
		List<Department> list = depService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepart.setItems(obsList);
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() { // pega os dados que foram preenchidos no formul�rio e carrega um objeto com
									// esses dados
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(txtId.getText())); // pegando o id do textField

		if (txtName.getText() == null || txtName.getText().trim().equals("")) { // o trim() remove qualquer espa�o em branco do in�cio e do final
			exception.addError("name", "Field can't be empty"); // caso o if seja verdadeiro, significa que o campo est� vazio													 
		}
		obj.setName(txtName.getText()); // pegando o nome do textField
 		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) { 
			exception.addError("email", "Field can't be empty"); 
		}
		obj.setEmail(txtEmail.getText());
		
		if (txtBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty"); 
		}
		else {
			Instant instant = Instant.from(txtBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			// o atStartOfDay() converte a data escolhida no hor�rio do computador do usu�rio para instant, que � independente
			
			obj.setBirthDate(Date.from(instant));
			// o setBirthDate espera um valor do tipo Date, assim sendo necess�rio converter o instant para Date
		}
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) { 
			exception.addError("baseSalary", "Field can't be empty"); 
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepart(comboBoxDepart.getValue());
		
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
		Constraints.setTextFieldInteger(txtId); // o campo id s� pode ter n�meros inteiros
		Constraints.setTextFieldMaxLength(txtName, 75); // colocando um limite de 30 caracteres no nome do departamento
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 75);
		Utils.formatDatePicker(txtBirthDate, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
		
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		
		labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
		
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));

	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepart.setCellFactory(factory);
		comboBoxDepart.setButtonCell(factory.call(null));
	}
}
