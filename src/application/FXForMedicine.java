package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXForMedicine {

	public void addMedicine(TableView<Medicine> table) {

		Stage stage = new Stage();
		stage.setTitle("Add New Medicine");

		Label lblId = new Label("Medicine ID:");
		TextField tfId = new TextField();

		Label lblName = new Label("Trade Name:");
		TextField tfName = new TextField();

		Label lblUnit = new Label("Unit:");
		TextField tfUnit = new TextField();

		Label lblReord = new Label("Reorder Level:");
		TextField tfReorder = new TextField();

		Label lblPrice = new Label("Selling Price:");
		TextField tfPrice = new TextField();

		Label lblReq = new Label("Requires Prescription:");
		CheckBox cbReq = new CheckBox();

		Label lblCatId = new Label("Category ID:");
		TextField tfCategory = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.add(lblId, 0, 0);
		grid.add(tfId, 1, 0);
		grid.add(lblName, 0, 1);
		grid.add(tfName, 1, 1);
		grid.add(lblUnit, 0, 2);
		grid.add(tfUnit, 1, 2);
		grid.add(lblReord, 0, 3);
		grid.add(tfReorder, 1, 3);
		grid.add(lblPrice, 0, 4);
		grid.add(tfPrice, 1, 4);
		grid.add(lblReq, 0, 5);
		grid.add(cbReq, 1, 5);
		grid.add(lblCatId, 0, 6);
		grid.add(tfCategory, 1, 6);

		Button btnAdd = new Button("Add");
		Button btnCancel = new Button("Cancel");

		btnAdd.setStyle("-fx-background-color: #52b788; -fx-text-fill: white; -fx-font-weight: bold;");
		btnCancel.setStyle("-fx-background-color: #cccccc; -fx-font-weight: bold;");

		HBox actions = new HBox(10, btnAdd, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color: #d8f3dc;");

		btnAdd.setOnAction(e -> {
			try {
				int id = Integer.parseInt(tfId.getText().trim());
				String name = tfName.getText().trim();
				String unit = tfUnit.getText().trim();
				int reorder = Integer.parseInt(tfReorder.getText().trim());
				double price = Double.parseDouble(tfPrice.getText().trim());
				boolean req = cbReq.isSelected();
				int categoryId = Integer.parseInt(tfCategory.getText().trim());

				if (name.isEmpty() || unit.isEmpty()) {
					showError("Name and Unit cannot be empty.");
					return;
				}

				Connection conn = DBConnection.getConnection();

				PreparedStatement check = conn.prepareStatement("SELECT 1 FROM Medicine WHERE Medicine_ID = ?");
				check.setInt(1, id);
				ResultSet rs = check.executeQuery();
				if (rs.next()) {
					showError("ID already exists. Please use another ID.");
					rs.close();
					check.close();
					return;
				}
				rs.close();
				check.close();

				PreparedStatement ps = conn.prepareStatement("INSERT INTO Medicine "
						+ "(Medicine_ID, Trade_Name, Unit, Reorder_Level, "
						+ " Selling_Price, Requires_Prescription, Category_ID) " + "VALUES (?, ?, ?, ?, ?, ?, ?)");

				ps.setInt(1, id);
				ps.setString(2, name);
				ps.setString(3, unit);
				ps.setInt(4, reorder);
				ps.setDouble(5, price);
				ps.setBoolean(6, req);
				ps.setInt(7, categoryId);

				int rows = ps.executeUpdate();
				ps.close();

				if (rows > 0) {
					showInfo("Medicine added successfully.");

					MedicineMng.loadMedicines(table);
					stage.close();
				} else {
					showError("Failed to add medicine.");
				}

			} catch (NumberFormatException ex) {
				showError("ID, Reorder Level, Price, Category ID must be numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e -> stage.close());

		stage.setScene(new Scene(root, 420, 360));
		stage.showAndWait();
	}

	public void deleteMedicine(TableView<Medicine> table) {

		Stage stage = new Stage();
		stage.setTitle("Delete Medicine");

		TextField tfId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));
		grid.addRow(0, new Label("Medicine ID:"), tfId);

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
					showError("Please enter a Medicine ID.");
					return;
				}

				int id = Integer.parseInt(text.trim());

				conn = DBConnection.getConnection();
				if (conn == null) {
					showError("Cannot connect to the database right now. Please try again.");
					return;
				}

				// 1) هل الدواء موجود؟
				ps = conn.prepareStatement("SELECT 1 FROM Medicine WHERE Medicine_ID = ?");
				ps.setInt(1, id);
				rs = ps.executeQuery();
				if (!rs.next()) {
					showInfo("No medicine found with ID = " + id + ".");
					return;
				}
				rs.close();
				ps.close();

				// 2) عدد الـ Batches
				ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM Batch WHERE Medicine_ID = ?");
				ps.setInt(1, id);
				rs = ps.executeQuery();
				rs.next();
				int batchCnt = rs.getInt("cnt");
				rs.close();
				ps.close();

				// 3) عدد الـ Sale_Item
				ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM Sale_Item WHERE Medicine_ID = ?");
				ps.setInt(1, id);
				rs = ps.executeQuery();
				rs.next();
				int saleCnt = rs.getInt("cnt");
				rs.close();
				ps.close();

				// 4) عدد الـ Purchase_Order_Item
				ps = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM Purchase_Order_Item WHERE Medicine_ID = ?");
				ps.setInt(1, id);
				rs = ps.executeQuery();
				rs.next();
				int poCnt = rs.getInt("cnt");
				rs.close();
				ps.close();

				if (batchCnt > 0 || saleCnt > 0 || poCnt > 0) {

					String msg = "Sorry, this medicine cannot be deleted because it has related records.\n\n";

					if (batchCnt > 0)
						msg += "• Batches: " + batchCnt + " record(s)\n";
					if (saleCnt > 0)
						msg += "• Sale Items: " + saleCnt + " record(s)\n";
					if (poCnt > 0)
						msg += "• Purchase Order Items: " + poCnt + " record(s)\n";

					msg += "\nReason: Deleting it would break the database relationships (foreign keys).\n"
							+ "Tip: Remove the related records first, then try again.";

					showError(msg);
					return;
				}

				Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
						"Are you sure you want to delete Medicine ID = " + id + " ?", ButtonType.YES, ButtonType.NO);
				confirm.setHeaderText(null);

				ButtonType res = confirm.showAndWait().orElse(ButtonType.NO);
				if (res != ButtonType.YES)
					return;

				ps = conn.prepareStatement("DELETE FROM Medicine WHERE Medicine_ID = ?");
				ps.setInt(1, id);
				int rows = ps.executeUpdate();
				ps.close();

				if (rows > 0) {
					showInfo("Medicine deleted successfully.");
					MedicineMng.loadMedicines(table);
					stage.close();
				} else {
					showError("Delete failed. Please try again.");
				}

			} catch (NumberFormatException ex) {
				showError("Medicine ID must be a valid number.");
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

	public void searchMedicine(TableView<Medicine> table) {

		Stage stagee = new Stage();
		stagee.setTitle("Search Medicine by ID");

		Label lblId = new Label("Medicine ID:");
		TextField tfId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));
		grid.add(lblId, 0, 0);
		grid.add(tfId, 1, 0);

		Button btnSearch = new Button("Search");
		Button btnCancel = new Button("Cancel");

		btnSearch.setStyle("-fx-background-color: #52b788; -fx-text-fill: white; -fx-font-weight: bold;");
		btnCancel.setStyle("-fx-background-color: #cccccc; -fx-font-weight: bold;");

		HBox actions = new HBox(10, btnSearch, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color: #f1faee;");

		btnSearch.setOnAction(e -> {

			Connection conn = null;

			try {
				int id = Integer.parseInt(tfId.getText().trim());
				table.getItems().clear();

				conn = DBConnection.getConnection();
				if (conn == null)
					return;

				String sql = "SELECT m.Medicine_ID, m.Trade_Name, m.Unit, "
						+ "m.Reorder_Level, m.Selling_Price, m.Requires_Prescription, " + "c.Name AS Category_Name "
						+ "FROM Medicine m " + "JOIN Category c ON m.Category_ID = c.Category_ID "
						+ "WHERE m.Medicine_ID = ?";

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					Medicine med = new Medicine(rs.getInt("Medicine_ID"), rs.getString("Trade_Name"),
							rs.getString("Unit"), rs.getInt("Reorder_Level"), rs.getDouble("Selling_Price"),
							rs.getBoolean("Requires_Prescription"), rs.getString("Category_Name"));
					table.getItems().add(med);
					stagee.close();
				} else {
					showInfo("No medicine found with this ID.");
				}

				rs.close();
				ps.close();

			} catch (NumberFormatException ex) {
				showError("ID must be a valid number.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception ex) {

				}
			}
		});

		btnCancel.setOnAction(e2 -> stagee.close());

		stagee.setScene(new Scene(root, 360, 160));
		stagee.showAndWait();
	}

	public void updateMedicine(TableView<Medicine> table) {

		Stage stagee = new Stage();
		stagee.setTitle("Update Medicine");

		Label lblId = new Label("Medicine ID:");
		TextField tfId = new TextField();

		Label lblName = new Label("New Trade Name:");
		TextField tfName = new TextField();

		Label lblUnit = new Label("New Unit:");
		TextField tfUnit = new TextField();

		Label lblReord = new Label("New Reorder Level:");
		TextField tfReorder = new TextField();

		Label lblPrice = new Label("New Selling Price:");
		TextField tfPrice = new TextField();

		Label lblReq = new Label("Requires Prescription (set new):");
		CheckBox cbReq = new CheckBox();

		Label lblCatId = new Label("New Category ID:");
		TextField tfCategory = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.add(lblId, 0, 0);
		grid.add(tfId, 1, 0);
		grid.add(lblName, 0, 1);
		grid.add(tfName, 1, 1);
		grid.add(lblUnit, 0, 2);
		grid.add(tfUnit, 1, 2);
		grid.add(lblReord, 0, 3);
		grid.add(tfReorder, 1, 3);
		grid.add(lblPrice, 0, 4);
		grid.add(tfPrice, 1, 4);
		grid.add(lblReq, 0, 5);
		grid.add(cbReq, 1, 5);
		grid.add(lblCatId, 0, 6);
		grid.add(tfCategory, 1, 6);

		Button btnUpdate = new Button("Update");
		Button btnCancel = new Button("Cancel");

		btnUpdate.setStyle("-fx-background-color: #52b788; -fx-text-fill: white; -fx-font-weight: bold;");
		btnCancel.setStyle("-fx-background-color: #cccccc; -fx-font-weight: bold;");

		HBox actions = new HBox(10, btnUpdate, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color: #f1faee;");

		btnUpdate.setOnAction(e -> {

			Connection conn = null;

			try {
				int id = Integer.parseInt(tfId.getText().trim());

				String newName = tfName.getText();
				if (newName != null)
					newName = newName.trim();

				String newUnit = tfUnit.getText();
				if (newUnit != null)
					newUnit = newUnit.trim();

				int newReorder = -1;
				String reordText = tfReorder.getText();
				if (reordText != null && !reordText.trim().isEmpty()) {
					newReorder = Integer.parseInt(reordText.trim());
				}

				double newPrice = -1;
				String priceText = tfPrice.getText();
				if (priceText != null && !priceText.trim().isEmpty()) {
					newPrice = Double.parseDouble(priceText.trim());
				}

				int newCategoryId = -1;
				String catText = tfCategory.getText();
				if (catText != null && !catText.trim().isEmpty()) {
					newCategoryId = Integer.parseInt(catText.trim());
				}

				boolean newReq = cbReq.isSelected();

				conn = DBConnection.getConnection();
				if (conn == null)
					return;

				PreparedStatement psSel = conn.prepareStatement(
						"SELECT Medicine_ID, Trade_Name, Unit, Reorder_Level, Selling_Price, Requires_Prescription, Category_ID "
								+ "FROM Medicine WHERE Medicine_ID = ?");
				psSel.setInt(1, id);
				ResultSet rs = psSel.executeQuery();

				if (!rs.next()) {
					showError("Medicine not found with this ID.");
					rs.close();
					psSel.close();
					return;
				}

				String oldName = rs.getString("Trade_Name");
				String oldUnit = rs.getString("Unit");
				int oldReorder = rs.getInt("Reorder_Level");
				double oldPrice = rs.getDouble("Selling_Price");
				boolean oldReq = rs.getBoolean("Requires_Prescription");
				int oldCategoryId = rs.getInt("Category_ID");

				rs.close();
				psSel.close();

				String finalName = (newName != null && !newName.isEmpty()) ? newName : oldName;
				String finalUnit = (newUnit != null && !newUnit.isEmpty()) ? newUnit : oldUnit;
				int finalReorder = (newReorder != -1) ? newReorder : oldReorder;
				double finalPrice = (newPrice != -1) ? newPrice : oldPrice;
				int finalCategoryId = (newCategoryId != -1) ? newCategoryId : oldCategoryId;

				boolean finalReq = newReq;

				PreparedStatement psUp = conn
						.prepareStatement("UPDATE Medicine SET Trade_Name = ?, Unit = ?, Reorder_Level = ?, "
								+ "Selling_Price = ?, Requires_Prescription = ?, Category_ID = ? "
								+ "WHERE Medicine_ID = ?");

				psUp.setString(1, finalName);
				psUp.setString(2, finalUnit);
				psUp.setInt(3, finalReorder);
				psUp.setDouble(4, finalPrice);
				psUp.setBoolean(5, finalReq);
				psUp.setInt(6, finalCategoryId);
				psUp.setInt(7, id);

				int rows = psUp.executeUpdate();
				psUp.close();

				if (rows > 0) {
					showInfo("Medicine updated successfully.");
					MedicineMng.loadMedicines(table);
					stagee.close();
				} else {
					showError("Update failed. No rows affected.");
				}

			} catch (NumberFormatException ex) {
				showError("ID, Reorder Level, Price, Category ID must be valid numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (Exception ex) {

				}
			}
		});

		btnCancel.setOnAction(e2 -> stagee.close());

		stagee.setScene(new Scene(root, 480, 330));
		stagee.showAndWait();
	}

	private void showError(String msg) {
		Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
		a.showAndWait();
	}

	private void showInfo(String msg) {
		Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		a.showAndWait();
	}

}
