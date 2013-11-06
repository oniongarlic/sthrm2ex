/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sthrm2ex;

import javax.microedition.io.Connector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.StreamConnection;
import java.io.DataInputStream;
import java.io.IOException;


/**
 *
 * @author milang
 */
public class HRM implements Runnable {
    
    private final int STATE_INITIAL=1;
    private final int STATE_MARK1=2;
    private final int STATE_MARK2=3;
    
    private final int STATE_UNKNOWN1=4;
    private final int STATE_BATTERY=5;
    private final int STATE_UNKNOWN3=6;
    private final int STATE_UNKNOWN4=7;
    
    private final int STATE_HRM=50;    
    
    private boolean connected=false;
    private boolean isActive=false;
    
    private int state=STATE_INITIAL;
    private int mark1=0;
    private int mark2=0;
    private int u1=0;
    private int u2=0;
    private int battery=0;
    private int hr=0;
    
    private String bt;
    
    private StreamConnection c;
    private DataInputStream data;
    
    private HRMListener lh;
    
    public HRM(HRMListener lh) {
        this.lh=lh;
    }
    
    public void setHRMBtAddress(String bt) {
        this.bt=bt;
    }
    
    /*
    */
    private boolean readHRMData() {
        int i=-1;
                       
        try {
            i=data.read();
        } catch (IOException e) {
            Log.loge("IOe", e);
            return false;
        }
        
        if (i==-1)
            return false;
        if (i==0)
            return true;                
        
        switch (state) {
            // Looking for start message byte 0xFA
            case STATE_INITIAL:
                if (i==0xFA) {
                    // We got start marker, go to next state
                    state=STATE_MARK1;
                } else {
                    return true;
                }
                break;
            case STATE_MARK1:
                mark1=i;
                state=STATE_MARK2;
                break;
            case STATE_MARK2:
                mark2=i;
                if (mark1+mark2!=0xFF) {
                    state=STATE_INITIAL;
                    return true;
                }
                state=STATE_UNKNOWN1;
                break;
            case STATE_UNKNOWN1:
                // 0x81 ?
                u1=i;
                state=STATE_BATTERY;
                break;
            case STATE_BATTERY:
                // Battery level ?                
                if (u1==0x81 && i!=battery) {
                    battery=i;
                    lh.heartRateBattery(i);
                }                
                state=STATE_HRM; 
                break;
            case STATE_HRM:
                hr=i;
                lh.heartRate(hr);
                // Ok, we got what we need, wait for initial marker. We ignore anything else for now
                state=STATE_INITIAL;
                break;
            case STATE_UNKNOWN3:
                state=STATE_UNKNOWN4;
                break;
            case STATE_UNKNOWN4:
                state=STATE_HRM;
                break;
        }
        
        return true;
    }
    
    public int getHR() {
        return hr;
    }
    
    private boolean connect() {
        try {
            c = (StreamConnection) Connector.open(bt, Connector.READ_WRITE, true);            
            data = new DataInputStream(c.openInputStream());            
            connected = true;
            lh.heartRate(0);            
            lh.heartRateConnected();
            return true;
        } catch (ConnectionNotFoundException e) {
            lh.heartRateError("CNFE"+e.getMessage());
        } catch (IOException e) {    
            disconnect();
            lh.heartRateError("IOe"+e.getMessage());
        } catch (IllegalArgumentException e) {
            lh.heartRateError("IAE"+e.getMessage());
        } catch (SecurityException e) {
            lh.heartRateError("SE"+e.getMessage());
        }
        return false;
    }
    
    private void disconnect() {        
        try {
            if (data!=null)
                data.close();
            if (c!=null)
                c.close();
        } catch (IOException e) {
            
        }
        data=null;
        c=null;
        connected = false;
        lh.heartRateDisconnected();
    }
    
    public void stop() {
        isActive=false;
    }
    
    public void run() {        
        if (connect()==false) {
            lh.heartRateError("ConFail");
            lh.heartRateDisconnected();
            return;
        } else {
            lh.heartRateConnected();
        }
        isActive=true;        
        while (isActive) {
            boolean r=readHRMData();
            if (r==false) {
                lh.heartRateError("RF");
                // return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                //
            }
        }
        disconnect();
    }        
}
