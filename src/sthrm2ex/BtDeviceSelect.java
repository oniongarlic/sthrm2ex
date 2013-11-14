package sthrm2ex;

import javax.bluetooth.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author milang
 */
public class BtDeviceSelect implements
        DiscoveryListener,
        CommandListener {
    
    private List deviceList;
    private Command cancelCommand;
    private Command selectCommand;          
    
    private DiscoveryAgent da;
    
    private Displayable parent;
    
    /**
     *
     */
    public BtDeviceSelect(Displayable parent) {
        deviceList=new List("Devices", List.IMPLICIT);
        
        this.parent=parent;
        
        selectCommand=new Command("Select", Command.OK, 1);
        cancelCommand=new Command("Cancel", Command.BACK, 1);
        
        deviceList.setSelectCommand(selectCommand);
        deviceList.addCommand(cancelCommand);
        
        deviceList.setCommandListener(this);
    }
    
    public void commandAction (Command c, Displayable d) {
        if (c==selectCommand) {
            
        } else if (c==cancelCommand) {
            if (da!=null)
                da.cancelInquiry(this);
        }
    }
    
    public List getDeviceList() {        
        RemoteDevice[] devs=getDevices();
        
        deviceList.deleteAll();
        if (devs!=null) {
            appendDevices(devs);
        } else {
            deviceList.setTitle("Searching...");
            deviceList.setSelectCommand(null);
            searchDevices();
        }
        
        return deviceList;
    }
    
    private void appendDevices(RemoteDevice[] devs) {
        int i;
        
        for (i=0;i<devs.length;i++) {
            deviceList.append(devs[i].getBluetoothAddress(), null);
        }    
    }
    
    /**
     * Get list of devices we already know about.
     * 
     * @return
     */
    public RemoteDevice[] getDevices() {
        try {
            LocalDevice dev=LocalDevice.getLocalDevice();
            da=dev.getDiscoveryAgent();
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
            da=dev.getDiscoveryAgent();
            da.startInquiry(DiscoveryAgent.GIAC, this);
        } catch (Exception e) {
            Log.loge("SD: ", e);
        }        
    }
    
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {        
        deviceList.append(btDevice.getBluetoothAddress(), null);        
    }

    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void serviceSearchCompleted(int transID, int respCode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void inquiryCompleted(int discType) {
        deviceList.setTitle("Devices");
        deviceList.setSelectCommand(selectCommand);
    }

}
