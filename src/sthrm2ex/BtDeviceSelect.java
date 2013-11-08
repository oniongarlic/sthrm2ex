package sthrm2ex;

import javax.bluetooth.*;
import javax.microedition.lcdui.List;

/**
 *
 * @author milang
 */
public class BtDeviceSelect implements DiscoveryListener {
    
    /**
     *
     */
    public BtDeviceSelect() {
        
    }
    
    public List getDeviceList() {
        List l=new List("Devices", List.IMPLICIT);
        RemoteDevice[] devs=getDevices();
        int i;
        
        for (i=0;i<devs.length;i++) {
            l.append(devs[i].getBluetoothAddress(), null);
        }
        
        return l;
    }
    
    /**
     * Get list of devices we already know about.
     * 
     * @return
     */
    public RemoteDevice[] getDevices() {
        try {
            LocalDevice dev=LocalDevice.getLocalDevice();
            DiscoveryAgent da=dev.getDiscoveryAgent();
            return da.retrieveDevices(DiscoveryAgent.PREKNOWN);            
        } catch (Exception e) {
            Log.loge("SD: ", e);
        }
        return null;
    }

    /**
     * Start inquiry
     */
    public void searchDevices() {
        try {
            LocalDevice dev=LocalDevice.getLocalDevice();
            DiscoveryAgent da=dev.getDiscoveryAgent();
            da.startInquiry(DiscoveryAgent.GIAC, this);
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
