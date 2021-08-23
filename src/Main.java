import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
        **SUMMARY**
    What is an entropy ? It help to find out if a file is concidered random or not in a scale of 0 to 1.
    0 for the lowest entropy (ie compressed files) or 1 for the highest entropy (ie obfuscated file like malware / encrypted files).

    Each unique byte correspond to an occurrence.
    The probability of a particular byte opposed to the others is calculated by the
    division of the byte occurrency and the number of byte from the analysed file.

    Then for each element of an array that contain an unique byte as a key and his occurrency,
    i apply this formula:
    P(x(h))*log2 p(x(h))

    "P(x(h))" is the probability of finding out a particular byte.

    In java, "log2 p(x(h))" is translated to:
    Math.log(p(x(h)) / Math.log(2)

    Formula:
    value = p(x(h) * (Math.log(p(x(h)) / Math.log(2))

    Now, we only have the entropy of a particular byte. We have to use the same formula for each
    bytes and increment the result with the previous one:

    entropy = entropy + value
 */

public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Entropy mV = new Entropy();
            }
        });
    }
}
