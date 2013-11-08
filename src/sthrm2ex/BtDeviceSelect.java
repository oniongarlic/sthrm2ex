/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sthrm2ex;

import javax.bluetooth.*;

/**
 *
 * @author milang
 */
public class BtDeviceSelect implements DiscoveryListener {

    public void searchDevices() {
        try {
            LocalDevice dev=LocalDevice.getLocalDevice();
            DiscoveryAgent da=dev.getDiscoveryAgent();
            da.
        } catch (Exception e) {
            Log.loge("SD: ", e);
        }
    }
    
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void serviceSearchCompleted(int transID, int respCode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void inquiryCompleted(int discType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
