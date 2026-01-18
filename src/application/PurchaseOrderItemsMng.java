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

public class PurchaseOrderItemsMng {

	public static Parent getView() {

		Label title = new Label("Purchase Order Items");
		title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

		Label lblPo = new Label("Purchase Order ID (PO_ID):");
		TextField tfPo = new TextField();
		tfPo.setPrefWidth(120);

		Button btnShow = new Button("Show Items");
		btnShow.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");

		HBox top = new HBox(10, lblPo, tfPo, btnShow);
		top.setAlignment(Pos.CENTER_LEFT);

		TableView<PurchaseOrderItemView> table = new TableView<>();

		TableColumn<PurchaseOrderItemView, Integer> poCol = new TableColumn<>("PO ID");
		poCol.setCellValueFactory(new PropertyValueFactory<>("poId"));

		TableColumn<PurchaseOrderItemView, Integer> medIdCol = new TableColumn<>("Medicine ID");
		medIdCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));

		TableColumn<PurchaseOrderItemView, String> medNameCol = new TableColumn<>("Medicine Name");
		medNameCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));

		TableColumn<PurchaseOrderItemView, Integer> qtyCol = new TableColumn<>("Quantity");
		qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		TableColumn<PurchaseOrderItemView, Double> priceCol = new TableColumn<>("Unit Price");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

		table.getColumns().addAll(poCol, medIdCol, medNameCol, qtyCol, priceCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		btnShow.setOnAction(e -> {
			table.getItems().clear();

			int poId;
			try {
				poId = Integer.parseInt(tfPo.getText().trim());
			} catch (NumberFormatException ex) {
				new Alert(Alert.AlertType.ERROR, "PO_ID must be a valid number.").show();
				return;
			}

			String sql = "SELECT poi.PO_ID, m.Medicine_ID, m.Trade_Name AS Medicine_Name, "
					+ "       poi.Quantity, poi.Unit_Price " + "FROM Purchase_Order_Item poi "
					+ "JOIN Medicine m ON poi.Medicine_ID = m.Medicine_ID " + "WHERE poi.PO_ID = ?";

			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				conn = DBConnection.getConnection();
				if (conn == null) {
					return;
				}

				ps = conn.prepareStatement(sql);
				ps.setInt(1, poId);

				rs = ps.executeQuery();
				while (rs.next()) {
					PurchaseOrderItemView row = new PurchaseOrderItemView(rs.getInt("PO_ID"), rs.getInt("Medicine_ID"),
							rs.getString("Medicine_Name"), rs.getInt("Quantity"), rs.getDouble("Unit_Price"));
					table.getItems().add(row);
				}

				if (table.getItems().isEmpty()) {
					new Alert(Alert.AlertType.INFORMATION, "No items found for this Purchase Order.").show();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				new Alert(Alert.AlertType.ERROR, "Failed to load purchase order items.").show();
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
		root.setStyle("-fx-background-color:#d8f3dc;");

		return root;
	}
}
