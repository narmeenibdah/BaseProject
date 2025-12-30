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

public class FXForSupplier {

   
    public void addSupplier(TableView<Supplier> table) {

        Stage stage = new Stage();
        stage.setTitle("Add New Supplier");

        Label lblName  = new Label("Name:");
        TextField tfName = new TextField();

        Label lblPhone = new Label("Phone:");
        TextField tfPhone = new TextField();

        Label lblEmail = new Label("Email:");
        TextField tfEmail = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(lblName,  0, 0); grid.add(tfName,  1, 0);
        grid.add(lblPhone, 0, 1); grid.add(tfPhone, 1, 1);
        grid.add(lblEmail, 0, 2); grid.add(tfEmail, 1, 2);

        Button btnAdd    = new Button("Add");
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
                String name  = tfName.getText().trim();
                String phone = tfPhone.getText().trim();
                String email = tfEmail.getText().trim();

                if (name.isEmpty()) {
                    showError("Name cannot be empty.");
                    return;
                }

                Connection conn = DBConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO Supplier (Name, Phone, Email) VALUES (?, ?, ?)");
                ps.setString(1, name);
            
                if (phone != null && !phone.trim().isEmpty()) {
                    ps.setString(2, phone.trim());
                } else {
                    ps.setString(2, null);
                }

               
                if (email != null && !email.trim().isEmpty()) {
                    ps.setString(3, email.trim());
                } else {
                    ps.setString(3, null);
                }


                int rows = ps.executeUpdate();
                ps.close();
                conn.close();

                if (rows > 0) {
                    showInfo("Supplier added successfully.");
                    SupplierMng.loadAllSuppliers(table);
                    stage.close();
                } else {
                    showError("Failed to add supplier.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Unexpected error: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e2 -> stage.close());

        stage.setScene(new Scene(root, 420, 220));
        stage.showAndWait();
    }

   
    public void deleteSupplier(TableView<Supplier> table) {

        Stage stage = new Stage();
        stage.setTitle("Delete Supplier");

        Label lblId = new Label("Supplier ID:");
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

              
                PreparedStatement ch = conn.prepareStatement(
                        "SELECT COUNT(*) AS cnt FROM Purchase_Order WHERE Supplier_ID = ?");
                ch.setInt(1, id);
                ResultSet rs = ch.executeQuery();
                rs.next();
                int poCnt = rs.getInt("cnt");
                rs.close();
                ch.close();

                if (poCnt > 0) {
                    conn.close();
                    showError("Cannot delete: Supplier has Purchase Orders (" + poCnt + ").");
                    return;
                }

                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM Supplier WHERE Supplier_ID = ?");
                ps.setInt(1, id);
                int rows = ps.executeUpdate();
                ps.close();
                conn.close();

                if (rows > 0) {
                    showInfo("Supplier deleted successfully.");
                    SupplierMng.loadAllSuppliers(table);
                    stage.close();
                } else {
                    showError("No supplier found with this ID.");
                }

            } catch (NumberFormatException ex) {
                showError("Supplier ID must be a number.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Unexpected error: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e2 -> stage.close());

        stage.setScene(new Scene(root, 360, 160));
        stage.showAndWait();
    }

   
    public void updateSupplier(TableView<Supplier> table) {

        Stage stage = new Stage();
        stage.setTitle("Update Supplier");

        Label lblId    = new Label("Supplier ID:");
        TextField tfId = new TextField();

        Label lblName  = new Label("New Name:");
        TextField tfName = new TextField();

        Label lblPhone = new Label("New Phone:");
        TextField tfPhone = new TextField();

        Label lblEmail = new Label("New Email:");
        TextField tfEmail = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        grid.add(lblId,    0, 0); grid.add(tfId,    1, 0);
        grid.add(lblName,  0, 1); grid.add(tfName,  1, 1);
        grid.add(lblPhone, 0, 2); grid.add(tfPhone, 1, 2);
        grid.add(lblEmail, 0, 3); grid.add(tfEmail, 1, 3);

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

                PreparedStatement psSel = conn.prepareStatement(
                        "SELECT Name, Phone, Email FROM Supplier WHERE Supplier_ID = ?");
                psSel.setInt(1, id);
                ResultSet rs = psSel.executeQuery();

                if (!rs.next()) {
                    showError("Supplier not found with this ID.");
                    rs.close();
                    psSel.close();
                    conn.close();
                    return;
                }

                String oldName  = rs.getString("Name");
                String oldPhone = rs.getString("Phone");
                String oldEmail = rs.getString("Email");

                rs.close();
                psSel.close();

                String newName  = tfName.getText();
                String newPhone = tfPhone.getText();
                String newEmail = tfEmail.getText();

               
                String finalName;
                if (newName != null && !newName.trim().isEmpty()) {
                    finalName = newName.trim();
                } else {
                    finalName = oldName;
                }

               
                String finalPhone;
                if (newPhone != null && !newPhone.trim().isEmpty()) {
                    finalPhone = newPhone.trim();
                } else {
                    finalPhone = oldPhone;
                }

               
                String finalEmail;
                if (newEmail != null && !newEmail.trim().isEmpty()) {
                    finalEmail = newEmail.trim();
                } else {
                    finalEmail = oldEmail;
                }


                PreparedStatement psUp = conn.prepareStatement(
                        "UPDATE Supplier SET Name = ?, Phone = ?, Email = ? WHERE Supplier_ID = ?");
                psUp.setString(1, finalName);
                psUp.setString(2, finalPhone);
                psUp.setString(3, finalEmail);
                psUp.setInt(4, id);

                int rows = psUp.executeUpdate();
                psUp.close();
                conn.close();

                if (rows > 0) {
                    showInfo("Supplier updated successfully.");
                    SupplierMng.loadAllSuppliers(table);
                    stage.close();
                } else {
                    showError("Update failed. No rows affected.");
                }

            } catch (NumberFormatException ex) {
                showError("Supplier ID must be a valid number.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Unexpected error: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e2 -> stage.close());

        stage.setScene(new Scene(root, 420, 230));
        stage.showAndWait();
    }

    
    public void searchSupplierByName(TableView<Supplier> table) {

        Stage stage = new Stage();
        stage.setTitle("Search Supplier by Name");

        Label lblName = new Label("Supplier Name:");
        TextField tfName = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.add(lblName, 0, 0);
        grid.add(tfName, 1, 0);

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
                String pattern = tfName.getText().trim();

                if (pattern.isEmpty()) {
                    showError("Please enter part of the supplier name.");
                    return;
                }

                table.getItems().clear();

                Connection conn = DBConnection.getConnection();

                String sql =
                    "SELECT Supplier_ID, Name, Phone, Email " +
                    "FROM Supplier " +
                    "WHERE Name LIKE ? " +
                    "ORDER BY Name";

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + pattern + "%");
                ResultSet rs = ps.executeQuery();

                boolean found = false;
                while (rs.next()) {
                    Supplier s = new Supplier(
                            rs.getInt("Supplier_ID"),
                            rs.getString("Name"),
                            rs.getString("Phone"),
                            rs.getString("Email")
                    );
                    table.getItems().add(s);
                    found = true;
                }

                rs.close();
                ps.close();
                conn.close();

                if (!found) {
                    showInfo("No suppliers found matching this name.");
                } else {
                    stage.close();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Unexpected error: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e2 -> stage.close());

        stage.setScene(new Scene(root, 380, 180));
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
