package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartServices;

public class DepartListController implements Initializable {
    
	private DepartServices service;
	
	@FXML
	private TableView<Department> tableViewDepart;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event); // pegando a refer�ncia do stage atual
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartForm.fxml", parentStage);
	}
    
	private ObservableList<Department> obs;
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		
		List<Department> list = service.findAll();
		obs = FXCollections.observableArrayList(list);
		tableViewDepart.setItems(obs);
	}
	
	public void setDepartService(DepartServices service) { // invers�o de controle, injetando depend�ncia
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepart.prefHeightProperty().bind(stage.heightProperty());
	}
	
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			DepartFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.updateFormDate();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter department data"); // seta o titulo da janela
			dialogStage.setScene(new Scene(pane)); // setando a cena que ser� aberta
			dialogStage.setResizable(false); // esse m�todo diz que a janela pode ou n�o ser redimensionada
			dialogStage.initOwner(parentStage); // m�todo que diz quem � o stage pai da janela
			dialogStage.initModality(Modality.WINDOW_MODAL); // m�todo que faz com que a tela anterior n�o possa ser acessada enquanto a janela n�o for fechada
		    dialogStage.showAndWait();
		} 
		catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}