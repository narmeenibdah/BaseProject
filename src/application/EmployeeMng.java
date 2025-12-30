package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EmployeeMng {

    public static Parent getView() {

        TableView<Employee> table = new TableView<>();

        TableColumn<Employee, Integer> idCol = new TableColumn<>("Employee ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

        TableColumn<Employee, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<Employee, Double> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        table.getColumns().addAll(idCol, nameCol, roleCol, salaryCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label title = new Label("Employees - Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        String style = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";

        Button btnAdd    = new Button("Add");
        Button btnDelete = new Button("Delete");
        Button btnSearch = new Button("Search by ID");
        Button btnUpdate = new Button("Update");
        Button btnLoad   = new Button("All Employees");

        btnAdd.setStyle(style);
        btnDelete.setStyle(style);
        btnSearch.setStyle(style);
        btnUpdate.setStyle(style);
        btnLoad.setStyle(style);

        FXForEmployee fx = new FXForEmployee();

        btnAdd.setOnAction(e    -> fx.addEmployee(table));
        btnDelete.setOnAction(e -> fx.deleteEmployee(table));
        btnSearch.setOnAction(e -> fx.searchEmployee(table));   
        btnUpdate.setOnAction(e -> fx.updateEmployee(table));
        btnLoad.setOnAction(e   -> loadEmployees(table));

        HBox crudButtons = new HBox(10, btnAdd, btnDelete, btnSearch, btnUpdate);
        crudButtons.setAlignment(Pos.CENTER);
        crudButtons.setPadding(new Insets(10));

        HBox queryButtons = new HBox(10, btnLoad);
        queryButtons.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, crudButtons, queryButtons, table);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color:#d8f3dc;");

      
        loadEmployees(table);

        return root;
    }

   
    public static void loadEmployees(TableView<Employee> table) {
        table.getItems().clear();

        try {
            Connection conn = DBConnection.getConnection();

            String sql =
                "SELECT Employee_ID, Name, Role, Salary " +
                "FROM Employee " +
                "ORDER BY Name";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getInt("Employee_ID"),
                        rs.getString("Name"),
                        rs.getString("Role"),
                        rs.getDouble("Salary")
                );
                table.getItems().add(emp);
            }

            rs.close();
            ps.close();
            conn.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();

            Alert a = new Alert(Alert.AlertType.ERROR,
                    "Failed to load employees.\n" + ex.getMessage(),
                    ButtonType.OK);
            a.showAndWait();
        }
    }
}
