/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scical;

import java.util.HashMap;
/**
 *
 * @author Raymond
 */
public class SciUnit {
    public static HashMap<String, Double> prefix = new HashMap<String, Double>(){
        {
            put("Y", 1e24);
            put("Z", 1e21);
            put("E", 1e18);
            put("P", 1e15);
            put("T", 1e12);
            put("G", 1e9);
            put("M", 1e6);
            put("k", 1e3);
            put("h", 1e2);
            put("da", 1e1);
            put("d", 1e-1);
            put("c", 1e-2);
            put("m", 1e-3);
            put("u", 1e-6);
            put("n", 1e-9);
            put("p", 1e-12);
            put("f", 1e-15);
            put("a", 1e-18);
            put("z", 1e-21);
            put("y", 1e-24);
        }
    };
    
    private String name;
    private double order;
    
    public SciUnit(String name, double order) {
        this.name = name;
        this.order = order;
    }
    
    public SciUnit(SciUnit other) {
        this.name = other.name;
        this.order = other.order;
    }
    
    public void setName(String name) {this.name = name;}
    public String getName() { return name;}
    
    public void setOrder(double order) { this.order = order;}
    public double getOrder() { return order;}
    
    @Override
    public String toString() {
        if (Double.compare(order, 0.) == 0) return "";
        else if (Double.compare(order, 1.) == 0) return " " + name;
        else return " " + name + "^" + Double.toString(order);
    }
    
    public boolean equals(SciUnit other) {
        if (name != other.name) throw new IllegalArgumentException("cannot compare two different units.");
        return Double.compare(order, other.order) == 0;
    }
    
    public boolean equalsZero() {
        return Double.compare(order, 0.) == 0;
    }
}
