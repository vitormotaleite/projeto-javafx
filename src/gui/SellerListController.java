package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Program;
import db.DbIntegrityException;
import gui.listener.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable,DataChangeListener {
	
	private SellerService service;
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller,Integer> tableColumnId;
	
	@FXML
	private TableColumn<Seller,String> tableColumnName;
	
	@FXML
	private TableColumn<Seller,String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller,Date> tableColumnDate;
	
	@FXML
	private TableColumn<Seller,Double> tableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller,Seller> tableColumnEDIT;
	
	@FXML
	private TableColumn<Seller,Seller> tableColumnREMOVE;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.securityState(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		
	}

	private void initializeNodes() {
		this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		this.tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		this.tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(this.tableColumnDate, "dd/MM/yyyy");
		this.tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(this.tableColumnBaseSalary, 2);
		
		
		Stage stage = (Stage) Program.getMainScene().getWindow();
		this.tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
		
	}
	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("servico estava nulo");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableList(list);
		this.tableViewSeller.setItems(obsList);
		this.initEditButtons();
		this.initRemoveButtons();
	}
	
	private void createDialogForm(Seller obj,String absoluteName,Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//			
//			SellerFormController controller = loader.getController();
//			controller.setSeller(obj);
//			controller.setSellerService(new SellerService());
//			controller.subscribeDataChangeListener(this);
//			controller.updateFormData();
//			
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("digite dados do departamento");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//		}
//		catch (IOException e) {
//			Alerts.showAlert("IO Exception", "erro ao carregar visao", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChanged() {
		this.updateTableView();
		
	}
	
	private void initEditButtons() { 
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() { 
		 private final Button button = new Button("edit"); 
		 @Override
		 protected void updateItem(Seller obj, boolean empty) { 
		 super.updateItem(obj, empty); 
		 if (obj == null) { 
		 setGraphic(null); 
		 return; 
		 } 
		 setGraphic(button); 
		 button.setOnAction( 
		 event -> createDialogForm( 
		 obj, "/gui/SellerForm.fxml",Utils.securityState(event))); 
		 } 
		 }); 
		}
	
	private void initRemoveButtons() { 
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue())); 
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() { 
		 private final Button button = new Button("remove"); 
		 @Override
		 protected void updateItem(Seller obj, boolean empty) { 
		 super.updateItem(obj, empty); 
		 if (obj == null) { 
		 setGraphic(null); 
		 return; 
		 } 
		 setGraphic(button); 
		 button.setOnAction(event -> removeEntity(obj)); 
		 } 
		 }); 
		}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("confirmacao", "tem certeza que quer deletar ?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("servico esta nulo");
			}
			try {
				service.remove(obj);
				this.updateTableView();
			}
			catch(DbIntegrityException e) {
				Alerts.showAlert("error ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	
}
