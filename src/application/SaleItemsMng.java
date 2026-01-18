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

public class SaleItemsMng {

	public static Parent getView() {

		Label title = new Label("Medicines Sold In A Sale");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

		Label lblSale = new Label("Sale ID:");
		TextField tfSale = new TextField();
		tfSale.setPrefWidth(120);

		Button btnShow = new Button("Show Items");
		btnShow.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");

		HBox top = new HBox(10, lblSale, tfSale, btnShow);
		top.setAlignment(Pos.CENTER);
		top.setPadding(new Insets(10));

		TableView<SaleItem> table = new TableView<>();

		TableColumn<SaleItem, Integer> saleCol = new TableColumn<>("Sale ID");
		saleCol.setCellValueFactory(new PropertyValueFactory<>("saleId"));

		TableColumn<SaleItem, Integer> medIdCol = new TableColumn<>("Medicine ID");
		medIdCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));

		TableColumn<SaleItem, String> medNameCol = new TableColumn<>("Medicine Name");
		medNameCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));

		TableColumn<SaleItem, Integer> batchCol = new TableColumn<>("Batch ID");
		batchCol.setCellValueFactory(new PropertyValueFactory<>("batchId"));

		TableColumn<SaleItem, Integer> qtyCol = new TableColumn<>("Quantity");
		qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		TableColumn<SaleItem, Double> priceCol = new TableColumn<>("Unit Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

		table.getColumns().addAll(saleCol, medIdCol, medNameCol, batchCol, qtyCol, priceCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		btnShow.setOnAction(e -> {// 9
			table.getItems().clear();

			int saleId;
			try {
				saleId = Integer.parseInt(tfSale.getText().trim());
			} catch (NumberFormatException ex) {
				showError("Sale ID must be a valid number.");
				return;
			}

			String sql = "SELECT si.Sale_ID, m.Medicine_ID, m.Trade_Name AS Medicine_Name, "
					+ "       si.Batch_ID, si.Quantity, si.Unit_Price " + "FROM Sale_Item si "
					+ "JOIN Medicine m ON m.Medicine_ID = si.Medicine_ID " + "WHERE si.Sale_ID = ? "
					+ "ORDER BY m.Trade_Name";

			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				conn = DBConnection.getConnection();
				if (conn == null) {
					return;
				}

				ps = conn.prepareStatement(sql);
				ps.setInt(1, saleId);

				rs = ps.executeQuery();
				while (rs.next()) {
					SaleItem item = new SaleItem(rs.getInt("Sale_ID"), rs.getInt("Medicine_ID"),
							rs.getString("Medicine_Name"), rs.getInt("Batch_ID"), rs.getInt("Quantity"),
							rs.getDouble("Unit_Price"));
					table.getItems().add(item);
				}

				if (table.getItems().isEmpty()) {
					showInfo("No medicines found for this Sale ID.");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Failed to load sale items.");
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

		});

		VBox root = new VBox(15, title, top, table);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.TOP_CENTER);
		root.setStyle("-fx-background-color:#d8f3dc;");

		return root;
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
