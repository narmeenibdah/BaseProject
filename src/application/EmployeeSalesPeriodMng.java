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

public class EmployeeSalesPeriodMng {

	public static Parent getView() {

		Label title = new Label("Employee Sales");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

		TextField tfEmpId = new TextField();
		tfEmpId.setPromptText("Employee ID");
		tfEmpId.setPrefWidth(160);

		DatePicker dpFrom = new DatePicker();
		dpFrom.setPromptText("From Date");

		DatePicker dpTo = new DatePicker();
		dpTo.setPromptText("To Date");

		Button btnSearch = new Button("Search");
		btnSearch.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");

		HBox filters = new HBox(10, tfEmpId, dpFrom, dpTo, btnSearch);
		filters.setAlignment(Pos.CENTER);
		filters.setPadding(new Insets(10));

		TableView<Sale> table = new TableView<>();

		TableColumn<Sale, Integer> saleIdCol = new TableColumn<>("Sale ID");
		saleIdCol.setCellValueFactory(new PropertyValueFactory<>("saleId"));

		TableColumn<Sale, String> dateCol = new TableColumn<>("Sale Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));

		TableColumn<Sale, Double> totalCol = new TableColumn<>("Total Amount");
		totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

		TableColumn<Sale, Integer> branchCol = new TableColumn<>("Branch ID");
		branchCol.setCellValueFactory(new PropertyValueFactory<>("branchId"));

		TableColumn<Sale, Integer> empCol = new TableColumn<>("Employee ID");
		empCol.setCellValueFactory(new PropertyValueFactory<>("employeeId"));

		TableColumn<Sale, Integer> custCol = new TableColumn<>("Customer ID");
		custCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

		table.getColumns().addAll(saleIdCol, dateCol, totalCol, branchCol, empCol, custCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		btnSearch.setOnAction(e -> {

			String empIdStr = tfEmpId.getText().trim();
			if (empIdStr.isEmpty()) {
				new Alert(Alert.AlertType.WARNING, "Please enter Employee ID.").show();
				return;
			}

			if (dpFrom.getValue() == null || dpTo.getValue() == null) {
				new Alert(Alert.AlertType.WARNING, "Please select From and To dates.").show();
				return;
			}

			int empId;
			try {
				empId = Integer.parseInt(empIdStr);
			} catch (Exception ex) {
				new Alert(Alert.AlertType.WARNING, "Employee ID must be a number.").show();
				return;
			}

			String from = dpFrom.getValue().toString();
			String to = dpTo.getValue().toString();

			loadSales(table, empId, from, to);
		});

		VBox root = new VBox(15);
		root.setAlignment(Pos.TOP_CENTER);
		root.setPadding(new Insets(30, 15, 15, 15));
		root.getChildren().addAll(title, filters, table);
		root.setStyle("-fx-background-color:#d8f3dc;");

		return root;
	}

	private static void loadSales(TableView<Sale> table, int empId, String from, String to) {// 18

		table.getItems().clear();

		String fromDT = from + " 00:00:00";
		String toDT = to + " 23:59:59";

		String sql = "SELECT Sale_ID, Sale_Date, Total_Amount, Branch_ID, Employee_ID, Customer_ID " + "FROM Sale "
				+ "WHERE Employee_ID = ? " + "AND Sale_Date BETWEEN ? AND ? " + "ORDER BY Sale_Date";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				return;
			}

			ps = conn.prepareStatement(sql);
			ps.setInt(1, empId);
			ps.setString(2, fromDT);
			ps.setString(3, toDT);

			rs = ps.executeQuery();

			while (rs.next()) {
				Sale s = new Sale(rs.getInt("Sale_ID"), rs.getString("Sale_Date"), rs.getDouble("Total_Amount"),
						rs.getInt("Branch_ID"), rs.getInt("Employee_ID"), rs.getInt("Customer_ID"));
				table.getItems().add(s);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load employee sales.").show();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
	}

}
