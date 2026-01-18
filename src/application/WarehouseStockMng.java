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

public class WarehouseStockMng {

	public static Parent getView() {

		Label title = new Label("Stock by Warehouse");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

		TableView<MedicineWarehouseStock> table = new TableView<>();

		TableColumn<MedicineWarehouseStock, Integer> medIdCol = new TableColumn<>("Medicine ID");
		medIdCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));

		TableColumn<MedicineWarehouseStock, String> medNameCol = new TableColumn<>("Medicine");
		medNameCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));

		TableColumn<MedicineWarehouseStock, Integer> whIdCol = new TableColumn<>("Warehouse ID");
		whIdCol.setCellValueFactory(new PropertyValueFactory<>("warehouseId"));

		TableColumn<MedicineWarehouseStock, String> whNameCol = new TableColumn<>("Warehouse");
		whNameCol.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));

		TableColumn<MedicineWarehouseStock, Integer> qtyCol = new TableColumn<>("Available Qty");
		qtyCol.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));

		table.getColumns().addAll(medIdCol, medNameCol, whIdCol, whNameCol, qtyCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Button btnLoad = new Button("Load Report");
		btnLoad.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");

		btnLoad.setOnAction(e -> loadStockReport(table));

		HBox actions = new HBox(10, btnLoad);
		actions.setAlignment(Pos.CENTER);
		actions.setPadding(new Insets(10));

		VBox root = new VBox(15, title, actions, table);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.TOP_CENTER);
		root.setStyle("-fx-background-color:#d8f3dc;");

		loadStockReport(table);

		return root;
	}

	public static void loadStockReport(TableView<MedicineWarehouseStock> table) {// 14
		table.getItems().clear();

		String sql = "SELECT " + "  m.Medicine_ID, m.Trade_Name AS Medicine_Name, "
				+ "  w.Warehouse_ID, w.Name AS Warehouse_Name, " + "  (SELECT SUM(b.Quantity) " + "   FROM Batch b "
				+ "   WHERE b.Medicine_ID = m.Medicine_ID "
				+ "     AND b.Warehouse_ID = w.Warehouse_ID) AS Available_Quantity " + "FROM Medicine m, Warehouse w "
				+ "ORDER BY m.Trade_Name, w.Name";

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

				Object obj = rs.getObject("Available_Quantity");
				int available = 0;

				if (obj != null) {
					if (obj instanceof Number) {
						available = ((Number) obj).intValue();
					} else {
						available = Integer.parseInt(obj.toString());
					}
				}

				MedicineWarehouseStock row = new MedicineWarehouseStock(rs.getInt("Medicine_ID"),
						rs.getString("Medicine_Name"), rs.getInt("Warehouse_ID"), rs.getString("Warehouse_Name"),
						available);

				table.getItems().add(row);
			}

			if (table.getItems().isEmpty()) {
				showInfo("No stock data found.");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			showError("Failed to load Stock by Warehouse (Query 14).");
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

	private static void showError(String msg) {
		Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
		a.showAndWait();
	}

	private static void showInfo(String msg) {
		Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		a.showAndWait();
	}
}
