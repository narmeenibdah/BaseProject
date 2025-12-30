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

public class SupplierMng {

    public static Parent getView() {

        TableView<Supplier> table = new TableView<>();

        TableColumn<Supplier, Integer> idCol = new TableColumn<>("Supplier ID");
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

        String styleGreen = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";

        Button btnAdd    = new Button("Add");
        Button btnDelete = new Button("Delete");
        Button btnUpdate = new Button("Update");
        Button btnSearch = new Button("Search by Name");
        Button btnAll    = new Button("All Suppliers");
       

        btnAdd.setStyle(styleGreen);
        btnDelete.setStyle(styleGreen);
        btnUpdate.setStyle(styleGreen);
        btnSearch.setStyle(styleGreen);
        btnAll.setStyle(styleGreen);
       

        FXForSupplier fx = new FXForSupplier();

        btnAdd.setOnAction(e    -> fx.addSupplier(table));
        btnDelete.setOnAction(e -> fx.deleteSupplier(table));
        btnUpdate.setOnAction(e -> fx.updateSupplier(table));
        btnSearch.setOnAction(e -> fx.searchSupplierByName(table));
        btnAll.setOnAction(e    -> loadAllSuppliers(table));
       

        HBox topButtons = new HBox(10, btnAdd, btnDelete, btnUpdate, btnSearch, btnAll);
        topButtons.setAlignment(Pos.CENTER);
        topButtons.setPadding(new Insets(10));

        VBox root = new VBox(15, title, topButtons, table);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color:#d8f3dc;");

       
        loadAllSuppliers(table);

        return root;
    }

   
    public static void loadAllSuppliers(TableView<Supplier> table) {
        table.getItems().clear();

        try {
            Connection conn = DBConnection.getConnection();

            String sql =
                "SELECT Supplier_ID, Name, Phone, Email " +
                "FROM Supplier " +
                "ORDER BY Name";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Supplier s = new Supplier(
                        rs.getInt("Supplier_ID"),
                        rs.getString("Name"),
                        rs.getString("Phone"),
                        rs.getString("Email")
                );
                table.getItems().add(s);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR,
                    "Failed to load suppliers.\n" + ex.getMessage(),
                    ButtonType.OK);
            a.showAndWait();
        }
    }

   
    public static void loadSuppliersWithPurchaseOrders(TableView<Supplier> table) {
        table.getItems().clear();

        try {
            Connection conn = DBConnection.getConnection();

            String sql =
                "SELECT DISTINCT s.Supplier_ID, s.Name, s.Phone, s.Email " +
                "FROM Supplier s " +
                "JOIN Purchase_Order po ON s.Supplier_ID = po.Supplier_ID " +
                "ORDER BY s.Name";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Supplier s = new Supplier(
                        rs.getInt("Supplier_ID"),
                        rs.getString("Name"),
                        rs.getString("Phone"),
                        rs.getString("Email")
                );
                table.getItems().add(s);
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            Alert a = new Alert(Alert.AlertType.ERROR,
                    "Failed to load suppliers with purchase orders.\n" + ex.getMessage(),
                    ButtonType.OK);
            a.showAndWait();
        }
    }
}
