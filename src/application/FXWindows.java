package application;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class FXWindows {

    public static void design(Stage stage) {

        TabPane tabPane = new TabPane();

        Tab tab1 = new Tab("Medicines");
        tab1.setClosable(false);
        tab1.setContent(MedicineMng.getView());   

        Tab tab2 = new Tab("Expiring Batches");
        tab2.setClosable(false);
        tab2.setContent(BatchMng.getView());      
        tabPane.getTabs().addAll(tab1, tab2);

        Scene scene = new Scene(tabPane, 1200, 650);
        stage.setScene(scene);
        stage.setTitle("Pharmacy System");
        stage.show();
    }
}
