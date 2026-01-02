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

public class FXForSale {

	public void salesByDate(TableView<Sale> table) {

		Stage stage = new Stage();
		stage.setTitle("Sales by Date");

		Label lblY = new Label("Year:");
		TextField tfY = new TextField();

		Label lblM = new Label("Month:");
		TextField tfM = new TextField();

		Label lblD = new Label("Day:");
		TextField tfD = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.add(lblY, 0, 0);
		grid.add(tfY, 1, 0);
		grid.add(lblM, 0, 1);
		grid.add(tfM, 1, 1);
		grid.add(lblD, 0, 2);
		grid.add(tfD, 1, 2);

		Button btnShow = new Button("Show");
		Button btnCancel = new Button("Cancel");

		btnShow.setStyle("-fx-background-color: #52b788; -fx-text-fill: white; -fx-font-weight: bold;");
		btnCancel.setStyle("-fx-background-color: #cccccc; -fx-font-weight: bold;");

		HBox actions = new HBox(10, btnShow, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color: #f1faee;");

		btnShow.setOnAction(e -> {
			try {
				int y = Integer.parseInt(tfY.getText().trim());
				int m = Integer.parseInt(tfM.getText().trim());
				int d = Integer.parseInt(tfD.getText().trim());

				table.getItems().clear();

				String sql = "SELECT Sale_ID, Sale_Date, Total_Amount, Branch_ID, Employee_ID, Customer_ID "
						+ "FROM Sale " + "WHERE YEAR(Sale_Date)=? AND MONTH(Sale_Date)=? AND DAY(Sale_Date)=? "
						+ "ORDER BY Sale_Date";

				try (Connection conn = DBConnection.getConnection();
						PreparedStatement ps = conn.prepareStatement(sql)) {

					ps.setInt(1, y);
					ps.setInt(2, m);
					ps.setInt(3, d);

					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) {
							table.getItems()
									.add(new Sale(rs.getInt("Sale_ID"), rs.getString("Sale_Date"),
											rs.getDouble("Total_Amount"), rs.getInt("Branch_ID"),
											rs.getInt("Employee_ID"), rs.getInt("Customer_ID")));
						}
					}
				}

				if (table.getItems().isEmpty()) {
					showInfo("No sales found on this date.");
				} else {
					stage.close();
				}

			} catch (NumberFormatException ex) {
				showError("Year, Month, and Day must be valid numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e2 -> stage.close());

		stage.setScene(new Scene(root, 360, 220));
		stage.showAndWait();
	}

	private void showError(String msg) {
		Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
		a.showAndWait();
	}

	private void showInfo(String msg) {
		Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		a.showAndWait();
	}

	public void addSale(TableView<Sale> table) {

		Stage stage = new Stage();
		stage.setTitle("Add New Sale");

		Label lblDate = new Label("Sale Date:");
		TextField tfDate = new TextField();

		Label lblTotal = new Label("Total Amount:");
		TextField tfTotal = new TextField();

		Label lblBranch = new Label("Branch ID:");
		TextField tfBranch = new TextField();

		Label lblEmp = new Label("Employee ID:");
		TextField tfEmp = new TextField();

		Label lblCust = new Label("Customer ID:");
		TextField tfCust = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.add(lblDate, 0, 0);
		grid.add(tfDate, 1, 0);
		grid.add(lblTotal, 0, 1);
		grid.add(tfTotal, 1, 1);
		grid.add(lblBranch, 0, 2);
		grid.add(tfBranch, 1, 2);
		grid.add(lblEmp, 0, 3);
		grid.add(tfEmp, 1, 3);
		grid.add(lblCust, 0, 4);
		grid.add(tfCust, 1, 4);

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
				String saleDate = tfDate.getText().trim();
				String totalText = tfTotal.getText().trim();
				String bText = tfBranch.getText().trim();
				String eText = tfEmp.getText().trim();
				String cText = tfCust.getText().trim();

				if (saleDate.isEmpty()) {
					showError("Sale Date cannot be empty.");
					return;
				}
				if (bText.isEmpty() || eText.isEmpty() || cText.isEmpty()) {
					showError("Branch ID, Employee ID, Customer ID cannot be empty.");
					return;
				}

				double total = 0.0;
				if (!totalText.isEmpty()) {
					total = Double.parseDouble(totalText);
				}

				int branchId = Integer.parseInt(bText);
				int empId = Integer.parseInt(eText);
				int custId = Integer.parseInt(cText);

				Connection conn = DBConnection.getConnection();

				PreparedStatement chB = conn.prepareStatement("SELECT 1 FROM Branch WHERE Branch_ID = ?");
				chB.setInt(1, branchId);
				ResultSet rsB = chB.executeQuery();
				if (!rsB.next()) {
					rsB.close();
					chB.close();
					conn.close();
					showError("Branch ID not found.");
					return;
				}
				rsB.close();
				chB.close();

				PreparedStatement chE = conn.prepareStatement("SELECT 1 FROM Employee WHERE Employee_ID = ?");
				chE.setInt(1, empId);
				ResultSet rsE = chE.executeQuery();
				if (!rsE.next()) {
					rsE.close();
					chE.close();
					conn.close();
					showError("Employee ID not found.");
					return;
				}
				rsE.close();
				chE.close();

				PreparedStatement chC = conn.prepareStatement("SELECT 1 FROM Customer WHERE Customer_ID = ?");
				chC.setInt(1, custId);
				ResultSet rsC = chC.executeQuery();
				if (!rsC.next()) {
					rsC.close();
					chC.close();
					conn.close();
					showError("Customer ID not found.");
					return;
				}
				rsC.close();
				chC.close();

				PreparedStatement ps = conn.prepareStatement(
						"INSERT INTO Sale (Sale_Date, Total_Amount, Branch_ID, Employee_ID, Customer_ID) VALUES (?, ?, ?, ?, ?)");
				ps.setString(1, saleDate);
				ps.setDouble(2, total);
				ps.setInt(3, branchId);
				ps.setInt(4, empId);
				ps.setInt(5, custId);

				int rows = ps.executeUpdate();
				ps.close();
				conn.close();

				if (rows > 0) {
					showInfo("Sale added successfully.");
					SaleMng.loadAllSales(table);
					stage.close();
				} else {
					showError("Failed to add sale.");
				}

			} catch (NumberFormatException ex) {
				showError("Total Amount, IDs must be valid numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e2 -> stage.close());

		stage.setScene(new Scene(root, 520, 320));
		stage.showAndWait();
	}

	public void deleteSale(TableView<Sale> table) {

		Stage stage = new Stage();
		stage.setTitle("Delete Sale");

		Label lblId = new Label("Sale ID:");
		TextField tfId = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));
		grid.add(lblId, 0, 0);
		grid.add(tfId, 1, 0);

		Button btnDelete = new Button("Delete");
		Button btnCancel = new Button("Cancel");

		btnDelete.setStyle("-fx-background-color:#e63946; -fx-text-fill:white; -fx-font-weight:bold;");
		btnCancel.setStyle("-fx-background-color:#cccccc; -fx-font-weight:bold;");

		HBox actions = new HBox(10, btnDelete, btnCancel);
		actions.setAlignment(Pos.CENTER_RIGHT);

		VBox root = new VBox(15, grid, actions);
		root.setPadding(new Insets(15));
		root.setStyle("-fx-background-color: #f1faee;");

		btnDelete.setOnAction(e -> {
			try {
				int saleId = Integer.parseInt(tfId.getText().trim());

				Connection conn = DBConnection.getConnection();

				PreparedStatement ch1 = conn
						.prepareStatement("SELECT COUNT(*) AS cnt FROM Sale_Item WHERE Sale_ID = ?");
				ch1.setInt(1, saleId);
				ResultSet rs1 = ch1.executeQuery();
				rs1.next();
				int itemCnt = rs1.getInt("cnt");
				rs1.close();
				ch1.close();

				if (itemCnt > 0) {
					conn.close();
					showError("Cannot delete: Sale has Sale Items (" + itemCnt + ").");
					return;
				}

				PreparedStatement ch2 = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM Payment WHERE Sale_ID = ?");
				ch2.setInt(1, saleId);
				ResultSet rs2 = ch2.executeQuery();
				rs2.next();
				int payCnt = rs2.getInt("cnt");
				rs2.close();
				ch2.close();

				if (payCnt > 0) {
					conn.close();
					showError("Cannot delete: Sale has Payments (" + payCnt + ").");
					return;
				}

				PreparedStatement ps = conn.prepareStatement("DELETE FROM Sale WHERE Sale_ID = ?");
				ps.setInt(1, saleId);

				int rows = ps.executeUpdate();
				ps.close();
				conn.close();

				if (rows > 0) {
					showInfo("Sale deleted successfully.");
					SaleMng.loadAllSales(table);
					stage.close();
				} else {
					showError("No sale found with this ID.");
				}

			} catch (NumberFormatException ex) {
				showError("Sale ID must be a number.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e2 -> stage.close());

		stage.setScene(new Scene(root, 360, 160));
		stage.showAndWait();
	}

	public void updateSale(TableView<Sale> table) {

		Stage stage = new Stage();
		stage.setTitle("Update Sale");

		Label lblId = new Label("Sale ID:");
		TextField tfId = new TextField();

		Label lblDate = new Label("New Sale Date (optional):");
		TextField tfDate = new TextField();

		Label lblTotal = new Label("New Total Amount (optional):");
		TextField tfTotal = new TextField();

		Label lblBranch = new Label("New Branch ID (optional):");
		TextField tfBranch = new TextField();

		Label lblEmp = new Label("New Employee ID (optional):");
		TextField tfEmp = new TextField();

		Label lblCust = new Label("New Customer ID (optional):");
		TextField tfCust = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.add(lblId, 0, 0);
		grid.add(tfId, 1, 0);
		grid.add(lblDate, 0, 1);
		grid.add(tfDate, 1, 1);
		grid.add(lblTotal, 0, 2);
		grid.add(tfTotal, 1, 2);
		grid.add(lblBranch, 0, 3);
		grid.add(tfBranch, 1, 3);
		grid.add(lblEmp, 0, 4);
		grid.add(tfEmp, 1, 4);
		grid.add(lblCust, 0, 5);
		grid.add(tfCust, 1, 5);

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
			try {
				int saleId = Integer.parseInt(tfId.getText().trim());

				Connection conn = DBConnection.getConnection();

				PreparedStatement psSel = conn.prepareStatement(
						"SELECT Sale_Date, Total_Amount, Branch_ID, Employee_ID, Customer_ID FROM Sale WHERE Sale_ID = ?");
				psSel.setInt(1, saleId);
				ResultSet rs = psSel.executeQuery();

				if (!rs.next()) {
					rs.close();
					psSel.close();
					conn.close();
					showError("Sale not found with this ID.");
					return;
				}

				String oldDate = rs.getString("Sale_Date");
				double oldTotal = rs.getDouble("Total_Amount");
				int oldBranch = rs.getInt("Branch_ID");
				int oldEmp = rs.getInt("Employee_ID");
				int oldCust = rs.getInt("Customer_ID");

				rs.close();
				psSel.close();

				String newDateText = tfDate.getText();
				String newTotalText = tfTotal.getText();
				String newBranchText = tfBranch.getText();
				String newEmpText = tfEmp.getText();
				String newCustText = tfCust.getText();

				String finalDate;
				if (newDateText != null && !newDateText.trim().isEmpty()) {
					finalDate = newDateText.trim();
				} else {
					finalDate = oldDate;
				}

				double finalTotal;
				if (newTotalText != null && !newTotalText.trim().isEmpty()) {
					finalTotal = Double.parseDouble(newTotalText.trim());
				} else {
					finalTotal = oldTotal;
				}

				int finalBranch;
				if (newBranchText != null && !newBranchText.trim().isEmpty()) {
					finalBranch = Integer.parseInt(newBranchText.trim());
				} else {
					finalBranch = oldBranch;
				}

				int finalEmp;
				if (newEmpText != null && !newEmpText.trim().isEmpty()) {
					finalEmp = Integer.parseInt(newEmpText.trim());
				} else {
					finalEmp = oldEmp;
				}

				int finalCust;
				if (newCustText != null && !newCustText.trim().isEmpty()) {
					finalCust = Integer.parseInt(newCustText.trim());
				} else {
					finalCust = oldCust;
				}

				PreparedStatement chB = conn.prepareStatement("SELECT 1 FROM Branch WHERE Branch_ID = ?");
				chB.setInt(1, finalBranch);
				ResultSet rsB = chB.executeQuery();
				if (!rsB.next()) {
					rsB.close();
					chB.close();
					conn.close();
					showError("Branch ID not found.");
					return;
				}
				rsB.close();
				chB.close();

				PreparedStatement chE = conn.prepareStatement("SELECT 1 FROM Employee WHERE Employee_ID = ?");
				chE.setInt(1, finalEmp);
				ResultSet rsE = chE.executeQuery();
				if (!rsE.next()) {
					rsE.close();
					chE.close();
					conn.close();
					showError("Employee ID not found.");
					return;
				}
				rsE.close();
				chE.close();

				PreparedStatement chC = conn.prepareStatement("SELECT 1 FROM Customer WHERE Customer_ID = ?");
				chC.setInt(1, finalCust);
				ResultSet rsC = chC.executeQuery();
				if (!rsC.next()) {
					rsC.close();
					chC.close();
					conn.close();
					showError("Customer ID not found.");
					return;
				}
				rsC.close();
				chC.close();

				PreparedStatement psUp = conn.prepareStatement(
						"UPDATE Sale SET Sale_Date = ?, Total_Amount = ?, Branch_ID = ?, Employee_ID = ?, Customer_ID = ? WHERE Sale_ID = ?");
				psUp.setString(1, finalDate);
				psUp.setDouble(2, finalTotal);
				psUp.setInt(3, finalBranch);
				psUp.setInt(4, finalEmp);
				psUp.setInt(5, finalCust);
				psUp.setInt(6, saleId);

				int rows = psUp.executeUpdate();
				psUp.close();
				conn.close();

				if (rows > 0) {
					showInfo("Sale updated successfully.");
					SaleMng.loadAllSales(table);
					stage.close();
				} else {
					showError("Update failed. No rows affected.");
				}

			} catch (NumberFormatException ex) {
				showError("IDs and Total Amount must be valid numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e2 -> stage.close());

		stage.setScene(new Scene(root, 560, 360));
		stage.showAndWait();
	}

	public void searchSale(TableView<Sale> table) {

		Stage stage = new Stage();
		stage.setTitle("Search Sale by ID");

		Label lblId = new Label("Sale ID:");
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
			try {
				int saleId = Integer.parseInt(tfId.getText().trim());
				table.getItems().clear();

				Connection conn = DBConnection.getConnection();

				PreparedStatement ps = conn.prepareStatement(
						"SELECT Sale_ID, Sale_Date, Total_Amount, Branch_ID, Employee_ID, Customer_ID FROM Sale WHERE Sale_ID = ?");
				ps.setInt(1, saleId);
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					table.getItems()
							.add(new Sale(rs.getInt("Sale_ID"), rs.getString("Sale_Date"), rs.getDouble("Total_Amount"),
									rs.getInt("Branch_ID"), rs.getInt("Employee_ID"), rs.getInt("Customer_ID")));
					stage.close();
				} else {
					showInfo("No sale found with this ID.");
				}

				rs.close();
				ps.close();
				conn.close();

			} catch (NumberFormatException ex) {
				showError("Sale ID must be a valid number.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e2 -> stage.close());

		stage.setScene(new Scene(root, 360, 170));
		stage.showAndWait();
	}
	
	

}
