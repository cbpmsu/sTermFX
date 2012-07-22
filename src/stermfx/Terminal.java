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

package stermfx;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import javax.swing.Timer;
import stermfx.comms.CommPortInterface;
import stermfx.comms.CommPortSetting;
import stermfx.comms.CommRxEvent;

/**
 *
 * @author Brian Powellx
 */
public class Terminal implements Initializable
{

    @FXML
    TextArea terminalTA;
    @FXML
    Button button;
//    @FXML
//    WebView myWebView;
    Timer caretTimer;
    CommPortInterface cpi;
    String terminalBuffer;
    boolean terminalBufferDirty;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        terminalBuffer = "";
        terminalBufferDirty = false;

        caretTimer = new Timer(500, new ActionListener()
        {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent ae)
            {
                caretAction();
            }
        });
        caretTimer.setRepeats(true);
        caretTimer.setInitialDelay(40);
        caretTimer.start();

        if (button != null)
        {
            button.setOnAction(new EventHandler<ActionEvent>()
            {

                @Override
                public void handle(ActionEvent event)
                {
                    System.out.println(terminalTA.getOpacity());
                    if (terminalTA.getOpacity() == 1)
                    {
                        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), terminalTA);
                        fadeOut.setFromValue(1.0);
                        fadeOut.setToValue(0.0);
                        fadeOut.play();
                    }
                    else
                    {
                        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), terminalTA);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    }
                }
            });
        }

//        if (myWebView != null)
//        {
//            myWebView.setOnKeyTyped(new EventHandler<KeyEvent>() {
//
//                @Override
//                public void handle(KeyEvent arg0)
//                {
//                    try
//                    {
//                        //System.out.println("Key: " + arg0.getCharacter().getBytes()[0]);
//                        cpi.sendByte(arg0.getCharacter().getBytes()[0]);
//                        myWebView.getEngine().loadContent("<!DOCTYPE html>"
//                                + "<html lang=\"en-US\">"
//                                + "<head>"
//                                + "<meta charset=utf-8>"
//                                + "<style type=\"text/css\">"
//                                + "p {font-family:\"monospaced\"; font-size:18;}"
//                                + "</style>"
//                                + "</head>"
//                                + "<body>"
//                                + "<p>" + terminalBuffer + "</p>"
//                                + "</body>"
//                                + "</html>");
//                    }
//                    catch (IOException ex)
//                    {
//                        Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            });
//        }

        if (terminalTA != null)
        {
            terminalTA.setOnKeyTyped(new EventHandler<KeyEvent>()
            {

                @Override
                public void handle(KeyEvent arg0)
                {
                    //addCharacterToTerminal(arg0.getCharacter());
                    //System.out.println("Key: " + arg0.getCharacter().getBytes()[0]);
                    try
                    {
                        cpi.sendByte(arg0.getCharacter().getBytes()[0]);
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }

        cpi = new CommPortInterface(new CommRxEvent()
        {

            @Override
            public void byteReceived(byte rxByte)
            {
                addCharacterToTerminal(new String(new byte[] {rxByte}));
            }
        });
        try
        {
            CommPortSetting cps = new CommPortSetting("CommPort", "COM5");
            cps.setBaudRate(115200);
            cpi.openCommPort(cps);
        }
        catch (PortInUseException | IOException | TooManyListenersException | UnsupportedCommOperationException ex)
        {
            Logger.getLogger(Terminal.class.getName()).log(Level.SEVERE, null, ex);
        }
        //terminalTA.setEditable(true);
    }

    private void addCharacterToTerminal(String character)
    {
        //caretTimer.stop();
        //String text = terminalTA.getText();
        //text = text.substring(0, text.length() - 1);
        //terminalTA.deletePreviousChar();
        byte key = character.getBytes()[0];
        switch (key)
        {
            case 8:
                terminalBuffer = terminalBuffer.substring(0, terminalBuffer.length() - 1);
                break;
//            case 13:
//                terminalBuffer += "<br>";
//                terminalBuffer += "\n";
//                break;
            default:
                terminalBuffer += character;
        }
        // only restart if this is first character since the timer has fired
        if (!terminalBufferDirty)
            caretTimer.restart();
        terminalBufferDirty = true;
        //terminalTA.setText("");
        //terminalTA.appendText(text + "_");
        //terminalTA.appendText("_");
    }

    private void caretAction()
    {
        // always service the terminal buffer first before dealing with the cursor
        if (terminalBufferDirty)
        {
            // update the text area and append a blank character to take care of scrolling
            terminalTA.appendText(terminalBuffer + "_");
            terminalBuffer = "";
            terminalBufferDirty = false;
        }
        else
        {
            if (terminalTA.getSelectedText().length() > 0)
            {
                return;
            }

            terminalTA.setEditable(true);
            terminalTA.appendText("");
            if (terminalTA.getText().endsWith("_"))
            {
                terminalTA.deletePreviousChar();
                terminalTA.appendText(" ");
            }
            else
            {
                terminalTA.deletePreviousChar();
                terminalTA.appendText("_");
            }
            terminalTA.setEditable(false);
        }
    }
}
