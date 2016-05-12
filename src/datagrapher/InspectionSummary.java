/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datagrapher;

/**
 *
 * @author dwheadon
 */
public class InspectionSummary {
    private int zip;
    private int failed;
    private int unfailed;
    
    public InspectionSummary() {
    }
    
    public InspectionSummary(int zip, int failed, int unfailed) {
        this.zip = zip;
        this.failed = failed;
        this.unfailed = unfailed;
    }
    
    public int getZip() {
        return zip;
    }
    
    public int getFailed() {
        return failed;
    }
    
    public int getUnfailed() {
        return unfailed;
    }
        
    @Override
    public String toString() {
        return "" + zip + ": " + failed + " failed and " + unfailed + " not failed";
    }
}
