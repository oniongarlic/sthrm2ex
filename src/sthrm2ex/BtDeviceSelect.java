package sthrm2ex;

import java.util.Vector;
import javax.bluetooth.*;
import javax.microedition.lcdui.*;

/**
 *
 * Bluetooth device selector class. Handles seearching, list and saving of default device.
 * 
 * Create an instance
 * 
 * @author milang
 */
public class BtDeviceSelect implements
        DiscoveryListener,
        CommandListener {
    
    private List deviceList;
    
    private Gauge searchingGauge;
    private Alert searchingAlert;
    
    private Command cancelCommand;
    private Command backCommand;
    private Command selectCommand;
    private Command searchCommand;
    
    private Vector devices;
    private RemoteDevice[] devs;
    private RemoteDevice selected;
    private DiscoveryAgent da;              
    
    private Midlet midlet;
    private Displayable parent;
    
    private String defDevice;
    private String filter;
    private String prefixFilter;
    
    private BtDeviceSelected listener;
    
    /**
     *
     */
    public BtDeviceSelect(Midlet midlet, Displayable parent, BtDeviceSelected listener) {                
        this.midlet=midlet;
        this.parent=parent;
        this.listener=listener;
        
        deviceList=new List("Devices", List.IMPLICIT);
        
        selectCommand=new Command("Select", Command.OK, 1);
        cancelCommand=new Command("Cancel", Command.BACK, 1);
        backCommand=new Command("Back", Command.BACK, 1);
        searchCommand=new Command("Rescan", Command.SCREEN, 1);
        
        searchingGauge=new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_IDLE);
        searchingAlert=new Alert("Searching");
        searchingAlert.setIndicator(searchingGauge);
        searchingAlert.setTimeout(Alert.FOREVER);
        
        devices=new Vector();
        
        deviceList.setCommandListener(this);
    }
    
    public String getDefaultAddress() {
        return defDevice;
    }
    
    public void setDefaultAddress(String btMac) {
        defDevice=btMac;
    }
    
    public RemoteDevice getSelected() {
        return selected;
    }
    
    /*
     * Set a filter so only device that match it gets discovered
     */
    public void setDeviceNameFilter(String filter) {
        this.filter=filter;
    }

    public void setDeviceNamePrefixFilter(String filter) {
        this.prefixFilter=filter;
    }    
    
    public void commandAction (Command c, Displayable d) {
        if (c==selectCommand) {
            int i=deviceList.getSelectedIndex();
            if (i!=-1) {
                selected=(RemoteDevice)devices.elementAt(i);
            } else {
                selected=null;
            }
            midlet.switchDisplayable(null, parent);
            listener.btDeviceSelected(selected!=null ? selected.getBluetoothAddress() : "");
        } else if (c==backCommand) {
            midlet.switchDisplayable(null, parent);
        } else if (c==cancelCommand) {
            if (da!=null) {                
                da.cancelInquiry(this);                
            }
        } else if (c==searchCommand) {
            doScan();
        }
    }
    
    public void showConnecting(String msg) {
        searchingGauge.setValue(Gauge.CONTINUOUS_RUNNING);
        searchingAlert.setString(msg);
        midlet.switchDisplayable(searchingAlert, deviceList);
    }
    
    public void hideConnecting() {        
        midlet.switchDisplayable(null, deviceList);
    }
    
    public void showDeviceList() {        
        //devs=getDevices();
        //if (devs!=null) {
        //    doCachedScan();
        //} else {
            doScan();
        //}
        midlet.switchDisplayable(null, deviceList);
    }
    
    private void doCachedScan() {
            deviceList.deleteAll();
            appendDevices(devs);
            deviceList.addCommand(backCommand);
            deviceList.setSelectCommand(null);
    }
    
    private void doScan() {
        deviceList.deleteAll();
        deviceList.setTitle("Searching...");
        deviceList.removeCommand(searchCommand);
        deviceList.removeCommand(backCommand);
        deviceList.addCommand(cancelCommand);
        deviceList.setSelectCommand(selectCommand);
        searchDevices();
    }
    
    /*
     * 
     */
    private void scanDoneView() {
        deviceList.setTitle("Devices");
        deviceList.setSelectCommand(selectCommand);
        deviceList.removeCommand(cancelCommand);
        deviceList.addCommand(searchCommand);
        deviceList.addCommand(backCommand);       
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
    private RemoteDevice[] getDevices() {
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
    private void searchDevices() {
        try {
            LocalDevice dev=LocalDevice.getLocalDevice();
            da=dev.getDiscoveryAgent();
            da.startInquiry(DiscoveryAgent.GIAC, this);
        } catch (Exception e) {
            Log.loge("SD: ", e);
            da=null;
        }        
    }    
    
    /*
     * Bluetooth discovery callbacks
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        String lName;        
        
        lName=btDevice.getBluetoothAddress();        
        try {
            lName=btDevice.getFriendlyName(false);
        } catch (Exception e) {
            Log.loge("GFN: ", e);
        }
        
        // check if we have any filter, if so, check if we have match
        if (prefixFilter.length()>0) {
            if (lName.startsWith(prefixFilter)==false)
                return;
        }

        if (filter.length()>0) {
            if (lName.compareTo(filter)!=0)
                return;
        }
                
        devices.addElement(btDevice);
        deviceList.append(lName, null);
    }

    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void serviceSearchCompleted(int transID, int respCode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void inquiryCompleted(int discType) {
        da=null;
        scanDoneView();
    }

}
