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
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXForEmployee {

	public void addEmployee(TableView<Employee> table) {

	    Stage stage = new Stage();
	    stage.setTitle("Add Employee");

	    Label lblName = new Label("Name:");
	    TextField tfName = new TextField();

	    Label lblSalary = new Label("Salary:");
	    TextField tfSalary = new TextField();

	    Label lblRole = new Label("Role:");
	    TextField tfRole = new TextField();

	    Label lblBranch = new Label("Branch ID:");
	    TextField tfBranch = new TextField();

	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(15));

	    grid.add(lblName, 0, 0);
	    grid.add(tfName, 1, 0);

	    grid.add(lblSalary, 0, 1);
	    grid.add(tfSalary, 1, 1);

	    grid.add(lblRole, 0, 2);
	    grid.add(tfRole, 1, 2);

	    grid.add(lblBranch, 0, 3);
	    grid.add(tfBranch, 1, 3);

	    Button btnAdd = new Button("Add");
	    Button btnCancel = new Button("Cancel");

	    btnAdd.setStyle("-fx-background-color: #52b788; -fx-text-fill: white; -fx-font-weight: bold;");
	    btnCancel.setStyle("-fx-background-color: #cccccc; -fx-font-weight: bold;");

	    HBox actions = new HBox(10, btnAdd, btnCancel);
	    actions.setAlignment(Pos.CENTER_RIGHT);

	    VBox root = new VBox(15, grid, actions);
	    root.setPadding(new Insets(15));
	    root.setStyle("-fx-background-color: #f1faee;");

	    btnAdd.setOnAction(e -> {
	        try {
	            String name = tfName.getText().trim();
	            String role = tfRole.getText().trim();
	            String salaryTxt = tfSalary.getText().trim();
	            String branchTxt = tfBranch.getText().trim();

	            if (name.isEmpty() || salaryTxt.isEmpty() || branchTxt.isEmpty()) {
	                showError("Name, Salary, and Branch ID are required.");
	                return;
	            }

	            double salary = Double.parseDouble(salaryTxt);
	            int branchId = Integer.parseInt(branchTxt);

	            if (salary < 0) {
	                showError("Salary must be >= 0.");
	                return;
	            }

	            String sql =
	                "INSERT INTO Employee (Name, Salary, Role, Branch_ID) VALUES (?, ?, ?, ?)";

	            try (Connection conn = DBConnection.getConnection()) {
	                if (conn == null) return;

	                try (PreparedStatement ps = conn.prepareStatement(sql)) {
	                    ps.setString(1, name);
	                    ps.setDouble(2, salary);
	                    ps.setString(3, role.isEmpty() ? null : role);
	                    ps.setInt(4, branchId);

	                    int rows = ps.executeUpdate();
	                    if (rows > 0) {
	                        showInfo("Employee added successfully.");
	                        EmployeeMng.loadEmployees(table);
	                        stage.close();
	                    } else {
	                        showError("Insert failed.");
	                    }
	                }
	            }

	        } catch (NumberFormatException ex) {
	            showError("Salary and Branch ID must be valid numbers.");
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            showError("Unexpected error: " + ex.getMessage());
	        }
	    });

	    btnCancel.setOnAction(e2 -> stage.close());

	    stage.setScene(new Scene(root, 420, 260));
	    stage.showAndWait();
	}


	public void deleteEmployee(TableView<Employee> table) {

		Stage stage = new Stage();
		stage.setTitle("Delete Employee");

		Label lblId = new Label("Employee ID:");
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

		btnDelete.setOnAction(e -> {
			try {
				int id = Integer.parseInt(tfId.getText().trim());

				Connection conn = DBConnection.getConnection();

				PreparedStatement ch = conn.prepareStatement("SELECT COUNT(*) AS cnt FROM Sale WHERE Employee_ID = ?");
				ch.setInt(1, id);
				ResultSet rs = ch.executeQuery();
				rs.next();
				int saleCnt = rs.getInt("cnt");
				rs.close();
				ch.close();

				if (saleCnt > 0) {
					conn.close();
					showError("Cannot delete: Employee is linked to Sales (" + saleCnt + ").");
					return;
				}

				PreparedStatement ps = conn.prepareStatement("DELETE FROM Employee WHERE Employee_ID = ?");
				ps.setInt(1, id);
				int rows = ps.executeUpdate();
				ps.close();
				conn.close();

				if (rows > 0) {
					showInfo("Employee deleted successfully.");
					EmployeeMng.loadEmployees(table);
					stage.close();
				} else {
					showError("No employee found with this ID.");
				}

			} catch (NumberFormatException ex) {
				showError("Employee ID must be a number.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e2 -> stage.close());

		stage.setScene(new Scene(root, 360, 160));
		stage.showAndWait();
	}

	public void updateEmployee(TableView<Employee> table) {

		Stage stage = new Stage();
		stage.setTitle("Update Employee");

		Label lblId = new Label("Employee ID:");
		TextField tfId = new TextField();

		Label lblName = new Label("New Name:");
		TextField tfName = new TextField();

		Label lblRole = new Label("New Role:");
		TextField tfRole = new TextField();

		Label lblSalary = new Label("New Salary:");
		TextField tfSalary = new TextField();

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(15));

		grid.add(lblId, 0, 0);
		grid.add(tfId, 1, 0);
		grid.add(lblName, 0, 1);
		grid.add(tfName, 1, 1);
		grid.add(lblRole, 0, 2);
		grid.add(tfRole, 1, 2);
		grid.add(lblSalary, 0, 3);
		grid.add(tfSalary, 1, 3);

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
				int id = Integer.parseInt(tfId.getText().trim());

				Connection conn = DBConnection.getConnection();

				PreparedStatement psSel = conn
						.prepareStatement("SELECT Name, Role, Salary FROM Employee WHERE Employee_ID = ?");
				psSel.setInt(1, id);
				ResultSet rs = psSel.executeQuery();

				if (!rs.next()) {
					showError("Employee not found with this ID.");
					rs.close();
					psSel.close();
					conn.close();
					return;
				}

				String oldName = rs.getString("Name");
				String oldRole = rs.getString("Role");
				double oldSalary = rs.getDouble("Salary");

				rs.close();
				psSel.close();

				String newName = tfName.getText();
				String newRole = tfRole.getText();
				String sText = tfSalary.getText();

				String finalName;
				if (newName != null && !newName.trim().isEmpty()) {
					finalName = newName.trim();
				} else {
					finalName = oldName;
				}

				String finalRole;
				if (newRole != null && !newRole.trim().isEmpty()) {
					finalRole = newRole.trim();
				} else {
					finalRole = oldRole;
				}

				double finalSalary;
				if (sText != null && !sText.trim().isEmpty()) {
					finalSalary = Double.parseDouble(sText.trim());
				} else {
					finalSalary = oldSalary;
				}

				PreparedStatement psUp = conn
						.prepareStatement("UPDATE Employee SET Name = ?, Role = ?, Salary = ? WHERE Employee_ID = ?");
				psUp.setString(1, finalName);
				psUp.setString(2, finalRole);
				psUp.setDouble(3, finalSalary);
				psUp.setInt(4, id);

				int rows = psUp.executeUpdate();
				psUp.close();
				conn.close();

				if (rows > 0) {
					showInfo("Employee updated successfully.");
					EmployeeMng.loadEmployees(table);
					stage.close();
				} else {
					showError("Update failed. No rows affected.");
				}

			} catch (NumberFormatException ex) {
				showError("ID and Salary must be valid numbers.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Unexpected error: " + ex.getMessage());
			}
		});

		btnCancel.setOnAction(e2 -> stage.close());

		stage.setScene(new Scene(root, 420, 230));
		stage.showAndWait();
	}

	public void searchEmployee(TableView<Employee> table) {

	    Stage stage = new Stage();
	    stage.setTitle("Search Employee by ID");

	    Label lblId = new Label("Employee ID:");
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
	            int id = Integer.parseInt(tfId.getText().trim());
	            table.getItems().clear();

	            String sql =
	                "SELECT e.Employee_ID, e.Name, e.Role, e.Salary, b.Branch_name AS Branch_Name " +
	                "FROM Employee e " +
	                "LEFT JOIN Branch b ON e.Branch_ID = b.Branch_ID " +
	                "WHERE e.Employee_ID = ?";

	            try (Connection conn = DBConnection.getConnection()) {
	                if (conn == null) return;

	                try (PreparedStatement ps = conn.prepareStatement(sql)) {
	                    ps.setInt(1, id);

	                    try (ResultSet rs = ps.executeQuery()) {
	                        if (rs.next()) {
	                            Employee emp = new Employee(
	                                rs.getInt("Employee_ID"),
	                                rs.getString("Name"),
	                                rs.getString("Role"),
	                                rs.getDouble("Salary"),
	                                rs.getString("Branch_Name") // ممكن تكون null إذا ما مربوط
	                            );
	                            table.getItems().add(emp);
	                            stage.close();
	                        } else {
	                            showInfo("No employee found with this ID.");
	                        }
	                    }
	                }
	            }

	        } catch (NumberFormatException ex) {
	            showError("Employee ID must be a valid number.");
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            showError("Unexpected error: " + ex.getMessage());
	        }
	    });

	    btnCancel.setOnAction(e2 -> stage.close());

	    stage.setScene(new Scene(root, 360, 170));
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
}
