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

public class FXForPurchaseOrder {

	public void addPurchaseOrder(TableView<PurchaseOrder> table) {

		Stage stage = new Stage();
		stage.setTitle("Add New Purchase Order");

		Label lblId = new Label("PO ID:");
		TextField tfId = new TextField();

		Label lblDate = new Label("Date (YYYY-MM-DD):");
		TextField tfDate = new TextField();

		Label lblTotal = new Label("Total Amount:");
		TextField tfTotal = new TextField();

		Label lblStatus = new Label("Status:");
		TextField tfStatus = new TextField();

		Label lblSupplierId = new Label("Supplier ID:");
		TextField tfSupplierId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.addRow(0, lblId, tfId);
		grid.addRow(1, lblDate, tfDate);
		grid.addRow(2, lblTotal, tfTotal);
		grid.addRow(3, lblStatus, tfStatus);
		grid.addRow(4, lblSupplierId, tfSupplierId);

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
				int poId = Integer.parseInt(tfId.getText().trim());
				String date = tfDate.getText().trim();
				double total = Double.parseDouble(tfTotal.getText().trim());
				String status = tfStatus.getText().trim();
				int supplierId = Integer.parseInt(tfSupplierId.getText().trim());

				if (date.isEmpty() || status.isEmpty()) {
					showError("Date and Status cannot be empty.");
					return;
				}

				try (Connection conn = DBConnection.getConnection()) {
					if (conn == null)
						return;

					try (PreparedStatement check = conn
							.prepareStatement("SELECT 1 FROM Purchase_Order WHERE PO_ID = ?")) {
						check.setInt(1, poId);
						try (ResultSet rs = check.executeQuery()) {
							if (rs.next()) {
								showError("PO ID already exists. Use another ID.");
								return;
							}
						}
					}

					try (PreparedStatement checkSup = conn
							.prepareStatement("SELECT 1 FROM Supplier WHERE Supplier_ID = ?")) {
						checkSup.setInt(1, supplierId);
						try (ResultSet rs = checkSup.executeQuery()) {
							if (!rs.next()) {
								showError("Supplier ID not found.");
								return;
							}
						}
					}

					try (PreparedStatement ps = conn.prepareStatement(
							"INSERT INTO Purchase_Order (PO_ID, Date, Total_Amount, Status, Supplier_ID) "
									+ "VALUES (?, ?, ?, ?, ?)")) {
						ps.setInt(1, poId);
						ps.setString(2, date);
						ps.setDouble(3, total);
						ps.setString(4, status);
						ps.setInt(5, supplierId);

						int rows = ps.executeUpdate();
						if (rows > 0) {
							showInfo("Purchase Order added successfully.");
							PurchaseOrderMng.loadPurchOrder(table);
							stage.close();
						} else {
							showError("Failed to add Purchase Order.");
						}
					}
				}

			} catch (NumberFormatException ex) {
				showError("PO ID, Total Amount, Supplier ID must be numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 420, 280));
		stage.showAndWait();
	}

	public void deletePurchaseOrder(TableView<PurchaseOrder> table) {

		Stage stage = new Stage();
		stage.setTitle("Delete Purchase Order");

		Label lblId = new Label("PO ID:");
		TextField tfId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));
		grid.addRow(0, lblId, tfId);

		Button btnDelete = new Button("Delete");
		Button btnCancel = new Button("Cancel");

		btnDelete.setStyle("-fx-background-color:#e63946; -fx-text-fill:white; -fx-font-weight:bold;");
		btnCancel.setStyle("-fx-background-color:#cccccc; -fx-font-weight:bold;");

		HBox actions = new HBox(10, btnDelete, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color:#f1faee;");

		btnDelete.setOnAction(e -> {

			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				String text = tfId.getText();
				if (text == null || text.trim().isEmpty()) {
					showError("Please enter a PO ID.");
					return;
				}

				int poId = Integer.parseInt(text.trim());

				conn = DBConnection.getConnection();
				if (conn == null) {
					showError("Cannot connect to the database right now. Please try again.");
					return;
				}

				// 1) وجود PO
				ps = conn.prepareStatement("SELECT 1 FROM Purchase_Order WHERE PO_ID = ?");
				ps.setInt(1, poId);
				rs = ps.executeQuery();
				if (!rs.next()) {
					showInfo("No purchase order found with ID = " + poId + ".");
					return;
				}
				rs.close();
				ps.close();

				// 2) check items
				ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM Purchase_Order_Item WHERE PO_ID = ?");
				ps.setInt(1, poId);
				rs = ps.executeQuery();
				rs.next();
				int itemCnt = rs.getInt("cnt");
				rs.close();
				ps.close();

				// 3) check payments
				ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM Payment WHERE PO_ID = ?");
				ps.setInt(1, poId);
				rs = ps.executeQuery();
				rs.next();
				int payCnt = rs.getInt("cnt");
				rs.close();
				ps.close();

				if (itemCnt > 0 || payCnt > 0) {
					String msg = "Sorry, this purchase order cannot be deleted because it has related records.\n\n";
					if (itemCnt > 0)
						msg += "• PO Items: " + itemCnt + " record(s)\n";
					if (payCnt > 0)
						msg += "• Payments: " + payCnt + " record(s)\n";
					msg += "\nReason: Deleting it would break the database relationships (foreign keys).\n"
							+ "Tip: Remove related records first, then try again.";
					showError(msg);
					return;
				}

				Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
						"Are you sure you want to delete PO ID = " + poId + " ?", ButtonType.YES, ButtonType.NO);
				confirm.setHeaderText(null);

				ButtonType res = confirm.showAndWait().orElse(ButtonType.NO);
				if (res != ButtonType.YES)
					return;

				ps = conn.prepareStatement("DELETE FROM Purchase_Order WHERE PO_ID = ?");
				ps.setInt(1, poId);
				int rows = ps.executeUpdate();
				ps.close();

				if (rows > 0) {
					showInfo("Purchase order deleted successfully.");
					PurchaseOrderMng.loadPurchOrder(table);
					stage.close();
				} else {
					showError("Delete failed. Please try again.");
				}

			} catch (NumberFormatException ex) {
				showError("PO ID must be a valid number.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error happened.\nDetails: " + ex.getMessage());
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

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 360, 160));
		stage.showAndWait();
	}

	public void updatePurchaseOrder(TableView<PurchaseOrder> table) {

		Stage stage = new Stage();
		stage.setTitle("Update Purchase Order");

		Label lblId = new Label("PO ID:");
		TextField tfId = new TextField();

		Label lblDate = new Label("New Date (YYYY-MM-DD):");
		TextField tfDate = new TextField();

		Label lblTotal = new Label("New Total Amount:");
		TextField tfTotal = new TextField();

		Label lblStatus = new Label("New Status:");
		TextField tfStatus = new TextField();

		Label lblSupplierId = new Label("New Supplier ID:");
		TextField tfSupplierId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.addRow(0, lblId, tfId);
		grid.addRow(1, lblDate, tfDate);
		grid.addRow(2, lblTotal, tfTotal);
		grid.addRow(3, lblStatus, tfStatus);
		grid.addRow(4, lblSupplierId, tfSupplierId);

		Button btnUpdate = new Button("Update");
		Button btnCancel = new Button("Cancel");

		btnUpdate.setStyle("-fx-background-color:#40916c; -fx-text-fill:white; -fx-font-weight:bold;");
		btnCancel.setStyle("-fx-background-color:#cccccc; -fx-font-weight:bold;");

		HBox actions = new HBox(10, btnUpdate, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color:#d8f3dc;");

		btnUpdate.setOnAction(e -> {
			try {
				int poId = Integer.parseInt(tfId.getText().trim());
				String date = tfDate.getText().trim();
				String status = tfStatus.getText().trim();

				boolean hasDate = !date.isEmpty();
				boolean hasStatus = !status.isEmpty();
				boolean hasTotal = !tfTotal.getText().trim().isEmpty();
				boolean hasSupplier = !tfSupplierId.getText().trim().isEmpty();

				if (!hasDate && !hasStatus && !hasTotal && !hasSupplier) {
					showError("Enter at least one field to update.");
					return;
				}

				Double total = null;
				Integer supplierId = null;

				if (hasTotal)
					total = Double.parseDouble(tfTotal.getText().trim());
				if (hasSupplier)
					supplierId = Integer.parseInt(tfSupplierId.getText().trim());

				try (Connection conn = DBConnection.getConnection()) {
					if (conn == null)
						return;

					try (PreparedStatement check = conn
							.prepareStatement("SELECT 1 FROM Purchase_Order WHERE PO_ID = ?")) {
						check.setInt(1, poId);
						try (ResultSet rs = check.executeQuery()) {
							if (!rs.next()) {
								showError("PO ID not found.");
								return;
							}
						}
					}

					if (supplierId != null) {
						try (PreparedStatement checkSup = conn
								.prepareStatement("SELECT 1 FROM Supplier WHERE Supplier_ID = ?")) {
							checkSup.setInt(1, supplierId);
							try (ResultSet rs = checkSup.executeQuery()) {
								if (!rs.next()) {
									showError("Supplier ID not found.");
									return;
								}
							}
						}
					}

					StringBuilder sql = new StringBuilder("UPDATE Purchase_Order SET ");
					boolean first = true;

					if (hasDate) {
						sql.append("Date = ?");
						first = false;
					}
					if (hasTotal) {
						sql.append(first ? "" : ", ").append("Total_Amount = ?");
						first = false;
					}
					if (hasStatus) {
						sql.append(first ? "" : ", ").append("Status = ?");
						first = false;
					}
					if (hasSupplier) {
						sql.append(first ? "" : ", ").append("Supplier_ID = ?");
						first = false;
					}

					sql.append(" WHERE PO_ID = ?");

					try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {

						int idx = 1;
						if (hasDate)
							ps.setString(idx++, date);
						if (hasTotal)
							ps.setDouble(idx++, total);
						if (hasStatus)
							ps.setString(idx++, status);
						if (hasSupplier)
							ps.setInt(idx++, supplierId);

						ps.setInt(idx, poId);

						int rows = ps.executeUpdate();
						if (rows > 0) {
							showInfo("Purchase Order updated successfully.");
							PurchaseOrderMng.loadPurchOrder(table);
							stage.close();
						} else {
							showError("Update failed.");
						}
					}
				}

			} catch (NumberFormatException ex) {
				showError("PO ID, Total Amount, Supplier ID must be numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 450, 320));
		stage.showAndWait();
	}

	public void searchPurchaseOrder(TableView<PurchaseOrder> table) {

		Stage stage = new Stage();
		stage.setTitle("Search Purchase Order by ID");

		Label lblId = new Label("PO ID:");
		TextField tfId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));
		grid.addRow(0, lblId, tfId);

		Button btnSearch = new Button("Search");
		Button btnCancel = new Button("Cancel");

		btnSearch.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnCancel.setStyle("-fx-background-color:#cccccc; -fx-font-weight:bold;");

		HBox actions = new HBox(10, btnSearch, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));

		btnSearch.setOnAction(e -> {
			try {
				int poId = Integer.parseInt(tfId.getText().trim());
				table.getItems().clear();

				String sql = "SELECT po.PO_ID, po.Date, po.Total_Amount, po.Status, s.Name AS Supplier_Name "
						+ "FROM Purchase_Order po " + "JOIN Supplier s ON po.Supplier_ID = s.Supplier_ID "
						+ "WHERE po.PO_ID = ?";

				try (Connection conn = DBConnection.getConnection();
						PreparedStatement ps = conn.prepareStatement(sql)) {

					ps.setInt(1, poId);

					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							PurchaseOrder po = new PurchaseOrder(rs.getInt("PO_ID"), rs.getString("Date"),
									rs.getDouble("Total_Amount"), rs.getString("Status"),
									rs.getString("Supplier_Name"));
							table.getItems().add(po);
							stage.close();
						} else {
							showError("No Purchase Order found with this ID.");
						}
					}
				}

			} catch (NumberFormatException ex) {
				showError("PO ID must be a number.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 360, 160));
		stage.showAndWait();
	}

	private void showError(String msg) {
		Alert a = new Alert(Alert.AlertType.ERROR, msg);
		a.setHeaderText(null);
		a.showAndWait();
	}

	private void showInfo(String msg) {
		Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
		a.setHeaderText(null);
		a.showAndWait();
	}
}
