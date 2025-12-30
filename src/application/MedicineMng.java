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

public class MedicineMng {

    // =========================
    // VIEW FOR TAB (بدون Stage)
    // =========================
    public static Parent getView() {

        TableView<Medicine> table = new TableView<>();

        TableColumn<Medicine, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));

        TableColumn<Medicine, String> nameCol = new TableColumn<>("Trade Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("tradeName"));

        TableColumn<Medicine, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<Medicine, Integer> reorderCol = new TableColumn<>("Reorder Level");
        reorderCol.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));

        TableColumn<Medicine, Integer> qtyCol = new TableColumn<>("Total Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));

        TableColumn<Medicine, Double> priceCol = new TableColumn<>("Selling Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Medicine, Boolean> reqCol = new TableColumn<>("Prescription Required");
        reqCol.setCellValueFactory(new PropertyValueFactory<>("requiresPrescription"));

        TableColumn<Medicine, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        table.getColumns().addAll(
                idCol, nameCol, unitCol, reorderCol,
                qtyCol, priceCol, reqCol, catCol
        );
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ===== Title =====
        Label title = new Label("Medicines Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Button btnAdd = new Button("Add");
        Button btnDelete = new Button("Delete");
        Button btnSearch = new Button("Search");
        Button btnUpdate = new Button("Update");

        Button btnAll = new Button("All Medicines");
        Button btnLow = new Button("Low Stock");

        String style = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";
        btnAdd.setStyle(style);
        btnDelete.setStyle(style);
        btnSearch.setStyle(style);
        btnUpdate.setStyle(style);
        btnAll.setStyle(style);
        btnLow.setStyle(style);

        // ===== Actions =====
        btnAdd.setOnAction(e -> new FXForMidicine().addMedicine(table));
        btnDelete.setOnAction(e -> new FXForMidicine().deleteMedicine(table));
        btnSearch.setOnAction(e -> new FXForMidicine().searchMedicine(table));
        btnUpdate.setOnAction(e -> new FXForMidicine().updateMedicine(table));

        btnAll.setOnAction(e -> loadMedicines(table));
        btnLow.setOnAction(e -> loadLowStockMedicines(table));

        // ===== Layout =====
        HBox crudButtons = new HBox(10, btnAdd, btnDelete, btnSearch, btnUpdate);
        crudButtons.setAlignment(Pos.CENTER);

        HBox queryButtons = new HBox(10, btnAll, btnLow);
        queryButtons.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, crudButtons, queryButtons, table);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color:#d8f3dc;");

        // Load Query 1 by default
        loadMedicines(table);

        return root;
    }

    // =========================
    // Query 1: All Medicines
    // =========================
    public static void loadMedicines(TableView<Medicine> table) {
        table.getItems().clear();
        try {
            Connection conn = DBConnection.getConnection();

            String sql =
                "SELECT m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
                "m.Selling_Price, m.Requires_Prescription, c.Name AS Category_Name, " +
                "IFNULL(SUM(b.Quantity),0) AS Total_Quantity " +
                "FROM Medicine m " +
                "JOIN Category c ON m.Category_ID = c.Category_ID " +
                "LEFT JOIN Batch b ON b.Medicine_ID = m.Medicine_ID " +
                "GROUP BY m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
                "m.Selling_Price, m.Requires_Prescription, c.Name";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Medicine m = new Medicine(
                        rs.getInt("Medicine_ID"),
                        rs.getString("Trade_Name"),
                        rs.getString("Unit"),
                        rs.getInt("Reorder_Level"),
                        rs.getDouble("Selling_Price"),
                        rs.getBoolean("Requires_Prescription"),
                        rs.getString("Category_Name")
                );
                m.setTotalQuantity(rs.getInt("Total_Quantity"));
                table.getItems().add(m);
            }

            rs.close(); ps.close(); conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // Query 2: Low Stock
    // =========================
    public static void loadLowStockMedicines(TableView<Medicine> table) {
        table.getItems().clear();
        try {
            Connection conn = DBConnection.getConnection();

            String sql =
                "SELECT m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
                "m.Selling_Price, m.Requires_Prescription, c.Name AS Category_Name, " +
                "SUM(b.Quantity) AS Total_Quantity " +
                "FROM Medicine m " +
                "JOIN Category c ON m.Category_ID = c.Category_ID " +
                "JOIN Batch b ON b.Medicine_ID = m.Medicine_ID " +
                "GROUP BY m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
                "m.Selling_Price, m.Requires_Prescription, c.Name " +
                "HAVING SUM(b.Quantity) < m.Reorder_Level";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Medicine m = new Medicine(
                        rs.getInt("Medicine_ID"),
                        rs.getString("Trade_Name"),
                        rs.getString("Unit"),
                        rs.getInt("Reorder_Level"),
                        rs.getDouble("Selling_Price"),
                        rs.getBoolean("Requires_Prescription"),
                        rs.getString("Category_Name")
                );
                m.setTotalQuantity(rs.getInt("Total_Quantity"));
                table.getItems().add(m);
            }

            rs.close(); ps.close(); conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
