package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CustomerMng {

	public static Parent getView() {

		Label title = new Label("Regular Customers Purchases");

		title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

		TableView<Customer> table = new TableView<>();

		TableColumn<Customer, Integer> idCol = new TableColumn<>("Customer ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

		TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Customer, Integer> countCol = new TableColumn<>("Number Of Purchases");
		countCol.setCellValueFactory(new PropertyValueFactory<>("numberOfPurchases"));

		table.getColumns().addAll(idCol, nameCol, countCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		loadData(table);

		VBox root = new VBox(15);
		root.setAlignment(Pos.TOP_CENTER);
		root.getChildren().addAll(title, table);
		root.setPadding(new Insets(30, 15, 15, 15));

		root.setStyle("-fx-background-color:#d8f3dc;");

		return root;
	}

	private static void loadData(TableView<Customer> table) { // Query 16

		table.getItems().clear();

		String sql = "SELECT c.Customer_ID, c.Name, COUNT(s.Sale_ID) AS Number_Of_Purchases " + "FROM Customer c "
				+ "JOIN Sale s ON s.Customer_ID = c.Customer_ID " + "GROUP BY c.Customer_ID, c.Name "
				+ "HAVING COUNT(s.Sale_ID) > 1";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				return;
			}

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				Customer c = new Customer(rs.getInt("Customer_ID"), rs.getString("Name"),
						rs.getInt("Number_Of_Purchases"));
				table.getItems().add(c);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load regular customers.").show();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (Exception ex) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}
	}

}
