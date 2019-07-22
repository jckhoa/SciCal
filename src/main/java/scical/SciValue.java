/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scical;

/**
 *
 * @author Raymond
 */


public class SciValue {
    private double value = 0.;
    private SciUnit unit[] = {new SciUnit("s",0), new SciUnit("m", 0)
            , new SciUnit("g", 0), new SciUnit("A", 0), new SciUnit("K", 0)
            , new SciUnit("mol", 0), new SciUnit("cd", 0)};

    
    // Constructors
    public SciValue() {}    
    public SciValue(double value) { setValue(value); }    
    public SciValue(double value, double seconds) {
        setValue(value, seconds);
    }   
    public SciValue(double value, double seconds, double meters) {
        setValue(value, seconds, meters);
    }   
    public SciValue(double value, double seconds, double meters, double grams) {
        setValue(value, seconds, meters, grams);
    }   
    public SciValue(double value, double seconds, double meters, double grams, double amperes) {
        setValue(value, seconds, meters, grams, amperes);
    }    
    public SciValue(double value, double seconds, double meters, double grams, double amperes, double kelvins) {
        setValue(value, seconds, meters, grams, amperes, kelvins);
    }   
    public SciValue(double value, double seconds, double meters, double grams, double amperes, double kelvins, double moles) {
        setValue(value, seconds, meters, grams, amperes, kelvins, moles);
    }   
    public SciValue(double value, double seconds, double meters, double grams, double amperes, double kelvins, double moles, double candelas) {
        setValue(value, seconds, meters, grams, amperes, kelvins, moles, candelas);
    }  
    public SciValue(SciValue other) {
        this.value = other.value;
        setUnit(other);
    }  
    public SciValue(double value, SciUnit[] unit) {
        this.value = value;
        this.unit = new SciUnit[7];
        for (int i = 0; i < 7; ++i) {
            this.unit[i] = unit[i];
        }
    }
  
    // set, get
    public void setValue(double value) { this.value = value; }
    public double getValue() {return this.value;}
    public void setValue(double value, double seconds) {
        this.value = value;
        this.unit[0].setOrder(seconds);
    }    
    public void setValue(double value, double seconds, double meters) {
        this.value = value;
        this.unit[0].setOrder(seconds);
        this.unit[1].setOrder(meters);
    }    
    public void setValue(double value, double seconds, double meters, double grams) {
        this.value = value;
        this.unit[0].setOrder(seconds);
        this.unit[1].setOrder(meters);
        this.unit[2].setOrder(grams);
    }    
    public void setValue(double value, double seconds, double meters, double grams, double amperes) {
        this.value = value;
        this.unit[0].setOrder(seconds);
        this.unit[1].setOrder(meters);
        this.unit[2].setOrder(grams);
        this.unit[3].setOrder(amperes);
    }    
    public void setValue(double value, double seconds, double meters, double grams, double amperes, double kelvins) {
        this.value = value;
        this.unit[0].setOrder(seconds);
        this.unit[1].setOrder(meters);
        this.unit[2].setOrder(grams);
        this.unit[3].setOrder(amperes);
        this.unit[4].setOrder(kelvins);
    }
    public void setValue(double value, double seconds, double meters, double grams, double amperes, double kelvins, double moles) {
        this.value = value;
        this.unit[0].setOrder(seconds);
        this.unit[1].setOrder(meters);
        this.unit[2].setOrder(grams);
        this.unit[3].setOrder(amperes);
        this.unit[4].setOrder(kelvins);
        this.unit[5].setOrder(moles);
    }
    public void setValue(double value, double seconds, double meters, double grams, double amperes, double kelvins, double moles, double candelas) {
        this.value = value;
        this.unit[0].setOrder(seconds);
        this.unit[1].setOrder(meters);
        this.unit[2].setOrder(grams);
        this.unit[3].setOrder(amperes);
        this.unit[4].setOrder(kelvins);
        this.unit[5].setOrder(moles);
        this.unit[6].setOrder(candelas);
    }
    private void setUnit(SciValue other) {
        unit = new SciUnit[7];
        for (int i = 0; i < 7; ++i) {
            unit[i] = new SciUnit(other.unit[i]);
        }
    }
    SciUnit[] getUnit() { return unit;}
    
    //----------------------output---------------------------------
    @Override
    public String toString() {
        String result = String.valueOf(value);
        for (int i = 0; i < 7; ++i) result += unit[i];
        return result;
    }
    //----------comparison------------------------------------------
    public boolean equals(SciValue other) {
        if (null == other) return false;
        if (!(other instanceof SciValue)) return false;
        return unitEquals(other) && compareValue(other) == 0;
    }
    public int compareTo(SciValue other) {
        checkUnitCompatibility(other);
        return compareValue(other);
    }

    //-------------math operators---------------------------------
    SciValue plus(SciValue other) {
        checkUnitCompatibility(other);
        return new SciValue(value + other.value, unit);
    }
    
    SciValue minus(SciValue other) {
        checkUnitCompatibility(other);
        return new SciValue(value - other.value, unit);
    }
    
    SciValue mod(SciValue other) {
        checkUnitCompatibility(other);
        return new SciValue(value - Math.floor(value / other.value) * other.value, unit);
    }
    
    SciValue multiply(SciValue other) {
        if (other == null) return null;
        return new SciValue(value * other.value, unitPlus(other));
    }

    SciValue div(SciValue other) {
        if (other == null) return null;
        return new SciValue(value / other.value, unitMinus(other));
    }
    
    SciValue power(SciValue other) {
        if (!other.isScalar()) {
            throw new IllegalArgumentException("The other number is not a scalar.");
        }
        return power(other.value);
    }
    
    SciValue power(double other) {
        SciValue result = new SciValue(this);
        result.value = Math.pow(result.value, other);
        for (int i = 0; i < 7; ++i) {
            result.unit[i].setOrder(result.unit[i].getOrder() * other);
        }
        return result;
    }

    SciValue ln() {
        assertScalar("log");
        return new SciValue(Math.log(value));
    }
    //-----boolean returned functions----------------
    public boolean isScalar() {
        for (int i = 0; i < 7; ++i) {
            if (!unit[i].equalsZero()) return false;
        }
        return true;
    }
    
    private int compareValue(SciValue other) { return Double.compare(value, other.value); }
    
    private boolean unitEquals(SciValue other) {
        for (int i = 0; i < 7; ++i)
            if (!unit[i].equals(other.unit[i])) return false;
        return true;
    }
    
    //private functions
    private void resetUnit() {
        for (int i = 0; i < 7; ++i) this.unit[i].setOrder(0.);
    }
    
    private void assertScalar(String operatorName) {
        if (!isScalar()) {
            throw new IllegalArgumentException("cannot use '" + operatorName + "' on a non-scalar value");
        }
    }
        
    private void checkUnitCompatibility(SciValue other) {
        if (null == other || !unitEquals(other)) {
            throw new IllegalArgumentException("cannot compare two scientific values with different units.");
        }
    }
    
    private SciUnit[] unitPlus(SciValue other) {
        SciUnit[] unitResult = new SciUnit[7];
        for (int i = 0; i < 7; ++i) {
            unitResult[i] = new SciUnit(unit[i].getName(), unit[i].getOrder() + other.unit[i].getOrder());
        }
        return unitResult;
    }
    
    private SciUnit[] unitMinus(SciValue other) {
        SciUnit[] unitResult = new SciUnit[7];
        for (int i = 0; i < 7; ++i) {
            unitResult[i] = new SciUnit(unit[i].getName(), unit[i].getOrder() - other.unit[i].getOrder());
        }
        return unitResult;
    }
}
