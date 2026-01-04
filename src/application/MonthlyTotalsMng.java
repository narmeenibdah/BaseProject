package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MonthlyTotalsMng {

	public static Parent getView() {

		Label title = new Label("Monthly Summary");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
		title.setStyle("-fx-text-fill:#1b4332;");

		LocalDate now = LocalDate.now();
		String monthName = now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

		Label subTitle = new Label("Total Sales & Purchases — " + monthName + " " + now.getYear());
		subTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		subTitle.setStyle("-fx-text-fill:#2d6a4f;");

		VBox header = new VBox(4, title, subTitle);
		header.setPadding(new Insets(20));
		header.setAlignment(Pos.CENTER_LEFT);
		header.setStyle("-fx-background-color:#f1faee; -fx-background-radius:14;");

		Label salesTitle = new Label("Total Sales (Current Month)");
		salesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		salesTitle.setStyle("-fx-text-fill:#2d6a4f;");

		Label salesValue = new Label("—");
		salesValue.setFont(Font.font("Arial", FontWeight.BOLD, 26));
		salesValue.setStyle("-fx-text-fill:#081c15;");

		VBox salesCard = new VBox(8, salesTitle, salesValue);
		salesCard.setAlignment(Pos.CENTER_LEFT);
		salesCard.setPadding(new Insets(16));
		salesCard.setPrefWidth(420);
		salesCard.setStyle("-fx-background-color:white;" + "-fx-background-radius:14;" + "-fx-border-color:#b7e4c7;"
				+ "-fx-border-radius:14;");

		Label purchTitle = new Label("Total Purchases (Current Month)");
		purchTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		purchTitle.setStyle("-fx-text-fill:#2d6a4f;");

		Label purchValue = new Label("—");
		purchValue.setFont(Font.font("Arial", FontWeight.BOLD, 26));
		purchValue.setStyle("-fx-text-fill:#081c15;");

		VBox purchCard = new VBox(8, purchTitle, purchValue);
		purchCard.setAlignment(Pos.CENTER_LEFT);
		purchCard.setPadding(new Insets(16));
		purchCard.setPrefWidth(420);
		purchCard.setStyle("-fx-background-color:white;" + "-fx-background-radius:14;" + "-fx-border-color:#b7e4c7;"
				+ "-fx-border-radius:14;");

		HBox cardsRow = new HBox(18, salesCard, purchCard);
		cardsRow.setAlignment(Pos.CENTER);
		cardsRow.setPadding(new Insets(20, 0, 10, 0));

		Button btnCalc = new Button("Calculate");
		btnCalc.setPrefWidth(160);
		btnCalc.setStyle("-fx-background-color:#52b788;" + "-fx-text-fill:white;" + "-fx-font-weight:bold;"
				+ "-fx-background-radius:10;");

		Label hint = new Label("If there are no records in the current month, the result will be 0.");
		hint.setStyle("-fx-text-fill:#555; -fx-font-size:12px;");

		VBox actions = new VBox(10, btnCalc, hint);
		actions.setAlignment(Pos.CENTER);
		actions.setPadding(new Insets(10));

		VBox content = new VBox(20, header, cardsRow, actions);
		content.setAlignment(Pos.TOP_CENTER);
		content.setPadding(new Insets(20));

		BorderPane root = new BorderPane(content);
		root.setStyle("-fx-background-color:#d8f3dc;");

		btnCalc.setOnAction(e -> {

			String sql = "SELECT " + " (SELECT SUM(si.Quantity * si.Unit_Price) " + "  FROM Sale s "
					+ "  JOIN Sale_Item si ON si.Sale_ID = s.Sale_ID " + "  WHERE YEAR(s.Sale_Date)=YEAR(CURDATE()) "
					+ "    AND MONTH(s.Sale_Date)=MONTH(CURDATE())) AS totalSales, "
					+ " (SELECT SUM(poi.Quantity * poi.Unit_Price) " + "  FROM Purchase_Order po "
					+ "  JOIN Purchase_Order_Item poi ON poi.PO_ID = po.PO_ID "
					+ "  WHERE YEAR(po.Date)=YEAR(CURDATE()) "
					+ "    AND MONTH(po.Date)=MONTH(CURDATE())) AS totalPurchases";

			try (Connection conn = DBConnection.getConnection();
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {

				if (rs.next()) {

					double totalSales;
					double totalPurch;

					Object salesObj = rs.getObject("totalSales");
					if (salesObj == null) {
						totalSales = 0.0;
					} else {
						totalSales = rs.getDouble("totalSales");
					}

					Object purchObj = rs.getObject("totalPurchases");
					if (purchObj == null) {
						totalPurch = 0.0;
					} else {
						totalPurch = rs.getDouble("totalPurchases");
					}

					salesValue.setText(String.valueOf(totalSales));
					purchValue.setText(String.valueOf(totalPurch));

				}

			} catch (Exception ex) {
				ex.printStackTrace();
				showError("Failed to run Query 11: " + ex.getMessage());
			}
		});

		return root;
	}

	private static void showError(String msg) {
		Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
		a.showAndWait();
	}

	private static void showInfo(String msg) {
		Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
		a.showAndWait();
	}
}
