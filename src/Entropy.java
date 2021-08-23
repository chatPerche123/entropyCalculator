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

public class Entropy extends JFrame implements ActionListener {
    private JPanel contentPanel;
    private JPanel dAndDPanel;
    private JTextPane dAndDTextPane;
    private JScrollPane resultScrollPane;
    private JTextPane resultTextPane;
    private JPanel buttonsPanel;
    private JButton resetButton;
    private java.util.List<File> droppedFiles;

    public Entropy() {
        contentPanel = new JPanel();
        dAndDPanel = new JPanel();

        resetButton = new JButton();
        dAndDTextPane = new JTextPane();
        resultScrollPane = new JScrollPane();
        resultTextPane = new JTextPane();
        resultTextPane.setEditable(false);
        resultTextPane.setContentType("text/html");
        buttonsPanel = new JPanel();

        DefaultCaret caret = (DefaultCaret) resultTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        try {
            dAndDTextPane.setEditorKit(new MyEditorKit());
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
            StyledDocument doc = (StyledDocument) dAndDTextPane.getDocument();
            Style fontSize;
            fontSize = doc.addStyle("fontSize", null);
            StyleConstants.setFontSize(fontSize, 30);
            doc.insertString(0, "DRAG AND DROP YOUR FILES HERE", attrs);

            doc.setCharacterAttributes(0, doc.getLength(), fontSize, false);
            doc.setParagraphAttributes(0, doc.getLength() - 1, attrs, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        createAndShowGUI();
    }

    public void createAndShowGUI() {
        droppedFiles = new ArrayList<>();

        dAndDPanel.add(dAndDTextPane);
        dAndDPanel.setPreferredSize(new Dimension(400, 300));
        dAndDTextPane.setPreferredSize(new Dimension(400, 300));
        dAndDTextPane.setEditable(false);

        resetButton.setPreferredSize(new Dimension(150, 50));
        resetButton = new JButton("RESET");

        resultScrollPane.setViewportView(resultTextPane);
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultScrollPane.setPreferredSize(new Dimension(500, 500));
        resultScrollPane.setViewportView(resultTextPane);//replace the add

        buttonsPanel.setPreferredSize(new Dimension(590, 60));
        buttonsPanel.add(resetButton);

        resetButton.addActionListener(this);
        resetButton.setActionCommand("reset");
        resetButton.setPreferredSize(new Dimension(150, 50));

        contentPanel.add(dAndDPanel, BorderLayout.NORTH);
        contentPanel.add(buttonsPanel, BorderLayout.CENTER);
        contentPanel.add(resultScrollPane, BorderLayout.SOUTH);

        add(contentPanel);
        //https://stackoverflow.com/questions/34778965/how-to-remove-auto-focus-in-swing
        getContentPane().requestFocusInWindow(); //leave the default focus to the JFrame
        setTitle("Hash and VirusTotal Checker");
        setVisible(true);//making the frame visible
        setResizable(false);//not resizable, fixed
        setSize(600, 1000);
        setLocationRelativeTo(null);//center

        //Change JTextPane backGround on Nimbus style
        Color bgColor = Color.WHITE;
        UIDefaults defaults = new UIDefaults();
        defaults.put("TextPane.background", new ColorUIResource(bgColor));
        defaults.put("TextPane[Enabled].backgroundPainter", bgColor);
        dAndDTextPane.putClientProperty("Nimbus.Overrides", defaults);
        dAndDTextPane.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        dAndDTextPane.setBackground(bgColor);

        dAndDTextPane.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    boolean isHere = false;

                    for (File f1 : (java.util.List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)) {
                        String fileName1 = f1.getName();

                        for (File f2 : droppedFiles) {
                            String fileName2 = f2.getName();

                            if (fileName1.equals(fileName2)) {
                                isHere = true;
                            }
                        }
                        if (!isHere) {
                            droppedFiles.add(f1);
                        }

                    }
                    updateResultTextPane();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing (WindowEvent e){
                System.exit(0);
            }
        });
    }
    public void updateResultTextPane() throws IOException {
        String str = "";
        if (droppedFiles.isEmpty()) {
            resultTextPane.setText("");
        } else {
            for (File file : droppedFiles) {
                str += "<strong>File name</strong>: " + file.getName() + " <br/>" +
                        "<strong>Entropy</strong>: " + entropyCalcultator(file) +"<br/><br/>";
            }
            resultTextPane.setText(str);
        }
    }

    public double entropyCalcultator(File myFile) throws IOException {
        Map<Integer, Integer> occurrency_Arr = new HashMap<>();
        //recover every bytes from a file
        byte[] fileContent = Files.readAllBytes(myFile.toPath());

        //for each byte
        for(Byte b : fileContent) {
            //convert it's value to an int
            int value = b.intValue();

            //if my hashmap already have the byte as a key then
            if(occurrency_Arr.containsKey(value)) {
                //update the key's value
                occurrency_Arr.put(value, occurrency_Arr.get(value) + 1);
            }else {
                //or add the new occurrency to the hashmap
                occurrency_Arr.put(value, 1);
            }
        }
            /*
            for (Map.Entry<Integer, Integer> entry : occurrency_Arr.entrySet()) {
                int key = entry.getKey();
                int value = entry.getValue();
                System.out.println("Byte: "+key+" | Occurency: "+value);
            }
            */
        double lengthFile = fileContent.length;
        double entropy = 0;
        //for each key (unique byte)
        for (Map.Entry<Integer, Integer> entry : occurrency_Arr.entrySet()) {
            int value = entry.getValue();
            //do the probability by dividing the occurrency of a byte with the number of byte in the file
            double probability = value / lengthFile;
            //update the entropy variable with the formula
            entropy += probability * (Math.log(probability) / Math.log(2));
        }
        //entropy *= -1;
        //absolute value
        entropy = Math.abs(entropy);

        return entropy;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionName = e.getActionCommand();

        if (actionName.equals("reset")) {
            reset();
        }
    }

    private void reset() {
        droppedFiles.clear();
        resultTextPane.setText("");
    }
}