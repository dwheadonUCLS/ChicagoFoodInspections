/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datagrapher;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author dwheadon
 */
public class TableController implements Initializable {

    @FXML
    private TableView table;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TableColumn zipColumn = new TableColumn("Zip");
        TableColumn numFailsColumn = new TableColumn("# Failures");
        TableColumn numUnfailsColumn = new TableColumn("# Unfailures");
        ObservableList<InspectionSummary> data = FXCollections.observableArrayList();
        Map<Integer, Integer> failures = Settings.getFailures();
        Map<Integer, Integer> unfailures = Settings.getUnfailures();
        
        for (Integer zip : failures.keySet()) {
            int failed = failures.get(zip);
            int unfailed = unfailures.get(zip);
            int total = failed + unfailed;
            double percentFailed = failed / (double) total;

            String filter = Settings.getFilterValue();
            if (filter.equals("good")) {
                if (percentFailed <= 0.25) {
                    data.add(new InspectionSummary(zip, failures.get(zip), unfailures.get(zip)));
                }
            } else if (filter.equals("bad")) {
                if (percentFailed > 0.25 && percentFailed < 0.75) {
                    data.add(new InspectionSummary(zip, failures.get(zip), unfailures.get(zip)));
                }
            } else if (filter.equals("ugly")) {
                if (percentFailed >= 0.75) {
                    data.add(new InspectionSummary(zip, failures.get(zip), unfailures.get(zip)));
                }
            } else { // all
                data.add(new InspectionSummary(zip, failed, unfailed));
            }
        }
        zipColumn.setCellValueFactory(
            new PropertyValueFactory<InspectionSummary,Integer>("zip")
        );
        numFailsColumn.setCellValueFactory(
            new PropertyValueFactory<InspectionSummary,Integer>("failed")
        );
        numUnfailsColumn.setCellValueFactory(
            new PropertyValueFactory<InspectionSummary,Integer>("unfailed")
        );
        table.setItems(data);
        table.getColumns().addAll(zipColumn, numFailsColumn, numUnfailsColumn);
    }    
    
    @FXML
    public void handleBackToGraph() {
        try {
            DataGrapher.getAppInstance().showGraphView();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
