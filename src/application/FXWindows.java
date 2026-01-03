package application;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class FXWindows {

	public static void design(Stage stage) {

		TabPane tabPane = new TabPane();

		Tab tab1 = new Tab("Medicines");
		tab1.setClosable(false);
		tab1.setContent(MedicineMng.getView());

		Tab tab2 = new Tab("Expiring Batches");
		tab2.setClosable(false);
		tab2.setContent(BatchMng.getView());

		Tab tab3 = new Tab("Suppliers");
		tab3.setClosable(false);
		tab3.setContent(SupplierMng.getView());

		Tab tab4 = new Tab("Employees");
		tab4.setClosable(false);
		tab4.setContent(EmployeeMng.getView());

		Tab tab5 = new Tab("Purchase Orders");
		tab5.setClosable(false);
		tab5.setContent(PurchaseOrderMng.getView());

		Tab tab6 = new Tab("PO Items");
		tab6.setClosable(false);
		tab6.setContent(PurchaseOrderItemsMng.getView());

		Tab tab7 = new Tab("Sales");
		tab7.setClosable(false);
		tab7.setContent(SaleMng.getView());
		
		Tab tab8 = new Tab("Sale Items");
		tab8.setClosable(false);
		tab8.setContent(SaleItemsMng.getView());
		
		Tab tab16 = new Tab("Regular Customers");
		tab16.setClosable(false);
		tab16.setContent(CustomerMng.getView());
		Tab tab18 = new Tab("Employee Sales");
		tab18.setContent(EmployeeSalesPeriodMng.getView());
		Tab tab10 = new Tab("Top Selling Medicines");
		tab10.setContent(TopSellingMedicinesMng.getView());
	


		tabPane.getTabs().addAll(tab1, tab2, tab3, tab4, tab5, tab6, tab7, tab8,tab16,tab18,tab10);


		

		Scene scene = new Scene(tabPane, 1200, 650);
		stage.setScene(scene);
		stage.setTitle("Pharmacy System");
		stage.show();
	}
}
