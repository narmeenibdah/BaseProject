package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StockMovementMng {

	public static Parent getView() {

		Label title = new Label("Stock Movements By Medicine");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

		TextField tfMedId = new TextField();
		tfMedId.setPromptText("Medicine ID");
		tfMedId.setPrefWidth(150);

		Button btnLoad = new Button("Load");
		btnLoad.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");

		HBox top = new HBox(10, tfMedId, btnLoad);
		top.setAlignment(Pos.CENTER);

		TableView<StockMovementView> table = new TableView<>();

		TableColumn<StockMovementView, String> typeCol = new TableColumn<>("Type");
		typeCol.setCellValueFactory(new PropertyValueFactory<>("movementType"));

		TableColumn<StockMovementView, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("movementDate"));

		TableColumn<StockMovementView, Integer> refCol = new TableColumn<>("Reference");
		refCol.setCellValueFactory(new PropertyValueFactory<>("referenceId"));

		TableColumn<StockMovementView, Integer> qtyCol = new TableColumn<>("Quantity");
		qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		table.getColumns().addAll(typeCol, dateCol, refCol, qtyCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		btnLoad.setOnAction(e -> {
			table.getItems().clear();

			if (tfMedId.getText().trim().isEmpty()) {
				new Alert(Alert.AlertType.WARNING, "Please enter Medicine ID").show();
				return;
			}

			int medId;
			try {
				medId = Integer.parseInt(tfMedId.getText().trim());
			} catch (Exception ex) {
				new Alert(Alert.AlertType.WARNING, "Medicine ID must be a number").show();
				return;
			}

			loadData(table, medId);
		});

		VBox root = new VBox(15, title, top, table);
		root.setAlignment(Pos.TOP_CENTER);
		root.setPadding(new Insets(30, 15, 15, 15));
		root.setStyle("-fx-background-color:#d8f3dc;");

		return root;
	}

	private static void loadData(TableView<StockMovementView> table, int medId) {// 13

		table.getItems().clear();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				return;
			}

			// ---------- PURCHASE movements ----------
			String sqlPurchase = "SELECT po.Date AS Movement_Date, poi.PO_ID AS Reference_ID, poi.Quantity "
					+ "FROM Purchase_Order_Item poi " + "JOIN Purchase_Order po ON poi.PO_ID = po.PO_ID "
					+ "WHERE poi.Medicine_ID = ?";

			ps = conn.prepareStatement(sqlPurchase);
			ps.setInt(1, medId);
			rs = ps.executeQuery();

			while (rs.next()) {
				StockMovementView row = new StockMovementView("PURCHASE", rs.getString("Movement_Date"),
						rs.getInt("Reference_ID"), rs.getInt("Quantity"));
				table.getItems().add(row);
			}

			rs.close();
			ps.close();

			// ---------- SALE movements ----------
			String sqlSale = "SELECT s.Sale_Date AS Movement_Date, si.Sale_ID AS Reference_ID, si.Quantity "
					+ "FROM Sale_Item si " + "JOIN Sale s ON si.Sale_ID = s.Sale_ID " + "WHERE si.Medicine_ID = ?";

			ps = conn.prepareStatement(sqlSale);
			ps.setInt(1, medId);
			rs = ps.executeQuery();

			while (rs.next()) {
				StockMovementView row = new StockMovementView("SALE", rs.getString("Movement_Date"),
						rs.getInt("Reference_ID"), rs.getInt("Quantity"));
				table.getItems().add(row);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load stock movements.").show();
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
