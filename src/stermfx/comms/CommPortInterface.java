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

import gnu.io.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 *
 * @author Brian Powell
 */
public class CommPortInterface implements SerialPortEventListener
{

    /**
     * A data input stream that ties to the input of the serial port
     */
    private DataInputStream inputStream = null;
    /**
     * A data output stream that ties to the output of the serial port
     */
    private DataOutputStream outputStream = null;
    /**
     * A serial port object that will be used in this port interface
     */
    private SerialPort serialPort = null;
    /**
     * A single CommRxEvent object that provides a callback for received bytes
     */
    private CommRxEvent rxEvent;
    /**
     * A flag for detemining if the port is open or not
     */
    private boolean portOpen = false;
    /**
     * Tells if the communications port is open or not
     *
     * @return true if the port is open
     */
    public boolean isPortOpen()
    {
        return this.portOpen;
    }

    /** Creates a new instance of CommPortInterface */
    public CommPortInterface(CommRxEvent _rxEvent) {
        // Set the rx event object
        this.rxEvent = _rxEvent;
    }

    /**
     * Opens the communications port specified by the given comm port setting
     *
     * @param cp Comm port settings specifying details of the port to open
     * @throws PortInUseException port is already in use
     * @throws IOException a general I/O exception occured at the lower levels
     * @throws TooManyListenersException too many registered serial event listeners
     * @throws UnsupportedCommOperationException comm operation is not supported
     */
    public void openCommPort(CommPort cp) throws PortInUseException,
                                                        IOException,
                                                        TooManyListenersException,
                                                        UnsupportedCommOperationException
    {
        CommPortIdentifier portId;
        Enumeration portList;
        boolean commPortNameFound = false;

        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements())
        {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                if (portId.getName().equals(cp.getCommPortName()))
                {
                    commPortNameFound = true;
                    serialPort = (SerialPort) portId.open("TstatSimulator", 2000);

                    inputStream = new DataInputStream(serialPort.getInputStream());
                    outputStream = new DataOutputStream(serialPort.getOutputStream());

                    serialPort.addEventListener(this);
                    serialPort.notifyOnDataAvailable(true);
                    //serialPort.notifyOnOutputEmpty(true);
                    serialPort.setSerialPortParams(Integer.parseInt(cp.getBaudRate()),
                            cp.getDataBitsRaw(),
                            cp.getStopBitsRaw(),
                            cp.getParityRaw());
                    serialPort.setFlowControlMode(cp.getFlowControlRaw());
                }
            }
        }
        if (!commPortNameFound)
        {
            throw new IOException("CommPort name '" + cp.getCommPortName() + "' not found!");
        }

        portOpen = true;
    }

    /**
     * Closes the communications port
     *
     * @throws IOException a general I/O exception occured at the lower levels
     */
    public void closeCommPort() throws IOException
    {
        portOpen = false;
        serialPort.close();
        inputStream.close();
        outputStream.close();
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent)
    {
        switch(serialPortEvent.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                try {
                    // Signal rx events for each received byte
                    while (inputStream.available() > 0) {
                        rxEvent.byteReceived(inputStream.readByte());
                    }
                } catch (Exception ex) { }

                break;
        }
    }

    public void sendByte(byte byteToSend) throws IOException
    {
        outputStream.writeByte(byteToSend);
    }

    public static void listPorts()
    {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while ( portEnum.hasMoreElements() )
        {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
        }
    }

    public static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
}
