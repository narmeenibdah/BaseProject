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

public class PurchaseOrderMng {

	public static Parent getView() {

		TableView<PurchaseOrder> table = new TableView<>();

		TableColumn<PurchaseOrder, Integer> idCol = new TableColumn<>("PO ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("poId"));

		TableColumn<PurchaseOrder, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<PurchaseOrder, Double> totalCol = new TableColumn<>("Total Amount");
		totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

		TableColumn<PurchaseOrder, String> statusCol = new TableColumn<>("Status");
		statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

		TableColumn<PurchaseOrder, String> supplierCol = new TableColumn<>("Supplier");
		supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

		table.getColumns().addAll(idCol, dateCol, totalCol, statusCol, supplierCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Label title = new Label("Purchase Orders Details");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

		Button btnLoad = new Button("Load Purchase Orders ");
		btnLoad.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnLoad.setOnAction(e -> loadPurchOrder(table));

		Button btnAdd = new Button("Add");
		Button btnUpdate = new Button("Update");
		Button btnDelete = new Button("Delete");
		Button btnSearch = new Button("Search");

		btnAdd.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnUpdate.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnDelete.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnSearch.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");

		HBox buttonsBox = new HBox(10, btnAdd, btnUpdate, btnDelete, btnSearch, btnLoad);
		buttonsBox.setAlignment(Pos.CENTER);
		buttonsBox.setPadding(new Insets(10));

		FXForPurchaseOrder fx = new FXForPurchaseOrder();

		btnAdd.setOnAction(e -> fx.addPurchaseOrder(table));
		btnUpdate.setOnAction(e -> fx.updatePurchaseOrder(table));
		btnDelete.setOnAction(e -> fx.deletePurchaseOrder(table));
		btnSearch.setOnAction(e -> fx.searchPurchaseOrder(table));

		VBox root = new VBox(15, title, buttonsBox, table);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.TOP_CENTER);
		root.setStyle("-fx-background-color:#d8f3dc;");

		loadPurchOrder(table);
		return root;
	}

	public static void loadPurchOrder(TableView<PurchaseOrder> table) {// 6
		table.getItems().clear();

		String sql = "SELECT po.PO_ID, po.Date, po.Total_Amount, po.Status, s.Name AS Supplier_Name "
				+ "FROM Purchase_Order po " + "JOIN Supplier s ON po.Supplier_ID = s.Supplier_ID "
				+ "ORDER BY po.Date DESC";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				return;
			}

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				PurchaseOrder po = new PurchaseOrder(rs.getInt("PO_ID"), rs.getString("Date"),
						rs.getDouble("Total_Amount"), rs.getString("Status"), rs.getString("Supplier_Name"));
				table.getItems().add(po);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load Purchase Orders (Query 6).").show();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}
	}

}
