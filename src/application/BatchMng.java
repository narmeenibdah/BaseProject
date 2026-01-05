package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BatchMng {

	public static Parent getView() {

		TableView<Batch> table = new TableView<>();

		TableColumn<Batch, Integer> idCol = new TableColumn<>("Batch ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("batchId"));

		TableColumn<Batch, String> numCol = new TableColumn<>("Batch Number");
		numCol.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));

		TableColumn<Batch, String> expCol = new TableColumn<>("Expiry Date");
		expCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));

		TableColumn<Batch, Integer> qtyCol = new TableColumn<>("Quantity");
		qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		TableColumn<Batch, Double> costCol = new TableColumn<>("Cost");
		costCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

		TableColumn<Batch, String> medCol = new TableColumn<>("Medicine");
		medCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));

		TableColumn<Batch, String> whCol = new TableColumn<>("Warehouse");
		whCol.setCellValueFactory(new PropertyValueFactory<>("warehouseName"));

		TableColumn<Batch, Integer> daysCol = new TableColumn<>("Days Remaining");
		daysCol.setCellValueFactory(new PropertyValueFactory<>("daysRemaining"));

		table.getColumns().addAll(idCol, numCol, expCol, qtyCol, costCol, medCol, whCol, daysCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Label title = new Label("Batches - Management");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

		Button btnAdd = new Button("Add");
		Button btnDelete = new Button("Delete");
		Button btnSearch = new Button("Search by ID");
		Button btnUpdate = new Button("Update");
		Button btnAll = new Button("All Batches");
		Button btnExp = new Button("Expiring 60 Days");

		Button expiring = new Button("Expiring");

		String styleGreen = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";
		btnAdd.setStyle(styleGreen);
		btnDelete.setStyle(styleGreen);
		btnSearch.setStyle(styleGreen);
		btnUpdate.setStyle(styleGreen);
		btnAll.setStyle(styleGreen);
		btnExp.setStyle(styleGreen);
		expiring.setStyle(styleGreen);

		FXForBatch fx = new FXForBatch();

		btnAdd.setOnAction(e -> fx.addBatch(table));
		btnDelete.setOnAction(e -> fx.deleteBatch(table));
		btnSearch.setOnAction(e -> fx.searchBatch(table));
		btnUpdate.setOnAction(e -> fx.updateBatch(table));
		btnAll.setOnAction(e -> loadAllBatches(table));
		btnExp.setOnAction(e -> loadExpiringBatches(table));
		expiring.setOnAction(e -> loadExpiringBatchesWithDays(table));

		HBox topButtons = new HBox(10, btnAdd, btnDelete, btnSearch, btnUpdate, btnAll, btnExp, expiring);
		topButtons.setAlignment(Pos.CENTER);
		topButtons.setPadding(new Insets(10));

		VBox root = new VBox(15, title, topButtons, table);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.TOP_CENTER);
		root.setStyle("-fx-background-color:#d8f3dc;");

		loadAllBatches(table);

		return root;
	}

	public static void loadAllBatches(TableView<Batch> table) {
		table.getItems().clear();

		String sql = "SELECT b.Batch_ID, b.Batch_Number, b.Expiry_Date, b.Quantity, b.Cost, "
				+ "       m.Trade_Name AS Medicine_Name, w.Name AS Warehouse_Name " + "FROM Batch b "
				+ "JOIN Medicine m ON b.Medicine_ID = m.Medicine_ID "
				+ "JOIN Warehouse w ON b.Warehouse_ID = w.Warehouse_ID " + "ORDER BY b.Batch_ID";

		try (Connection conn = DBConnection.getConnection()) {
			if (conn == null)
				return;

			try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					Batch b = new Batch(rs.getInt("Batch_ID"), rs.getString("Batch_Number"),
							rs.getString("Expiry_Date"), rs.getInt("Quantity"), rs.getDouble("Cost"),
							rs.getString("Medicine_Name"), rs.getString("Warehouse_Name"));
					table.getItems().add(b);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load all batches.").show();
		}
	}

	public static void loadExpiringBatches(TableView<Batch> table) {
		table.getItems().clear();

		String sql = "SELECT b.Batch_ID, b.Batch_Number, b.Expiry_Date, b.Quantity, b.Cost, "
				+ "       m.Trade_Name AS Medicine_Name, w.Name AS Warehouse_Name " + "FROM Batch b "
				+ "JOIN Medicine m ON b.Medicine_ID = m.Medicine_ID "
				+ "JOIN Warehouse w ON b.Warehouse_ID = w.Warehouse_ID " + "WHERE b.Expiry_Date BETWEEN ? AND ? "
				+ "ORDER BY b.Expiry_Date";

		try (Connection conn = DBConnection.getConnection()) {
			if (conn == null)
				return;

			LocalDate today = LocalDate.now();
			LocalDate after60 = today.plusDays(60);

			try (PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setDate(1, java.sql.Date.valueOf(today));
				ps.setDate(2, java.sql.Date.valueOf(after60));

				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						Batch b = new Batch(rs.getInt("Batch_ID"), rs.getString("Batch_Number"),
								rs.getString("Expiry_Date"), rs.getInt("Quantity"), rs.getDouble("Cost"),
								rs.getString("Medicine_Name"), rs.getString("Warehouse_Name"));
						table.getItems().add(b);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load expiring batches.").show();
		}
	}

	public static void loadExpiringBatchesWithDays(TableView<Batch> table) {
		table.getItems().clear();

		String sql = "SELECT b.Batch_ID, b.Batch_Number, b.Expiry_Date, b.Quantity, b.Cost, "
				+ "       m.Trade_Name AS Medicine_Name, w.Name AS Warehouse_Name " + "FROM Batch b "
				+ "JOIN Medicine m ON b.Medicine_ID = m.Medicine_ID "
				+ "JOIN Warehouse w ON b.Warehouse_ID = w.Warehouse_ID " + "WHERE b.Expiry_Date BETWEEN ? AND ? "
				+ "ORDER BY b.Expiry_Date";

		try (Connection conn = DBConnection.getConnection()) {
			if (conn == null)
				return;

			LocalDate today = LocalDate.now();
			LocalDate after60 = today.plusDays(60);

			try (PreparedStatement ps = conn.prepareStatement(sql)) {

				ps.setDate(1, java.sql.Date.valueOf(today));
				ps.setDate(2, java.sql.Date.valueOf(after60));

				try (ResultSet rs = ps.executeQuery()) {

					while (rs.next()) {

						LocalDate expDate = rs.getDate("Expiry_Date").toLocalDate();
						int daysRemaining = (int) ChronoUnit.DAYS.between(today, expDate);

						Batch b = new Batch(rs.getInt("Batch_ID"), rs.getString("Batch_Number"),
								rs.getString("Expiry_Date"), rs.getInt("Quantity"), rs.getDouble("Cost"),
								rs.getString("Medicine_Name"), rs.getString("Warehouse_Name"), daysRemaining);

						table.getItems().add(b);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load expiring batches with days remaining.").show();
		}
	}

}
