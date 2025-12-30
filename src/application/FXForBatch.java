package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXForBatch {

	public void addBatch(TableView<Batch> table) {

		Stage stage = new Stage();
		stage.setTitle("Add Batch");

		TextField tfNumber = new TextField();
		TextField tfExpiry = new TextField();
		TextField tfQty = new TextField();
		TextField tfCost = new TextField();
		TextField tfMedId = new TextField();
		TextField tfWhId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.addRow(0, new Label("Batch Number:"), tfNumber);
		grid.addRow(1, new Label("Expiry Date (YYYY-MM-DD):"), tfExpiry);
		grid.addRow(2, new Label("Quantity:"), tfQty);
		grid.addRow(3, new Label("Cost:"), tfCost);
		grid.addRow(4, new Label("Medicine_ID:"), tfMedId);
		grid.addRow(5, new Label("Warehouse_ID:"), tfWhId);

		Button btnAdd = new Button("Add");
		Button btnCancel = new Button("Cancel");

		btnAdd.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnCancel.setStyle("-fx-background-color:#cccccc; -fx-font-weight:bold;");

		HBox actions = new HBox(10, btnAdd, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color:#d8f3dc;");

		btnAdd.setOnAction(e -> {
			try {
				String number = tfNumber.getText().trim();
				String expiry = tfExpiry.getText().trim();
				int qty = Integer.parseInt(tfQty.getText().trim());
				double cost = Double.parseDouble(tfCost.getText().trim());
				int medId = Integer.parseInt(tfMedId.getText().trim());
				int whId = Integer.parseInt(tfWhId.getText().trim());

				Connection conn = DBConnection.getConnection();

				String sql = "INSERT INTO Batch (Batch_Number, Expiry_Date, Quantity, Cost, Medicine_ID, Warehouse_ID) "
						+ "VALUES (?, ?, ?, ?, ?, ?)";

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, number);
				ps.setString(2, expiry);
				ps.setInt(3, qty);
				ps.setDouble(4, cost);
				ps.setInt(5, medId);
				ps.setInt(6, whId);

				int rows = ps.executeUpdate();
				ps.close();
				conn.close();

				if (rows > 0) {
					showInfo("Batch added successfully.");
					BatchMng.loadAllBatches(table);
					stage.close();
				} else {
					showError("Failed to add batch.");
				}

			} catch (NumberFormatException ex) {
				showError("Quantity/Cost/Medicine_ID/Warehouse_ID must be numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 450, 330));
		stage.showAndWait();
	}

	public void deleteBatch(TableView<Batch> table) {

		Stage stage = new Stage();
		stage.setTitle("Delete Batch");

		TextField tfId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));
		grid.addRow(0, new Label("Batch ID:"), tfId);

		Button btnDelete = new Button("Delete");
		Button btnCancel = new Button("Cancel");

		btnDelete.setStyle("-fx-background-color:#e63946; -fx-text-fill:white; -fx-font-weight:bold;");
		btnCancel.setStyle("-fx-background-color:#cccccc; -fx-font-weight:bold;");

		HBox actions = new HBox(10, btnDelete, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));

		btnDelete.setOnAction(e -> {
			try {
				int id = Integer.parseInt(tfId.getText().trim());
				Connection conn = DBConnection.getConnection();

				PreparedStatement chk = conn
						.prepareStatement("SELECT COUNT(*) AS cnt FROM Sale_Item WHERE Batch_ID = ?");
				chk.setInt(1, id);
				ResultSet rs = chk.executeQuery();
				rs.next();
				int usedCount = rs.getInt("cnt");
				rs.close();
				chk.close();

				if (usedCount > 0) {
					conn.close();
					showError("Cannot delete batch: used in sales (" + usedCount + ").");
					return;
				}

				PreparedStatement ps = conn.prepareStatement("DELETE FROM Batch WHERE Batch_ID = ?");
				ps.setInt(1, id);

				int rows = ps.executeUpdate();
				ps.close();
				conn.close();

				if (rows > 0) {
					showInfo("Batch deleted successfully.");
					BatchMng.loadAllBatches(table);
					stage.close();
				} else {
					showError("No batch found with this ID.");
				}

			} catch (NumberFormatException ex) {
				showError("Batch ID must be a number.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 360, 160));
		stage.showAndWait();
	}

	public void searchBatch(TableView<Batch> table) {

		Stage stage = new Stage();
		stage.setTitle("Search Batch by ID");

		TextField tfId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));
		grid.addRow(0, new Label("Batch ID:"), tfId);

		Button btnSearch = new Button("Search");
		Button btnCancel = new Button("Cancel");

		btnSearch.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnCancel.setStyle("-fx-background-color:#cccccc; -fx-font-weight:bold;");

		HBox actions = new HBox(10, btnSearch, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color:#f1faee;");

		btnSearch.setOnAction(e -> {
			try {
				int id = Integer.parseInt(tfId.getText().trim());

				table.getItems().clear();

				Connection conn = DBConnection.getConnection();

				String sql = "SELECT b.Batch_ID, b.Batch_Number, b.Expiry_Date, b.Quantity, b.Cost, "
						+ "m.Trade_Name AS Medicine_Name, w.Name AS Warehouse_Name " + "FROM Batch b "
						+ "JOIN Medicine m ON b.Medicine_ID = m.Medicine_ID "
						+ "JOIN Warehouse w ON b.Warehouse_ID = w.Warehouse_ID " + "WHERE b.Batch_ID = ?";

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					Batch b = new Batch(rs.getInt("Batch_ID"), rs.getString("Batch_Number"),
							rs.getString("Expiry_Date"), rs.getInt("Quantity"), rs.getDouble("Cost"),
							rs.getString("Medicine_Name"), rs.getString("Warehouse_Name"));
					table.getItems().add(b);
					stage.close();
				} else {
					showInfo("No batch found with this ID.");
				}

				rs.close();
				ps.close();
				conn.close();

			} catch (NumberFormatException ex) {
				showError("Batch ID must be a number.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 360, 160));
		stage.showAndWait();
	}

	public void updateBatch(TableView<Batch> table) {

		Stage stage = new Stage();
		stage.setTitle("Update Batch");

		TextField tfId = new TextField();
		TextField tfNum = new TextField();
		TextField tfExp = new TextField();
		TextField tfQty = new TextField();
		TextField tfCost = new TextField();
		TextField tfMedId = new TextField();
		TextField tfWhId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.addRow(0, new Label("Batch ID:"), tfId);
		grid.addRow(1, new Label("New Batch Number:"), tfNum);
		grid.addRow(2, new Label("New Expiry Date (YYYY-MM-DD):"), tfExp);
		grid.addRow(3, new Label("New Quantity:"), tfQty);
		grid.addRow(4, new Label("New Cost:"), tfCost);
		grid.addRow(5, new Label("New Medicine_ID:"), tfMedId);
		grid.addRow(6, new Label("New Warehouse_ID:"), tfWhId);

		Button btnUpdate = new Button("Update");
		Button btnCancel = new Button("Cancel");

		btnUpdate.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnCancel.setStyle("-fx-background-color:#cccccc; -fx-font-weight:bold;");

		HBox actions = new HBox(10, btnUpdate, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color:#f1faee;");

		btnUpdate.setOnAction(e -> {
			try {
				int id = Integer.parseInt(tfId.getText().trim());

				Connection conn = DBConnection.getConnection();

				PreparedStatement psOld = conn
						.prepareStatement("SELECT Batch_Number, Expiry_Date, Quantity, Cost, Medicine_ID, Warehouse_ID "
								+ "FROM Batch WHERE Batch_ID = ?");
				psOld.setInt(1, id);
				ResultSet rsOld = psOld.executeQuery();

				if (!rsOld.next()) {
					showError("No batch found with this ID.");
					rsOld.close();
					psOld.close();
					conn.close();
					return;
				}

				String oldNum = rsOld.getString("Batch_Number");
				String oldExp = rsOld.getString("Expiry_Date");
				int oldQty = rsOld.getInt("Quantity");
				double oldCost = rsOld.getDouble("Cost");
				int oldMedId = rsOld.getInt("Medicine_ID");
				int oldWhId = rsOld.getInt("Warehouse_ID");

				rsOld.close();
				psOld.close();

				String newNum = tfNum.getText().trim();
				String newExp = tfExp.getText().trim();

				int newQty = tfQty.getText().trim().isEmpty() ? oldQty : Integer.parseInt(tfQty.getText().trim());
				double newCost = tfCost.getText().trim().isEmpty() ? oldCost
						: Double.parseDouble(tfCost.getText().trim());
				int newMed = tfMedId.getText().trim().isEmpty() ? oldMedId : Integer.parseInt(tfMedId.getText().trim());
				int newWh = tfWhId.getText().trim().isEmpty() ? oldWhId : Integer.parseInt(tfWhId.getText().trim());

				String finalNum = newNum.isEmpty() ? oldNum : newNum;
				String finalExp = newExp.isEmpty() ? oldExp : newExp;

				PreparedStatement psUp = conn.prepareStatement(
						"UPDATE Batch SET Batch_Number=?, Expiry_Date=?, Quantity=?, Cost=?, Medicine_ID=?, Warehouse_ID=? "
								+ "WHERE Batch_ID=?");
				psUp.setString(1, finalNum);
				psUp.setString(2, finalExp);
				psUp.setInt(3, newQty);
				psUp.setDouble(4, newCost);
				psUp.setInt(5, newMed);
				psUp.setInt(6, newWh);
				psUp.setInt(7, id);

				int rows = psUp.executeUpdate();
				psUp.close();
				conn.close();

				if (rows > 0) {
					showInfo("Batch updated successfully.");
					BatchMng.loadAllBatches(table);
					stage.close();
				} else {
					showError("Update failed.");
				}

			} catch (NumberFormatException ex) {
				showError("IDs/Quantity/Cost must be valid numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 520, 380));
		stage.showAndWait();
	}

	private void showError(String msg) {
		new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
	}

	private void showInfo(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
	}
}
