/* foo
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datagrapher;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author dwheadon
 */
public class DataGrapher extends Application {
    
    private static DataGrapher appInstance;
    private Stage mainWindow;
    
    public static DataGrapher getAppInstance() {
        return appInstance;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.appInstance = this;
        this.mainWindow = stage;
        showGraphView();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        Settings.save();
    }
    
    public void showTableView() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Table.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
        
        this.mainWindow.setScene(scene);
        this.mainWindow.show();        
    }

    public void showGraphView() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
        
        this.mainWindow.setScene(scene);
        this.mainWindow.show();
    }
    
}
