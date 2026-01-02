package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SaleMng {

	public static Parent getView() {

		TableView<Sale> table = new TableView<>();

		TableColumn<Sale, Integer> idCol = new TableColumn<>("Sale ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("saleId"));

		TableColumn<Sale, String> dateCol = new TableColumn<>("Sale Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

		TableColumn<Sale, Double> totalCol = new TableColumn<>("Total Amount");
		totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

		TableColumn<Sale, Integer> branchCol = new TableColumn<>("Branch ID");
		branchCol.setCellValueFactory(new PropertyValueFactory<>("branchId"));

		TableColumn<Sale, Integer> empCol = new TableColumn<>("Employee ID");
		empCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

		TableColumn<Sale, Integer> custCol = new TableColumn<>("Customer ID");
		custCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

		table.getColumns().addAll(idCol, dateCol, totalCol, branchCol, empCol, custCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Label title = new Label("Sales - Management");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

		Button btnAdd = new Button("Add");
		Button btnDelete = new Button("Delete");
		Button btnSearch = new Button("Search");
		Button btnUpdate = new Button("Update");
		Button btnAll = new Button("All Sales");
		Button btnByDate = new Button("Sales by Date");

		String style = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";
		btnAdd.setStyle(style);
		btnDelete.setStyle(style);
		btnSearch.setStyle(style);
		btnUpdate.setStyle(style);
		btnAll.setStyle(style);
		btnByDate.setStyle(style);

		FXForSale fx = new FXForSale();

		btnAdd.setOnAction(e -> fx.addSale(table));
		btnDelete.setOnAction(e -> fx.deleteSale(table));
		btnSearch.setOnAction(e -> fx.searchSale(table));
		btnUpdate.setOnAction(e -> fx.updateSale(table));

		btnAll.setOnAction(e -> loadAllSales(table));
		btnByDate.setOnAction(e -> fx.salesByDate(table));

		HBox buttons = new HBox(10, btnAdd, btnDelete, btnSearch, btnUpdate, btnAll, btnByDate);
		buttons.setAlignment(Pos.CENTER);
		buttons.setPadding(new Insets(10));

		VBox root = new VBox(15, title, buttons, table);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.TOP_CENTER);
		root.setStyle("-fx-background-color:#d8f3dc;");

		loadAllSales(table);
		return root;
	}

	public static void loadAllSales(TableView<Sale> table) {
		table.getItems().clear();

		String sql = "SELECT Sale_ID, Sale_Date, Total_Amount, Branch_ID, Employee_ID, Customer_ID FROM Sale ORDER BY Sale_Date DESC";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				table.getItems()
						.add(new Sale(rs.getInt("Sale_ID"), rs.getString("Sale_Date"), rs.getDouble("Total_Amount"),
								rs.getInt("Branch_ID"), rs.getInt("Employee_ID"), rs.getInt("Customer_ID")));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load sales.").show();
		}
	}
}
