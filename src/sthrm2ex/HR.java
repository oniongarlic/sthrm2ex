/*
 * Utilities to calculate HRr, HRmax 
 */
package sthrm2ex;

/**
 *
 * @author milang
 */
public class HR implements HeartRateInterface {       
    private int age;
    private double HRrest;    
    
    public HR(int age, double HRrest) {
        this.age=age;
        this.HRrest=HRrest;                
    }
    
    public int getHRmaxInt() {
        return (int)Math.floor(getHRmax());
    }
    
    public double getHRmax() {
        return 163.0 + (1.16*age)-(0.018*age*age);        
    }
    
    final public double getHRr() {
        return getHRmax()-HRrest;
    }
    
}
