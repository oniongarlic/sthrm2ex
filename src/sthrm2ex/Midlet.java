/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sthrm2ex;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.*;

/**
 * @author milang
 */
public class Midlet extends MIDlet implements 
        HRMListener,
        CommandListener {
    private Form form;                    
    private StringItem hrmData;
    private StringItem hrmErr;
    
    private Command exitCommand;    
    private Command backCommand;    
    private Command okCommand;
    
    private Gauge connectingGauge;
    private Alert connectingAlert;
    
    private HRM hrm;
    
    public Midlet() {
        form=new Form("ST-HRM2 Ex");
        
        exitCommand = new Command("Exit", Command.EXIT, 1);        
        okCommand = new Command("Connect", Command.OK, 1);
        backCommand = new Command("DisConnect", Command.OK, 2);
        
        hrmData = new StringItem("HR", "---");
        hrmErr = new StringItem("MSG", "");
               
        form.addCommand(exitCommand);
        form.addCommand(okCommand);
        
        form.append(hrmData);
        form.append(hrmErr);
        form.setCommandListener(this);
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c==okCommand) {            
            startHRM();
        } else if (c==backCommand) {
            stopHRM();
        } else if (c==exitCommand) {
            exitMIDlet();
        }
    }
    
    private void startHRM() {
        hrmData.setText("Connecting.");
        hrm=new HRM(this);
        hrm.setHRMBtAddress("btspp://FF0BAE1CA3E0:1");        
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
    
    public void heartRateError(String msg) {
        hrmErr.setText(msg);
    }
    
    public void heartRateConnected() {
        form.removeCommand(okCommand);
        form.addCommand(backCommand);
    }
    
    public void heartRateDisconnected() {
        form.removeCommand(backCommand);        
        form.addCommand(okCommand);        
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
