/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datagrapher;

import com.google.gson.Gson;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TreeMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

/**
 *
 * @author dwheadon
 */
public class MainController implements Initializable {
        
    @FXML
    private BarChart<String,Number> chart;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
            System.exit(-1);
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

        XYChart.Series<String, Number> failedSeries = new XYChart.Series();
        failedSeries.setName("# Failed Inspections");
        Object[] keys = failedInspections.keySet().toArray();
        Arrays.sort(keys);
        for (Object zip : keys) {
            failedSeries.getData().add(new XYChart.Data(zip.toString(), failedInspections.get(zip)));
        }
        chart.getData().add(failedSeries);
    }        
}
