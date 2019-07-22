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
            put("Y", 10e24);
            put("Z", 10e21);
            put("E", 10e18);
            put("P", 10e15);
            put("T", 10e12);
            put("G", 10e9);
            put("M", 10e6);
            put("k", 10e3);
            put("h", 10e2);
            put("da", 10e1);
            put("d", 10e-1);
            put("c", 10e-2);
            put("m", 10e-3);
            put("u", 10e-6);
            put("n", 10e-9);
            put("p", 10e-12);
            put("f", 10e-15);
            put("a", 10e-18);
            put("z", 10e-21);
            put("y", 10e-24);
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
