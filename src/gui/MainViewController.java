package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepart;
	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction"); // teste de funcionamento
	}

	@FXML
	public void onMenuItemDepartAction() {
		System.out.println("onMenuItemDepartAction");
	}

	@FXML
	public void onMenuIteAboutAction() {
		System.out.println("onMenuItemAboutAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

}
