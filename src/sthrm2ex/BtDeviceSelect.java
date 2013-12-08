package sthrm2ex;

import java.io.IOException;
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
        CommandListener
        {
    
    private List deviceList;
    
    private Gauge searchingGauge;
    private Alert searchingAlert;
    
    private Command cancelCommand;
    private Command backCommand;    
    private Command searchCommand;
    
    private Vector devices;    
    private RemoteDevice selected;    
    
    private Midlet midlet;
    private Displayable parent;
    
    private String defDevice;
    private String filter="";
    private String prefixFilter="";
    
    private BtDeviceSelected listener;
    
    private startInquiryThread inquiry;
    
    /**
     *
     */
    public BtDeviceSelect(Midlet midlet, Displayable parent, BtDeviceSelected listener) {                
        this.midlet=midlet;
        this.parent=parent;
        this.listener=listener;
                
        deviceList=new List("Devices", List.IMPLICIT);
        deviceList.setCommandListener(this);        
                
        cancelCommand=new Command("Cancel", Command.BACK, 1);
        backCommand=new Command("Back", Command.BACK, 1);
        searchCommand=new Command("Rescan", Command.SCREEN, 1);
        
        searchingGauge=new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_IDLE);
        searchingAlert=new Alert("Searching");
        searchingAlert.setIndicator(searchingGauge);
        searchingAlert.setTimeout(Alert.FOREVER);
        searchingAlert.setCommandListener(this);
        
        devices=new Vector();
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
        Log.log("BtDevSelCA");
        if (c==List.SELECT_COMMAND) {
            int i=deviceList.getSelectedIndex();
            if (i!=-1) {
                selected=(RemoteDevice)devices.elementAt(i);
            } else {
                selected=null;
            }
            listener.btDeviceSelected(selected!=null ? selected.getBluetoothAddress() : null);            
        } else if (c==backCommand) {
            listener.btDeviceSelected(null);            
        } else if (c==cancelCommand) {
            inquiry.cancelInquiry();
            inquiry=null;
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
        // Initiate a scan only on first time if we have a list of devices
        if (devices.isEmpty()) {
            doScan();
        }
        midlet.switchDisplayable(null, deviceList);
    }
    
    private void doScan() {
        Log.log("BtDevSelDoScan");
        devices.removeAllElements();
        deviceList.deleteAll();
        deviceList.setTitle("Searching...");
        deviceList.removeCommand(searchCommand);
        deviceList.removeCommand(backCommand);
        deviceList.addCommand(cancelCommand);
        deviceList.setSelectCommand(null);
        searchDevices();
    }
    
    /*
     * 
     */
    private void scanDoneView() {
        deviceList.setTitle("Devices");
        deviceList.setSelectCommand(List.SELECT_COMMAND);
        deviceList.removeCommand(cancelCommand);
        deviceList.addCommand(searchCommand);
        deviceList.addCommand(backCommand);       
    }
    
    /**
     * Start inquiry
     */
    private void searchDevices() {       
        inquiry=new startInquiryThread(this);
        inquiry.start();
    }
    
    private class startInquiryThread extends Thread {        
        private BtDeviceSelect parent;
        private LocalDevice dev;
        private DiscoveryAgent da;
        
        startInquiryThread(BtDeviceSelect parent) {
            this.parent=parent;
        }
        
        public void run() {
            try {
                dev=LocalDevice.getLocalDevice();                    
                da=dev.getDiscoveryAgent();                                
                da.startInquiry(DiscoveryAgent.GIAC, parent);                    
            } catch (Exception e) {
                Log.loge("SD: ", e);
                da=null;
            }                
        }
        
        public void cancelInquiry() {
            if (da!=null) {
                da.cancelInquiry(parent);
            }
        }
    }
    
    /*
     * Bluetooth discovery callbacks
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        String lName=null;
                        
        try {            
            lName=btDevice.getFriendlyName(false);
        } catch (IOException e) {
            Log.loge("GFN: ", e);            
        }
        if (lName==null)
            lName=btDevice.getBluetoothAddress();
        else if (lName.length()==0)
            lName=btDevice.getBluetoothAddress();
        
        // check if we have any filter, if so, check if we have match
        if (prefixFilter!=null && prefixFilter.length()>0) {
            if (lName.startsWith(prefixFilter)==false)
                return;
        }

        if (filter!=null && filter.length()>0) {
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
        inquiry=null;
        scanDoneView();
    }

}
