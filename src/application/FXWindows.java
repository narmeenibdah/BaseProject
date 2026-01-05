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
		
		Tab tab9 = new Tab("Regular Customers");
		tab9.setClosable(false);
		tab9.setContent(CustomerMng.getView());
		
		Tab tab10 = new Tab("Employee Sales");
		tab10.setContent(EmployeeSalesPeriodMng.getView());
		
		Tab tab11 = new Tab("Top Selling Medicines");
		tab11.setContent(TopSellingMedicinesMng.getView());
		
		Tab tab12 = new Tab("Monthly Totals");
		tab12.setClosable(false);
		tab12.setContent(MonthlyTotalsMng.getView());
		
		Tab tabStock = new Tab("Stock by Warehouse");
		tabStock.setClosable(false);
		tabStock.setContent(WarehouseStockMng.getView());

		Tab tab13 = new Tab("Stock Movements");
		tab13.setContent(StockMovementMng.getView());




		

	


		tabPane.getTabs().addAll(tab1, tab2, tab3, tab4, tab5, tab6, tab7, tab8,tab9,tab10,tab11, tab12, tabStock,tab13);


		

		Scene scene = new Scene(tabPane, 1200, 650);
		stage.setScene(scene);
		stage.setTitle("Pharmacy System");
		stage.show();
	}
}
