package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartServices;

public class DepartListController implements Initializable, DataChangeListener {

	private DepartServices service;

	@FXML
	private TableView<Department> tableViewDepart;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	private Button btNew;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event); // pegando a referência do stage atual
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
		
		initEditButtons();
	}

	public void setDepartService(DepartServices service) { // inversão de controle, injetando dependência
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
			controller.setDepartService(new DepartServices());
			controller.subscribeDataChangeListener(this);
			controller.updateFormDate();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter department data"); // seta o titulo da janela
			dialogStage.setScene(new Scene(pane)); // setando a cena que será aberta
			dialogStage.setResizable(false); // esse método diz que a janela pode ou não ser redimensionada
			dialogStage.initOwner(parentStage); // método que diz quem é o stage pai da janela
			dialogStage.initModality(Modality.WINDOW_MODAL); // método que faz com que a tela anterior não possa ser
															// acessada enquanto a janela não for fechada
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DepartForm.fxml", Utils.currentStage(event)));
			}
		});
	}
}