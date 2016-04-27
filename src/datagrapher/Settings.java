/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datagrapher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 *
 * @author dwheadon
 */
public class Settings implements java.io.Serializable {
    private String filterValue = "all";
    public transient static Settings instance;
    private Map<Integer, Integer> failedInspections;
    
    private Settings() {}
    
    private static void init() {
        if (instance == null) {
            try
            {
               FileInputStream fileIn = new FileInputStream("settings.ser");
               ObjectInputStream in = new ObjectInputStream(fileIn);
               instance = (Settings) in.readObject();
               in.close();
               fileIn.close();
            }catch(IOException i)
            {
               instance = new Settings();
               return;
            }catch(ClassNotFoundException c)
            {
               System.out.println("Employee class not found");
               c.printStackTrace();
               return;
            }            
        }
    }
    
    public static Map<Integer, Integer> getFailures() {
        init();
        return instance.failedInspections;
    }
    
    public static void setFailures(Map<Integer, Integer> data) {
        init();
        instance.failedInspections = data;
    }
    
    public static String getFilterValue() {
        init();
        return instance.filterValue;
    }
    
    public static void setFilterValue(String val) {
        init();
        instance.filterValue = val;
    }
    
    public static void save() {
        init();
        try {
           FileOutputStream fileOut =
           new FileOutputStream("settings.ser");
           ObjectOutputStream out = new ObjectOutputStream(fileOut);
           out.writeObject(instance);
           out.close();
           fileOut.close();
           System.out.printf("Serialized data is saved in settings.ser");
        }catch(IOException i) {
            i.printStackTrace();
        }    
    }
}
