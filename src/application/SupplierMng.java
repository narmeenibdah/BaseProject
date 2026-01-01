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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SupplierMng {

    public static Parent getView() {

        TableView<Supplier> table = new TableView<>();

        TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(idCol, nameCol, phoneCol, emailCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label title = new Label("Suppliers - Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        Button btnAdd = new Button("Add");
        Button btnDelete = new Button("Delete");
        Button btnSearch = new Button("Search");
        Button btnUpdate = new Button("Update");
        Button btnAll = new Button("All Suppliers");

        // ✅ Query 5 button (واضح للمستخدم)
        Button btnThisMonth = new Button("Suppliers with Purchase Orders This Month");

        String style = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";
        btnAdd.setStyle(style);
        btnDelete.setStyle(style);
        btnSearch.setStyle(style);
        btnUpdate.setStyle(style);
        btnAll.setStyle(style);
        btnThisMonth.setStyle(style);

        FXForSupplier fx = new FXForSupplier();

        btnAdd.setOnAction(e -> fx.addSupplier(table));
        btnDelete.setOnAction(e -> fx.deleteSupplier(table));

        // ✅ مهم: FXForSupplier عندك اسمها searchSupplierByName
        btnSearch.setOnAction(e -> fx.searchSupplierByName(table));

        btnUpdate.setOnAction(e -> fx.updateSupplier(table));
        btnAll.setOnAction(e -> loadSuppliers(table));

        // ✅ Query 5
        btnThisMonth.setOnAction(e -> loadSuppliersThisMonth(table));

        HBox buttons = new HBox(10, btnAdd, btnDelete, btnSearch, btnUpdate, btnAll, btnThisMonth);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, buttons, table);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color:#d8f3dc;");

        loadSuppliers(table);
        return root;
    }

    // ================= ALL SUPPLIERS =================
    public static void loadSuppliers(TableView<Supplier> table) {
        table.getItems().clear();

        String sql = "SELECT Supplier_ID, Name, Phone, Email FROM Supplier";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Supplier s = new Supplier(
                            rs.getInt("Supplier_ID"),
                            rs.getString("Name"),
                            rs.getString("Phone"),
                            rs.getString("Email")
                    );
                    table.getItems().add(s);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load suppliers.").show();
        }
    }

    // ✅ Alias عشان أي كود ثاني بينادي loadAllSuppliers ما يضرب
    public static void loadAllSuppliers(TableView<Supplier> table) {
        loadSuppliers(table);
    }

    // ================= QUERY 5 =================
    // Suppliers involved in purchase orders during the current month
    public static void loadSuppliersThisMonth(TableView<Supplier> table) {
        table.getItems().clear();

        String sql =
            "SELECT DISTINCT s.Supplier_ID, s.Name, s.Phone, s.Email " +
            "FROM Supplier s " +
            "JOIN Purchase_Order po ON po.Supplier_ID = s.Supplier_ID " +
            "WHERE MONTH(po.Date) = MONTH(CURDATE()) " +
            "AND YEAR(po.Date) = YEAR(CURDATE())";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Supplier s = new Supplier(
                            rs.getInt("Supplier_ID"),
                            rs.getString("Name"),
                            rs.getString("Phone"),
                            rs.getString("Email")
                    );
                    table.getItems().add(s);
                }
            }

            if (table.getItems().isEmpty()) {
                new Alert(
                    Alert.AlertType.INFORMATION,
                    "There are no suppliers with purchase orders in the current month."
                ).show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(
                Alert.AlertType.ERROR,
                "Failed to load suppliers with purchase orders for the current month."
            ).show();
        }
    }
}
