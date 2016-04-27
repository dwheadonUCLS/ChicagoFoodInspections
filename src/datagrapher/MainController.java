/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datagrapher;

import com.google.gson.Gson;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;

/**
 *
 * @author dwheadon
 */
public class MainController implements Initializable, ChangeListener<String> {
        
    @FXML
    private BarChart chart;
    
    @FXML
    private ChoiceBox filterChoice;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        filterChoice.setItems(FXCollections.observableArrayList("all", "good", "bad", "ugly"));
        filterChoice.setValue(Settings.getFilterValue()); // default
        filterChoice.getSelectionModel().selectedItemProperty().addListener(this);
        if(Settings.getFailures() == null) {
            refreshData();
        }
        updateGraph(filterChoice.getValue().toString());
    }
    
    public void changed(ObservableValue ov, String value, String newValue) {
        Settings.setFilterValue(newValue);
        updateGraph(newValue);
    }    
    
    public void refreshData() {
        String s = "https://data.cityofchicago.org/resource/4ijn-s7e5.json?$select=zip,results";
        URL myUrl = null;
        try {
            myUrl = new URL(s);
        } catch (Exception e) {
            System.out.println("Improper URL " + s);
        }
     
        // read from the URL
        Scanner scan = null;
        try {
            scan = new Scanner(myUrl.openStream());
        } catch (Exception e) {
            System.out.println("Could not connect to " + s);
        }
        
        String str = new String();
        while (scan.hasNext()) {
            str += scan.nextLine() + "\n";
        }
        scan.close();

        Map<Integer, Integer> failedInspections = new TreeMap<Integer, Integer>();
        Gson gson = new Gson();
        Inspection[] inspections = gson.fromJson(str, Inspection[].class);

        // Add all the zips to the maps
        for (Inspection inspection : inspections) {
            Integer zip = inspection.getZip();
            if (! failedInspections.containsKey(zip)) {
                failedInspections.put(zip, 0);
            }
        }
        for (Inspection inspection : inspections) {
            Integer zip = inspection.getZip();
            if (inspection.failed()) {
                Integer currFails = failedInspections.get(zip);
                failedInspections.put(zip, currFails + 1);
            }
        }
        Settings.setFailures(failedInspections);
        updateGraph(filterChoice.getValue().toString());
    }       
    
    public void updateGraph(String newFilter) {
        chart.getData().clear();
        XYChart.Series<String, Number> failedSeries = new XYChart.Series();
        failedSeries.setName("# Failed Inspections");
        Map<Integer, Integer> failedInspections = Settings.getFailures();
        Object[] keys = failedInspections.keySet().toArray();
        Arrays.sort(keys);
        for (Object zip : keys) {
            String filter = filterChoice.getValue().toString();
            int numFailures = failedInspections.get(zip);
            if (filter.equals("good")) {
                if (numFailures < 5) {
                    failedSeries.getData().add(new XYChart.Data(zip.toString(), numFailures));
                }
            } else if (filter.equals("bad")) {
                if (numFailures >= 5 && numFailures < 10) {
                    failedSeries.getData().add(new XYChart.Data(zip.toString(), numFailures));
                }
            } else if (filter.equals("ugly")) {
                if (numFailures >= 10) {
                    failedSeries.getData().add(new XYChart.Data(zip.toString(), numFailures));
                }
            } else { // all
                failedSeries.getData().add(new XYChart.Data(zip.toString(), numFailures));
            }
        }
        chart.getData().add(failedSeries);
    }
}
