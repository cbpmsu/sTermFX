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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author  Brian Powell
 */

public class CommPortSetting implements Serializable {

    public static final String[] STOPBITS_STR = {"Invalid", "1", "2", "1.5"};
    public static final String[] PARITY_STR = {"None", "Odd", "Even", "Mark"};
    public static final String[] FLOWCONTROL_STR = {"None", "RtsCts In", "RtsCts Out",
                                                    "", "XonXoff In", "", "", "",
                                                    "XonXoff Out"};

    /**
     * A text description of the communications port
     */
    private String description = "Default Device";

    /**
     * The communications port name that is usually OS specific (COM1, dev/ttys0)
     */
    private String commPortName = "None Found!";

    /**
     * The communications port's baud rate setting
     */
    private int baudRate = 4800;

    /**
     * The communications port's number of data bits setting
     */
    private int dataBits = SerialPort.DATABITS_8;

    /**
     * The communications port's number of stop bits setting
     */
    private int stopBits = SerialPort.STOPBITS_1;

    /**
     * The communications port's parity setting
     */
    private int parity = SerialPort.PARITY_NONE;

    /**
     * The communications port's flow control setting
     */
    private int flowControl = SerialPort.FLOWCONTROL_NONE;

    /**
     * Creates a new instance of CommPortSetting with all default settings
     */
    public CommPortSetting() {
    }

    /**
     * Creates a new instance of CommPortSetting with the given name
     *
     * @param commPortName - The name of this communications port (COM1, dev/ttys0)
     */
    public CommPortSetting(String commPortName) {
        this.commPortName = commPortName;
    }

    /**
     * Creates a new instance of CommPortSetting with the given name
     * and description
     *
     * @param description - A textual description of this communications port
     * @param commPortName - The name of this communications port (COM1, dev/ttys0)
     */
    public CommPortSetting(String description, String commPortName) {
        this.description = description;
        this.commPortName = commPortName;
    }

    /**
     * Creates a new instance of CommPortSetting with the given values
     *
     * @param description - A textual description of this communications port
     * @param commPortName - The name of this communications port (COM1, dev/ttys0)
     * @param baudRate - The baud rate value of this communications port
     * @param dataBits - The data bits value of this communications port
     * @param stopBits - The stop bits value of this communications port
     * @param parity - The parity setting of this communications port
     * @param flowControl - The flow control setting of this communications port
     */
    public CommPortSetting(String description, String commPortName, int baudRate, int dataBits, int stopBits, int parity, int flowControl) {
        this.description = description;
        this.commPortName = commPortName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.flowControl = flowControl;
    }

    /**
     * Gets the description of this communications port
     *
     * @return A string describing the communications port
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the name of this communications port
     *
     * @return The comm port name setting
     */
    public String getCommPortName() {
        return this.commPortName;
    }

    /**
     * Gets the baud rate of this communications port
     *
     * @return The baud rate setting
     */
    public int getBaudRate() {
        return this.baudRate;
    }

    /**
     * Gets the number data bits of this communications port
     *
     * @return The number of data bits setting
     */
    public int getDataBits() {
        return this.dataBits;
    }

    /**
     * Gets the number of stop bits of this communications port
     *
     * @return The number of stop bits setting
     */
    public int getStopBits() {
        return this.stopBits;
    }

    /**
     * Gets the parity setting of this communications port
     *
     * @return The parity setting
     */
    public int getParity() {
        return this.parity;
    }

    /**
     * Gets the flow control setting of this communications port
     *
     * @return The flow control setting
     */
    public int getFlowControl() {
        return this.flowControl;
    }

    /**
     * Sets the description of this communications port
     *
     * @param description A string describing the communications port
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the name of this communications port
     *
     * @param commPortName - The comm port name setting
     */
    public void setCommPortName(String commPortName) {
        this.commPortName = commPortName;
    }

    /**
     * Sets the baud rate of this communications port
     *
     * @param baudRate - The baud rate setting
     */
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    /**
     * Sets the number data bits of this communications port
     *
     * @param dataBits - The number of data bits setting
     */
    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    /**
     * Sets the number of stop bits of this communications port
     *
     * @param stopBits - The number of stop bits setting
     */
    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    /**
     * Sets the parity setting of this communications port
     *
     * @param parity - The parity setting
     */
    public void setParity(int parity) {
        this.parity = parity;
    }

    /**
     * Sets the flow control setting of this communications port
     *
     * @param flowControl - The flow control setting
     */
    public void setFlowControl(int flowControl) {
        this.flowControl = flowControl;
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
               "  Description:  " + description + '\n' +
               "  CommPort ID:  " + commPortName + '\n' +
               "  Baud Rate:  " + baudRate + '\n' +
               "  Data Bits:  " + dataBits + '\n' +
               "  Stop Bits:  " + STOPBITS_STR[stopBits] + '\n' +
               "  Parity:  " + PARITY_STR[parity] + '\n' +
               "  FlowControl:  " + FLOWCONTROL_STR[flowControl];
    }

    /**
     * Used by the Serializable interface to cause the fields of this class to be
     * written to an output stream.
     *
     * @param out - An object output stream to write out the various fields of this class.
     * @throws IOException - if I/O errors occur while writing to the underlying ObjectOutputStream.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Used by the Serializable interface to cause the fields of this class to be
     * read into an object input stream.
     *
     * @param in - An object input stream to read in the various fields of this class.
     * @throws IOException - Any of the usual Input/Output related exceptions.
     * @throws ClassNotFoundException - Class of a serialized object cannot be found.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    /**
     * Utility method to easily view the equivalent number values for the SerialPort
     * static defines of comm port settings
     */
    public static void printSerialPortParamIndexes() {
        System.out.println("Databits -> Index:");
        System.out.println("5 -> " + gnu.io.SerialPort.DATABITS_5);
        System.out.println("6 -> " + gnu.io.SerialPort.DATABITS_6);
        System.out.println("7 -> " + gnu.io.SerialPort.DATABITS_7);
        System.out.println("8 -> " + gnu.io.SerialPort.DATABITS_8);
        System.out.println("Stopbits -> Index:");
        System.out.println("1 -> " + gnu.io.SerialPort.STOPBITS_1);
        System.out.println("1.5 -> " + gnu.io.SerialPort.STOPBITS_1_5);
        System.out.println("2 -> " + gnu.io.SerialPort.STOPBITS_2);
        System.out.println("Parity -> Index:");
        System.out.println("Even -> " + gnu.io.SerialPort.PARITY_EVEN);
        System.out.println("Mark -> " + gnu.io.SerialPort.PARITY_MARK);
        System.out.println("None -> " + gnu.io.SerialPort.PARITY_NONE);
        System.out.println("Odd -> " + gnu.io.SerialPort.PARITY_ODD);
        System.out.println("Space -> " + gnu.io.SerialPort.PARITY_SPACE);
        System.out.println("Flow Control -> Index:");
        System.out.println("None -> " + gnu.io.SerialPort.FLOWCONTROL_NONE);
        System.out.println("RtsCts In -> " + gnu.io.SerialPort.FLOWCONTROL_RTSCTS_IN);
        System.out.println("RtsCts Out -> " + gnu.io.SerialPort.FLOWCONTROL_RTSCTS_OUT);
        System.out.println("XonXoff In -> " + gnu.io.SerialPort.FLOWCONTROL_XONXOFF_IN);
        System.out.println("XonXoff Out -> " + gnu.io.SerialPort.FLOWCONTROL_XONXOFF_OUT);
    }
}
