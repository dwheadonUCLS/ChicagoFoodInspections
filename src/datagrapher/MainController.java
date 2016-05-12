/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datagrapher;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;

/**
 *
 * @author dwheadon
 */
public class MainController implements Initializable, ChangeListener<String> {
        
    @FXML
    private StackedBarChart<String,Number> chart;
    
    @FXML
    private LineChart<String,Number> percentFailedChart;

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
        updateGraphs(filterChoice.getValue().toString());
    }
    
    public void changed(ObservableValue ov, String value, String newValue) {
        Settings.setFilterValue(newValue);
        updateGraphs(newValue);
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
        Map<Integer, Integer> unfailedInspections = new TreeMap<Integer, Integer>();
        Gson gson = new Gson();
        Inspection[] inspections = gson.fromJson(str, Inspection[].class);

        // Add all the zips to both maps
        for (Inspection inspection : inspections) {
            Integer zip = inspection.getZip();
            if (! failedInspections.containsKey(zip)) {
                failedInspections.put(zip, 0);
                unfailedInspections.put(zip, 0);
            }
        }
        for (Inspection inspection : inspections) {
            Integer zip = inspection.getZip();
            if (inspection.failed()) {
                Integer currFails = failedInspections.get(zip);
                failedInspections.put(zip, currFails + 1);
            } else {
                Integer curTotal = unfailedInspections.get(zip);
                unfailedInspections.put(zip, curTotal + 1);
            }
        }
        Settings.setFailures(failedInspections);
        Settings.setUnfailures(unfailedInspections);
        String newFilter = filterChoice.getValue().toString();
        Settings.setFilterValue(newFilter);
        updateGraphs(newFilter);
    }       
    
    public void updateGraphs(String newFilter) {
        chart.getData().clear();
        percentFailedChart.getData().clear();

        // Get the updated data
        Map<Integer, Integer> failedInspections = Settings.getFailures();
        Map<Integer, Integer> unfailedInspections = Settings.getUnfailures();
        
        // Recreate the percentage data which is derived from the failed and unfailed data
        Set<Integer> failedZips = failedInspections.keySet();
        Set<Integer> unfailedZips = unfailedInspections.keySet();
        Set<Integer> allZips = new TreeSet<Integer>();
        allZips.addAll(failedZips);
        allZips.addAll(unfailedZips);
        // Map<Integer, Double> percentFailures = new TreeMap<Integer, Double>();

        XYChart.Series<String, Number> failedSeries = new XYChart.Series();
        failedSeries.setName("# Failed Inspections");
        XYChart.Series<String, Number> unfailedSeries = new XYChart.Series<>();
        unfailedSeries.setName("# Unfailed Inspections");
        XYChart.Series<String, Number> percentFailedSeries = new XYChart.Series();
        // No legend for percentFailedSeries
        
        for (Integer zip : allZips) {
            int failed = 0;
            if (failedInspections.containsKey(zip)) {
                failed = failedInspections.get(zip);
            }
            int unfailed = 0;
            if (unfailedInspections.containsKey(zip)) {
                unfailed = unfailedInspections.get(zip);
            }
            int total = failed + unfailed;
            assert total != 0;
            double percentFailed = failed / (double) total;
            // percentFailures.put(zip, percent);
            String filter = filterChoice.getValue().toString();
            if (filter.equals("good")) {
                if (percentFailed <= 0.25) {
                    failedSeries.getData().add(new XYChart.Data(zip.toString(), failed));
                    unfailedSeries.getData().add(new XYChart.Data(zip.toString(), unfailed));
                    percentFailedSeries.getData().add(new XYChart.Data(zip.toString(), percentFailed));
                }
            } else if (filter.equals("bad")) {
                if (percentFailed > 0.25 && percentFailed < 0.75) {
                    failedSeries.getData().add(new XYChart.Data(zip.toString(), failed));
                    unfailedSeries.getData().add(new XYChart.Data(zip.toString(), unfailed));
                    percentFailedSeries.getData().add(new XYChart.Data(zip.toString(), percentFailed));
                }
            } else if (filter.equals("ugly")) {
                if (percentFailed >= 0.75) {
                    failedSeries.getData().add(new XYChart.Data(zip.toString(), failed));
                    unfailedSeries.getData().add(new XYChart.Data(zip.toString(), unfailed));
                    percentFailedSeries.getData().add(new XYChart.Data(zip.toString(), percentFailed));
                }
            } else { // all
                failedSeries.getData().add(new XYChart.Data(zip.toString(), failed));
                unfailedSeries.getData().add(new XYChart.Data(zip.toString(), unfailed));
                percentFailedSeries.getData().add(new XYChart.Data(zip.toString(), percentFailed));
            }
        }
        
        chart.getData().add(failedSeries);
        chart.getData().add(unfailedSeries);
        percentFailedChart.getData().add(percentFailedSeries);
        percentFailedChart.getXAxis().setTickLabelsVisible(false);
        percentFailedChart.getYAxis().setSide(Side.RIGHT);
        //percentFailedChart.getYAxis().setTickLabelsVisible(false);
    }
    
    @FXML
    public void handleShowData(ActionEvent event) {
        try {
            DataGrapher.getAppInstance().showTableView();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
