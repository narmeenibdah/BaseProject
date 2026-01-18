package application;

import java.time.LocalDate;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class FXWindows {

	private static Parent homeView;

	public static void design(Stage stage) {

		BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color:#d8f3dc;");

		Label appTitle = new Label("Birzeit Pharmacy | صيدلية بيرزيت");
		appTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
		appTitle.setStyle("-fx-text-fill:#1b4332;");

		Label today = new Label("Today: " + LocalDate.now());
		today.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
		today.setStyle("-fx-text-fill:#2d6a4f;");

		HBox header = new HBox(20, appTitle, today);
		header.setAlignment(Pos.CENTER_LEFT);
		header.setPadding(new Insets(12));
		header.setStyle("-fx-background-color:#f1faee;" + "-fx-border-color:#b7e4c7;" + "-fx-border-width:0 0 2 0;");

		ComboBox<String> cb = new ComboBox<>();
		cb.setPrefWidth(420);
		cb.setPromptText("Choose a page...");

		cb.setStyle("-fx-background-color:#b7e4c7;" + "-fx-font-weight:bold;" + "-fx-background-radius:10;"
				+ "-fx-border-radius:10;" + "-fx-border-color:#74c69d;");

		cb.getItems().addAll("Medicines", "Batches & Expiry", "Suppliers", "Employees", "Purchase Orders", "PO Details",
				"Sales", "Sale Details", "Customers", "Employee Sales Report", "Top Medicines", "Monthly Summary",
				"Stock by Warehouse", "Stock Movements");

		Button btnOpen = new Button("Open");
		btnOpen.setPrefWidth(120);
		btnOpen.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");

		Button btnHome = new Button("Home");
		btnHome.setPrefWidth(120);
		btnHome.setStyle("-fx-background-color:white; -fx-border-color:#74c69d; -fx-font-weight:bold;");

		HBox controls = new HBox(10, cb, btnOpen, btnHome);
		controls.setAlignment(Pos.CENTER_LEFT);
		controls.setPadding(new Insets(12));
		controls.setStyle("-fx-background-color:#f1faee;");

		VBox topArea = new VBox(header, controls, new Separator());
		root.setTop(topArea);

		homeView = createHomeView(root, cb);
		root.setCenter(homeView);

		btnOpen.setOnAction(e -> {
			String choice = cb.getValue();
			if (choice == null) {
				return;
			}

			Parent view = getViewByChoice(choice);
			if (view != null) {
				root.setCenter(view);
			}
		});

		btnHome.setOnAction(e -> {
			cb.setValue(null);
			root.setCenter(homeView);
		});

		Scene scene = new Scene(root, 1200, 650);
		stage.setScene(scene);
		stage.setTitle("Birzeit Pharmacy");
		stage.show();
	}

	private static Parent createHomeView(BorderPane root, ComboBox<String> cb) {

		VBox box = new VBox(15);
		box.setAlignment(Pos.CENTER);
		box.setPadding(new Insets(20));

		Label welcome = new Label("Welcome to Birzeit Pharmacy");
		welcome.setFont(Font.font("Arial", FontWeight.BOLD, 28));
		welcome.setStyle("-fx-text-fill:#1b4332;");

		Label sub = new Label("Choose a page from the menu above to start.");
		sub.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
		sub.setStyle("-fx-text-fill:#2d6a4f;");

		ImageView imgView = new ImageView();
		imgView.setPreserveRatio(true);
		imgView.setFitWidth(1000);
		imgView.setFitHeight(500);

		Label imgError = new Label("Image not found: /application/pharmacy.jpg");
		imgError.setStyle("-fx-text-fill:#b00020; -fx-font-weight:bold;");
		imgError.setVisible(false);

		try {
			Image img = new Image(FXWindows.class.getResourceAsStream("/application/pharmacy.jpg"));
			imgView.setImage(img);
		} catch (Exception ex) {
			imgError.setVisible(true);
			System.out.println("Could not load image: " + ex.getMessage());
		}

		StackPane imgCard = new StackPane();
		imgCard.setPadding(new Insets(10));
		imgCard.setStyle("-fx-background-color:white;" + "-fx-background-radius:16;" + "-fx-border-color:#b7e4c7;"
				+ "-fx-border-radius:16;");

		if (imgView.getImage() != null) {
			imgCard.getChildren().add(imgView);
		} else {
			imgCard.getChildren().add(imgError);
		}

		Button btnEnter = new Button("Enter System");
		btnEnter.setStyle("-fx-background-color:#52b788; -fx-text-fill:white; -fx-font-weight:bold;");
		btnEnter.setPrefWidth(180);

		btnEnter.setOnAction(e -> {
			cb.setValue("Medicines");
			root.setCenter(MedicineMng.getView());
		});

		box.getChildren().addAll(welcome, sub, imgCard, btnEnter);
		return box;
	}

	private static Parent getViewByChoice(String choice) {

		Parent view = null;

		if (choice.equals("Medicines")) {
			view = MedicineMng.getView();
		} else if (choice.equals("Batches & Expiry")) {
			view = BatchMng.getView();
		} else if (choice.equals("Suppliers")) {
			view = SupplierMng.getView();
		} else if (choice.equals("Employees")) {
			view = EmployeeMng.getView();
		} else if (choice.equals("Purchase Orders")) {
			view = PurchaseOrderMng.getView();
		} else if (choice.equals("PO Details")) {
			view = PurchaseOrderItemsMng.getView();
		} else if (choice.equals("Sales")) {
			view = SaleMng.getView();
		} else if (choice.equals("Sale Details")) {
			view = SaleItemsMng.getView();
		} else if (choice.equals("Customers")) {
			view = CustomerMng.getView();
		} else if (choice.equals("Employee Sales Report")) {
			view = EmployeeSalesPeriodMng.getView();
		} else if (choice.equals("Top Medicines")) {
			view = TopSellingMedicinesMng.getView();
		} else if (choice.equals("Monthly Summary")) {
			view = MonthlyTotalsMng.getView();
		} else if (choice.equals("Stock by Warehouse")) {
			view = WarehouseStockMng.getView();
		} else if (choice.equals("Stock Movements")) {
			view = StockMovementMng.getView();
		}

		return view;
	}
}
