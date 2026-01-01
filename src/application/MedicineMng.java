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

        table.getColumns().addAll(idCol, nameCol, unitCol, reorderCol, qtyCol, priceCol, reqCol, catCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label title = new Label("Medicines - Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        Button btnAdd = new Button("Add");
        Button btnDelete = new Button("Delete");
        Button btnSearch = new Button("Search");
        Button btnUpdate = new Button("Update");

        Button btnAll = new Button("All Medicines");
        Button btnLow = new Button("Low Stock");
        Button btnReq = new Button("Prescription Required");

        String style = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";
        btnAdd.setStyle(style);
        btnDelete.setStyle(style);
        btnSearch.setStyle(style);
        btnUpdate.setStyle(style);
        btnAll.setStyle(style);
        btnLow.setStyle(style);
        btnReq.setStyle(style);

        btnAdd.setOnAction(e -> new FXForMedicine().addMedicine(table));
        btnDelete.setOnAction(e -> new FXForMedicine().deleteMedicine(table));
        btnSearch.setOnAction(e -> new FXForMedicine().searchMedicine(table));
        btnUpdate.setOnAction(e -> new FXForMedicine().updateMedicine(table));

        btnAll.setOnAction(e -> loadMedicines(table));
        btnLow.setOnAction(e -> loadLowStockMedicines(table));
        btnReq.setOnAction(e -> loadPrescriptionMedicines(table));

        HBox crudButtons = new HBox(10, btnAdd, btnDelete, btnSearch, btnUpdate);
        crudButtons.setAlignment(Pos.CENTER);

        HBox queryButtons = new HBox(10, btnAll, btnLow, btnReq);
        queryButtons.setAlignment(Pos.CENTER);

        VBox root = new VBox(15, title, crudButtons, queryButtons, table);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color:#d8f3dc;");

        loadMedicines(table);

        return root;
    }

    public static void loadMedicines(TableView<Medicine> table) {
        table.getItems().clear();

        String sql =
            "SELECT m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
            "       m.Selling_Price, m.Requires_Prescription, c.Name AS Category_Name, " +
            "       IFNULL(SUM(b.Quantity),0) AS Total_Quantity " +
            "FROM Medicine m " +
            "JOIN Category c ON m.Category_ID = c.Category_ID " +
            "LEFT JOIN Batch b ON b.Medicine_ID = m.Medicine_ID " +
            "GROUP BY m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
            "         m.Selling_Price, m.Requires_Prescription, c.Name";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadLowStockMedicines(TableView<Medicine> table) {
        table.getItems().clear();

        // improved: LEFT JOIN so medicines with 0 batches can appear as low stock too
        String sql =
            "SELECT m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
            "       m.Selling_Price, m.Requires_Prescription, c.Name AS Category_Name, " +
            "       IFNULL(SUM(b.Quantity),0) AS Total_Quantity " +
            "FROM Medicine m " +
            "JOIN Category c ON m.Category_ID = c.Category_ID " +
            "LEFT JOIN Batch b ON b.Medicine_ID = m.Medicine_ID " +
            "GROUP BY m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
            "         m.Selling_Price, m.Requires_Prescription, c.Name " +
            "HAVING IFNULL(SUM(b.Quantity),0) < m.Reorder_Level";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadPrescriptionMedicines(TableView<Medicine> table) {
        table.getItems().clear();

        String sql =
            "SELECT m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
            "       m.Selling_Price, m.Requires_Prescription, c.Name AS Category_Name, " +
            "       IFNULL(SUM(b.Quantity), 0) AS Total_Quantity " +
            "FROM Medicine m " +
            "JOIN Category c ON m.Category_ID = c.Category_ID " +
            "LEFT JOIN Batch b ON b.Medicine_ID = m.Medicine_ID " +
            "WHERE m.Requires_Prescription = TRUE " +
            "GROUP BY m.Medicine_ID, m.Trade_Name, m.Unit, m.Reorder_Level, " +
            "         m.Selling_Price, m.Requires_Prescription, c.Name";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
