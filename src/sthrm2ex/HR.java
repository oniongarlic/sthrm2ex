/*
 * Utilities to calculate HRr, HRmax 
 */
package sthrm2ex;

/**
 *
 * @author milang
 */
public class HR {
    
    final int GENDER_MAN=1;
    final int GENDER_WOMAN=2;    
    
    private int age;
    private double HRrest;
    private double HRmax;
    
    public HR(int age, double HRrest) {
        this.age=age;
        this.HRrest=HRrest;
        this.HRmax=getHRmax();        
    }
    
    public int getHRmaxInt() {
        return (int)Math.floor(HRmax);
    }
    
    public double getHRmax() {
        return 163.0 + (1.16*age)-(0.018*age*age);        
    }
    
    public double getHRr() {
        return HRmax-HRrest;
    }
    
}
