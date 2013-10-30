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
    void heartRateError(String msg);
    void heartRateConnected();
    void heartRateDisconnected();
}
