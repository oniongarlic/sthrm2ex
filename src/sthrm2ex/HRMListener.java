/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sthrm2ex;

/**
 *
 * @author milang
 */
public interface HRMListener {
    void heartRate(int hr);
    void heartRateBattery(int batlevel);
    void heartRateError(String msg);
    void heartRateConnected();
    void heartRateDisconnected();
}
