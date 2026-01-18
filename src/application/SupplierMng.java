package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

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

		TableColumn<Supplier, Integer> qtyCol = new TableColumn<>("Total Qty");
		qtyCol.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));

		table.getColumns().addAll(idCol, nameCol, phoneCol, emailCol, qtyCol);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		Label title = new Label("Suppliers - Management");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 26));

		Button btnAdd = new Button("Add");
		Button btnDelete = new Button("Delete");
		Button btnSearch = new Button("Search");
		Button btnUpdate = new Button("Update");
		Button btnAll = new Button("All Suppliers");

		Button btnThisMonth = new Button("Suppliers with Purchase Orders This Month");

		Button btnTop5 = new Button("Top 5 Suppliers (2025)");

		String style = "-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;";
		btnAdd.setStyle(style);
		btnDelete.setStyle(style);
		btnSearch.setStyle(style);
		btnUpdate.setStyle(style);
		btnAll.setStyle(style);
		btnThisMonth.setStyle(style);

		btnTop5.setStyle(style);

		FXForSupplier fx = new FXForSupplier();

		btnAdd.setOnAction(e -> fx.addSupplier(table));
		btnDelete.setOnAction(e -> fx.deleteSupplier(table));

		btnSearch.setOnAction(e -> fx.searchSupplierByName(table));

		btnUpdate.setOnAction(e -> fx.updateSupplier(table));
		btnAll.setOnAction(e -> loadSuppliers(table));

		btnThisMonth.setOnAction(e -> loadSuppliersThisMonth(table));

		btnTop5.setOnAction(e -> loadTopSuppliersCurrentYear(table));

		HBox buttons = new HBox(10, btnAdd, btnDelete, btnSearch, btnUpdate, btnAll, btnThisMonth, btnTop5);

		buttons.setAlignment(Pos.CENTER);

		VBox root = new VBox(15, title, buttons, table);
		root.setPadding(new Insets(15));
		root.setAlignment(Pos.TOP_CENTER);
		root.setStyle("-fx-background-color:#d8f3dc;");

		loadSuppliers(table);
		return root;
	}

	public static void loadSuppliers(TableView<Supplier> table) {// 4
		table.getItems().clear();

		String sql = "SELECT Supplier_ID, Name, Phone, Email FROM Supplier";

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
				Supplier s = new Supplier(rs.getInt("Supplier_ID"), rs.getString("Name"), rs.getString("Phone"),
						rs.getString("Email"));
				table.getItems().add(s);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load suppliers.").show();
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

	public static void loadAllSuppliers(TableView<Supplier> table) {
		loadSuppliers(table);
	}

	public static void loadSuppliersThisMonth(TableView<Supplier> table) {// 5
		table.getItems().clear();

		LocalDate now = LocalDate.now();
		LocalDate startDate = now.withDayOfMonth(1);
		LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

		String startDT = startDate.toString() + " 00:00:00";
		String endDT = endDate.toString() + " 23:59:59";

		String sql = "SELECT DISTINCT s.Supplier_ID, s.Name, s.Phone, s.Email " + "FROM Supplier s "
				+ "JOIN Purchase_Order po ON po.Supplier_ID = s.Supplier_ID " + "WHERE po.Date BETWEEN ? AND ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				return;
			}

			ps = conn.prepareStatement(sql);
			ps.setString(1, startDT);
			ps.setString(2, endDT);

			rs = ps.executeQuery();

			while (rs.next()) {
				Supplier s = new Supplier(rs.getInt("Supplier_ID"), rs.getString("Name"), rs.getString("Phone"),
						rs.getString("Email"));
				table.getItems().add(s);
			}

			if (table.getItems().isEmpty()) {
				new Alert(Alert.AlertType.INFORMATION,
						"There are no suppliers with purchase orders in the current month.").show();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load suppliers with purchase orders for the current month.")
					.show();
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

	public static void loadTopSuppliersCurrentYear(TableView<Supplier> table) {// 20
		table.getItems().clear();

		LocalDate now = LocalDate.now();
		LocalDate startDate = LocalDate.of(now.getYear(), 1, 1);
		LocalDate endDate = LocalDate.of(now.getYear(), 12, 31);

		String startDT = startDate.toString() + " 00:00:00";
		String endDT = endDate.toString() + " 23:59:59";

		String sql = "SELECT s.Supplier_ID, s.Name, s.Phone, s.Email, " + "       SUM(poi.Quantity) AS Total_Quantity "
				+ "FROM Supplier s " + "JOIN Purchase_Order po ON po.Supplier_ID = s.Supplier_ID "
				+ "JOIN Purchase_Order_Item poi ON poi.PO_ID = po.PO_ID " + "WHERE po.Date BETWEEN ? AND ? "
				+ "GROUP BY s.Supplier_ID, s.Name, s.Phone, s.Email " + "ORDER BY Total_Quantity DESC";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = DBConnection.getConnection();
			if (conn == null) {
				return;
			}

			ps = conn.prepareStatement(sql);
			ps.setString(1, startDT);
			ps.setString(2, endDT);

			rs = ps.executeQuery();

			int count = 0;
			while (rs.next() && count < 5) {
				Supplier s = new Supplier(rs.getInt("Supplier_ID"), rs.getString("Name"), rs.getString("Phone"),
						rs.getString("Email"));

				Object obj = rs.getObject("Total_Quantity");
				int tq = 0;
				if (obj != null) {
					if (obj instanceof Number) {
						tq = ((Number) obj).intValue();
					} else {
						tq = Integer.parseInt(obj.toString());
					}
				}
				s.setTotalQuantity(tq);

				table.getItems().add(s);
				count++;
			}

			if (table.getItems().isEmpty()) {
				new Alert(Alert.AlertType.INFORMATION, "No supplier quantities found for the current year.").show();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			new Alert(Alert.AlertType.ERROR, "Failed to load top suppliers for the current year.").show();
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
