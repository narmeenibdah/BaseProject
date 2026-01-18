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

public class EmployeeMng {

	public static Parent getView() {

		TableView<Employee> table = new TableView<>();

		TableColumn<Employee, Integer> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

		TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
		roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

		TableColumn<Employee, Double> salCol = new TableColumn<>("Salary");
		salCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

		TableColumn<Employee, String> branchCol = new TableColumn<>("Branch");
		branchCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

		table.getColumns().addAll(idCol, nameCol, roleCol, salCol, branchCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Label title = new Label("Employees - Management");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

		Button btnAdd = new Button("Add");
		Button btnDelete = new Button("Delete");
		Button btnSearch = new Button("Search");
		Button btnUpdate = new Button("Update");
		Button btnAll = new Button("All Employees");

		String style = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";
		btnAdd.setStyle(style);
		btnDelete.setStyle(style);
		btnSearch.setStyle(style);
		btnUpdate.setStyle(style);
		btnAll.setStyle(style);

		FXForEmployee fx = new FXForEmployee();

		btnAdd.setOnAction(e -> fx.addEmployee(table));
		btnDelete.setOnAction(e -> fx.deleteEmployee(table));
		btnSearch.setOnAction(e -> fx.searchEmployee(table));
		btnUpdate.setOnAction(e -> fx.updateEmployee(table));
		btnAll.setOnAction(e -> loadEmployees(table));

		HBox buttons = new HBox(10, btnAdd, btnDelete, btnSearch, btnUpdate, btnAll);
		buttons.setAlignment(Pos.CENTER);

		VBox root = new VBox(15, title, buttons, table);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.TOP_CENTER);
		root.setStyle("-fx-background-color:#d8f3dc;");

		loadEmployees(table);
		return root;
	}

	public static void loadEmployees(TableView<Employee> table) {// 17
		table.getItems().clear();
		String sql = "SELECT e.Employee_ID, e.Name, e.Role, e.Salary, b.Branch_name AS Branch_Name "
				+ "FROM Employee e " + "LEFT JOIN Branch b ON e.Branch_ID = b.Branch_ID";

		try (Connection conn = DBConnection.getConnection()) {
			if (conn == null)
				return;

			try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					Employee emp = new Employee(rs.getInt("Employee_ID"), rs.getString("Name"), rs.getString("Role"),
							rs.getDouble("Salary"), rs.getString("Branch_Name"));
					table.getItems().add(emp);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load employees:\n" + ex.getMessage()).show();
		}
	}
}
