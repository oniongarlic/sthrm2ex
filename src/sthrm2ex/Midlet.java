/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sthrm2ex;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

/**
 * @author milang
 */
public class Midlet extends MIDlet implements 
        HRMListener,
        BtDeviceSelected,
        CommandListener {
    
    private Form form;                    
    private StringItem hrmData;
    private StringItem hrmBat;
    private StringItem hrmErr;
    
    private Command exitCommand;    
    private Command backCommand;    
    private Command okCommand;
    private Command selectDeviceCommand;
    
    private Gauge connectingGauge;
    private Alert connectingAlert;
    
    private HR hr;    
    private HRM hrm;
    
    private BtDeviceSelect btd;
    private String device="";
    
    public Midlet() {
        form=new Form("HRMEx-v0.02");
        
        exitCommand = new Command("Exit", Command.EXIT, 1);
        
        okCommand = new Command("Connect", Command.OK, 1);
        backCommand = new Command("DisConnect", Command.OK, 2);
        
        selectDeviceCommand = new Command("Select device", Command.SCREEN, 1);                
        
        hrmData = new StringItem("HR", "---");
        hrmBat = new StringItem("Battery Level", "---");
        hrmErr = new StringItem("ErrMSG", "");
               
        connectingGauge=new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_IDLE);
        connectingAlert=new Alert("Connecting");
        
        connectingAlert.setIndicator(connectingGauge);
        connectingAlert.setTimeout(Alert.FOREVER);
        
        form.addCommand(exitCommand);
        form.addCommand(okCommand);
        
        form.addCommand(selectDeviceCommand);
        
        form.append(hrmData);
        form.append(hrmBat);
        form.append(hrmErr);
        form.setCommandListener(this);
        
        btd=new BtDeviceSelect(this, form, this);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==okCommand) {
            if (device.length()==12) {
                startHRM();
            } else {
                showError("HRM device not selected.");
            }
        } else if (c==backCommand) {
            stopHRM();
        } else if (c==selectDeviceCommand) {
            btd.showDeviceList();
        } else if (c==exitCommand) {
            exitMIDlet();
        }
    }
    
    public void btDeviceSelected(String address) {        
        if (address.length()==12) {
            device=address;
            switchDisplayable(getInformationAlert("Device selected", "HRM device is "+address), form);            
        } else {            
            device="";
            switchDisplayable(null, form);
        }        
    }
    
    public void showConnecting(String msg) {
        connectingGauge.setValue(Gauge.CONTINUOUS_RUNNING);
        connectingAlert.setString(msg);
        switchDisplayable(connectingAlert, form);
    }
    
    public void hideConnecting() {
        //connectingAlert.setTimeout(200);
        switchDisplayable(null, form);
    }
    
    private void startHRM() {
        showConnecting("Connecting to HRM: "+device);
        hrm=new HRM(this);
        hrm.setHRMBtAddress("btspp://"+device+":1");        
        Thread ht = new Thread(hrm);
        ht.start();        
    }
    
    private void stopHRM() {
        if (hrm!=null)
            hrm.stop();
        hrm=null;
    }
    
    public void heartRate(int hr) {
        hrmData.setText(Integer.toString(hr));
    }
    
    public void heartRateBattery(int batlevel) {
        hrmBat.setText(Integer.toHexString(batlevel));
    }
    
    public void heartRateError(String msg) {
        hrmErr.setText(msg);
    }
    
    public void heartRateConnected() {
        form.removeCommand(okCommand);
        form.addCommand(backCommand);
        hideConnecting();
    }
    
    public void heartRateDisconnected() {
        form.removeCommand(backCommand);
        form.addCommand(okCommand);
        hideConnecting();
    }    
    
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {
        Display display = getDisplay();        
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }
    }
    
    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    public Alert getErrorAlert(String msg) {
        Alert erro = new Alert("Error", msg, null, AlertType.ERROR);
        erro.setTimeout(Alert.FOREVER);
        return erro;
    }
    
    public void showError(String msg) {        
        getDisplay().setCurrent(getErrorAlert(msg));
    }
    
    public Alert getInformationAlert(String title, String msg) {
        return new Alert(title, msg, null, AlertType.CONFIRMATION);
    }
    
    public void showInformation(String title, String msg) {        
        Alert info = new Alert(title, msg, null, AlertType.CONFIRMATION);
        getDisplay().setCurrent(info);
    }    
    
    public void exitMIDlet() {        
        switchDisplayable(null, null);
        notifyDestroyed();
    }
    
    public void startApp() {
        switchDisplayable(null, form);
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
}
