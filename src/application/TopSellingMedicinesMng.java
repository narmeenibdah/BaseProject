package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TopSellingMedicinesMng {

    public static Parent getView() {

        Label title = new Label("Top 10 Best-Selling Medicines");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

        DatePicker dpFrom = new DatePicker();
        dpFrom.setPromptText("From Date");

        DatePicker dpTo = new DatePicker();
        dpTo.setPromptText("To Date");

        Button btnSearch = new Button("Search");
        btnSearch.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");

        HBox filters = new HBox(10, dpFrom, dpTo, btnSearch);
        filters.setAlignment(Pos.CENTER);

        TableView<TopSellingMedicineView> table = new TableView<>();

        TableColumn<TopSellingMedicineView, Integer> idCol =
                new TableColumn<>("Medicine ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("medicineId"));

        TableColumn<TopSellingMedicineView, String> nameCol =
                new TableColumn<>("Trade Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("tradeName"));

        TableColumn<TopSellingMedicineView, Integer> soldCol =
                new TableColumn<>("Total Sold");
        soldCol.setCellValueFactory(new PropertyValueFactory<>("totalSold"));

        table.getColumns().addAll(idCol, nameCol, soldCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        btnSearch.setOnAction(e -> {
            if (dpFrom.getValue() == null || dpTo.getValue() == null) {
                new Alert(Alert.AlertType.WARNING, "Please select From and To dates.").show();
                return;
            }

            loadData(table,
                    dpFrom.getValue().toString(),
                    dpTo.getValue().toString());
        });

        VBox root = new VBox(15, title, filters, table);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30, 15, 15, 15));
        root.setStyle("-fx-background-color:#d8f3dc;");

        return root;
    }

    private static void loadData(TableView<TopSellingMedicineView> table,
                                 String from, String to) {

        table.getItems().clear();

        String fromDT = from + " 00:00:00";
        String toDT   = to   + " 23:59:59";

        String sql =
            "SELECT m.Medicine_ID, m.Trade_Name, SUM(si.Quantity) AS Total_Sold " +
            "FROM Sale s " +
            "JOIN Sale_Item si ON s.Sale_ID = si.Sale_ID " +
            "JOIN Medicine m ON si.Medicine_ID = m.Medicine_ID " +
            "WHERE s.Sale_Date BETWEEN ? AND ? " +
            "GROUP BY m.Medicine_ID, m.Trade_Name " +
            "ORDER BY Total_Sold DESC " +
            "LIMIT 10";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fromDT);
            ps.setString(2, toDT);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    table.getItems().add(
                        new TopSellingMedicineView(
                            rs.getInt("Medicine_ID"),
                            rs.getString("Trade_Name"),
                            rs.getInt("Total_Sold")
                        )
                    );
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to load top selling medicines.").show();
        }
    }
}
