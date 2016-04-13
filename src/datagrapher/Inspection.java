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
public class Inspection {
    private int zip;
    private String results;
    
    public Inspection() {
    }
    
    public int getZip() {
        return zip;
    }
    
    public boolean failed() {
        if (results.equals("Fail")) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "" + zip + " " + results;
    }
}
