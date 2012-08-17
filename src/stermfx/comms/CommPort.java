/*
 *  sTermFX - A serial terminal application with some nifty features.
 *  Copyright (C) 2012  Brian Powell
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package stermfx.comms;

import gnu.io.SerialPort;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author  Brian Powell
 *
 * Possible string values are as follows (case agnostic):
 *      DataBits - "5", "6", "7", "8"
 *      StopBits - "1", "2", "1.5"
 *      Parity - "None", "Odd", "Even", "Mark", "Space"
 *      FlowControl - "None", "RtsCts In", "RtsCts Out", "XonXoff In", "XonXoff Out"
 */

public class CommPort {

    /**
     * A text description of the communications port
     */
    private SimpleStringProperty description = new SimpleStringProperty("");

    /**
     * The communications port name that is usually OS specific (COM1, dev/ttys0)
     */
    private SimpleStringProperty commPortName = new SimpleStringProperty("");

    /**
     * The communications port's baud rate setting
     */
    private SimpleStringProperty baudRate = new SimpleStringProperty("");

    /**
     * The communications port's number of data bits setting
     */
    private SimpleStringProperty dataBits = new SimpleStringProperty("");

    /**
     * The communications port's number of stop bits setting
     */
    private SimpleStringProperty stopBits = new SimpleStringProperty("");

    /**
     * The communications port's parity setting
     */
    private SimpleStringProperty parity = new SimpleStringProperty("");

    /**
     * The communications port's flow control setting
     */
    private SimpleStringProperty flowControl = new SimpleStringProperty("");

    /**
     * The comm port interface object
     */
    private CommPortInterface cpi = null;

    /**
     * The persistent settings for this comm port object
     */
    private Properties commSettings;
    private File commSettingsFile;

    /**
     * Creates a new instance of CommPort
     */
    public CommPort(CommRxEvent rxEvent, String propertiesLocation) throws IOException{
        // create the comm port interface
        cpi = new CommPortInterface(rxEvent);
        // load the settings
        commSettingsFile = new File(propertiesLocation);
        Properties defaultProps = new Properties();
        defaultProps.load(getClass().getResourceAsStream("commdefaults.properties"));
        commSettings = new Properties(defaultProps);
        if (commSettingsFile.exists()) {
            try (FileInputStream in = new FileInputStream(commSettingsFile))
            {
                commSettings.load(in);
            }
        }
        // setup the class members
        description.setValue(commSettings.getProperty("description", "Undefined"));
        commPortName.setValue(commSettings.getProperty("comm.port.name"));
        baudRate.setValue(commSettings.getProperty("baud.rate"));
        dataBits.setValue(commSettings.getProperty("data.bits"));
        stopBits.setValue(commSettings.getProperty("stop.bits"));
        parity.setValue(commSettings.getProperty("parity"));
        flowControl.setValue(commSettings.getProperty("flow.control"));

        //commPortName.setValue("COM14");
    }

    /**
     * Saves the persistent settings of this comm port to the specified properties
     * location.
     *
     * @throws IOException If a problem occurs while trying to store the properties file.
     */
    public void saveSettings() throws IOException {
        // save the class members to local settings
        commSettings.setProperty("description", getDescription());
        commSettings.setProperty("comm.port.name", getCommPortName());
        commSettings.setProperty("baud.rate", getBaudRate());
        commSettings.setProperty("data.bits", getDataBits());
        commSettings.setProperty("flow.control", getFlowControl());
        commSettings.setProperty("parity", getParity());
        commSettings.setProperty("stop.bits", getStopBits());

        // create the comm settings file if it doesn't exist
        if (!commSettingsFile.exists())
        {
            commSettingsFile.getParentFile().mkdirs();
            commSettingsFile.createNewFile();
        }
        // store the comm settings
        try (FileOutputStream out = new FileOutputStream(commSettingsFile))
        {
            commSettings.store(out, "---CommPort Settings---");
        }
    }

    /**
     * Gets the description of this communications port
     *
     * @return A string describing the communications port
     */
    public String getDescription() {
        return description.getValue();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    /**
     * Gets the name of this communications port
     *
     * @return The comm port name setting
     */
    public String getCommPortName() {
        //return this.commPortName;
        return commPortName.getValue();
    }

    public StringProperty commPortNameProperty() {
        return commPortName;
    }

    /**
     * Gets the baud rate of this communications port
     *
     * @return The baud rate setting
     */
    public String getBaudRate() {
        return baudRate.getValue();
    }

    public StringProperty baudRateProperty() {
        return baudRate;
    }

    /**
     * Gets the number data bits of this communications port
     *
     * @return The number of data bits setting
     */
    public String getDataBits() {
        return dataBits.getValue();
    }

    public StringProperty dataBitsProperty() {
        return dataBits;
    }

    /**
     * Gets the number data bits of this communications port as a raw value
     * derived from the SerialPort class.
     *
     * DataBits - "5", "6", "7", "8"
     *
     * @return The number of data bits setting
     */
    public int getDataBitsRaw() {
        String dbSetting = getDataBits().toLowerCase(Locale.ENGLISH);
        switch (dbSetting)
        {
            case "5":
                return SerialPort.DATABITS_5;
            case "6":
                return SerialPort.DATABITS_6;
            case "7":
                return SerialPort.DATABITS_7;
            default:
                return SerialPort.DATABITS_8;
        }
    }

    /**
     * Gets the number of stop bits of this communications port
     *
     * @return The number of stop bits setting
     */
    public String getStopBits() {
        return stopBits.getValue();
    }

    public StringProperty stopBitsProperty() {
        return stopBits;
    }

    /**
     * Gets the number of stop bits of this communications port as a raw value
     * derived from the SerialPort class.
     *
     * @return The number of stop bits setting
     */
    public int getStopBitsRaw() {
        String sbSetting = getStopBits().toLowerCase(Locale.ENGLISH);
        switch (sbSetting)
        {
            case "2":
                return SerialPort.STOPBITS_2;
            case "1.5":
                return SerialPort.STOPBITS_1_5;
            default:
                return SerialPort.STOPBITS_1;
        }
    }

    /**
     * Gets the parity setting of this communications port
     *
     * @return The parity setting
     */
    public String getParity() {
        return parity.getValue();
    }

    public StringProperty parityProperty() {
        return parity;
    }

    /**
     * Gets the parity setting of this communications port as a raw value
     * derived from the SerialPort class.
     *
     * @return The parity setting
     */
    public int getParityRaw() {
        String pSetting = getParity().toLowerCase(Locale.ENGLISH);
        switch (pSetting)
        {
            case "odd":
                return SerialPort.PARITY_ODD;
            case "even":
                return SerialPort.PARITY_EVEN;
            case "mark":
                return SerialPort.PARITY_MARK;
            case "space":
                return SerialPort.PARITY_SPACE;
            default:
                return SerialPort.PARITY_NONE;
        }
    }

    /**
     * Gets the flow control setting of this communications port
     *
     * @return The flow control setting
     */
    public String getFlowControl() {
        return flowControl.getValue();
    }

    public StringProperty flowControlProperty() {
        return flowControl;
    }

    /**
     * Gets the flow control setting of this communications port as a raw value
     * derived from the SerialPort class.
     *
     * @return The flow control setting
     */
    public int getFlowControlRaw() {
        String fcSetting = getFlowControl().toLowerCase(Locale.ENGLISH);
        switch (fcSetting)
        {
            case "rtscts in":
                return SerialPort.FLOWCONTROL_RTSCTS_IN;
            case "rtscts out":
                return SerialPort.FLOWCONTROL_RTSCTS_OUT;
            case "xonxoff in":
                return SerialPort.FLOWCONTROL_XONXOFF_IN;
            case "xonxoff out":
                return SerialPort.FLOWCONTROL_XONXOFF_OUT;
            default:
                return SerialPort.FLOWCONTROL_NONE;
        }
    }

    /**
     * Get the CommPortInterface object for this comm port.
     *
     * @return The local CommPortInterface class.
     */
    public CommPortInterface commPortInterface() {
        return cpi;
    }

    /**
     * Sets the description of this communications port
     *
     * @param description A string describing the communications port
     */
    public void setDescription(String newValue) {
        description.setValue(newValue);
    }

    /**
     * Sets the name of this communications port
     *
     * @param commPortName - The comm port name setting
     */
    public void setCommPortName(String newValue) {
        commPortName.setValue(newValue);
    }

    /**
     * Sets the baud rate of this communications port
     *
     * @param baudRate - The baud rate setting
     */
    public void setBaudRate(String newValue) {
        baudRate.setValue(newValue);
    }

    /**
     * Sets the number data bits of this communications port
     *
     * @param dataBits - The number of data bits setting
     */
    public void setDataBits(String newValue) {
        dataBits.setValue(newValue);
    }

    /**
     * Sets the number of stop bits of this communications port
     *
     * @param stopBits - The number of stop bits setting
     */
    public void setStopBits(String newValue) {
        stopBits.setValue(newValue);
    }

    /**
     * Sets the parity setting of this communications port
     *
     * @param parity - The parity setting
     */
    public void setParity(String newValue) {
        parity.setValue(newValue);
    }

    /**
     * Sets the flow control setting of this communications port
     *
     * @param flowControl - The flow control setting
     */
    public void setFlowControl(String newValue) {
        flowControl.setValue(newValue);
    }

    /**
     * The toString() method for this class that returns all fields of this
     * object as a string.
     *
     * @return A string representation of this communications port object.
     */
    @Override
    public String toString() {
        return "-CommPort Settings-\n" +
               "  Description:  " + getDescription() + '\n' +
               "  CommPort ID:  " + getCommPortName() + '\n' +
               "  Baud Rate:  " + getBaudRate() + '\n' +
               "  Data Bits:  " + getDataBits() + '\n' +
               "  Stop Bits:  " + getStopBits() + '\n' +
               "  Parity:  " + getParity() + '\n' +
               "  FlowControl:  " + getFlowControl();
    }

    /**
     * Utility method to easily view the equivalent number values for the SerialPort
     * static defines of comm port settings
     */
    public static void printSerialPortParamIndexes() {
        System.out.println("Databits -> Index:");
        System.out.println("5 -> " + SerialPort.DATABITS_5);
        System.out.println("6 -> " + SerialPort.DATABITS_6);
        System.out.println("7 -> " + SerialPort.DATABITS_7);
        System.out.println("8 -> " + SerialPort.DATABITS_8);
        System.out.println("Stopbits -> Index:");
        System.out.println("1 -> " + SerialPort.STOPBITS_1);
        System.out.println("1.5 -> " + SerialPort.STOPBITS_1_5);
        System.out.println("2 -> " + SerialPort.STOPBITS_2);
        System.out.println("Parity -> Index:");
        System.out.println("Even -> " + SerialPort.PARITY_EVEN);
        System.out.println("Mark -> " + SerialPort.PARITY_MARK);
        System.out.println("None -> " + SerialPort.PARITY_NONE);
        System.out.println("Odd -> " + SerialPort.PARITY_ODD);
        System.out.println("Space -> " + SerialPort.PARITY_SPACE);
        System.out.println("Flow Control -> Index:");
        System.out.println("None -> " + SerialPort.FLOWCONTROL_NONE);
        System.out.println("RtsCts In -> " + SerialPort.FLOWCONTROL_RTSCTS_IN);
        System.out.println("RtsCts Out -> " + SerialPort.FLOWCONTROL_RTSCTS_OUT);
        System.out.println("XonXoff In -> " + SerialPort.FLOWCONTROL_XONXOFF_IN);
        System.out.println("XonXoff Out -> " + SerialPort.FLOWCONTROL_XONXOFF_OUT);
    }
}
