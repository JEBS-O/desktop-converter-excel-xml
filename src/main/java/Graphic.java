import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Graphic {
    private final static JFrame jFrame = new JFrame();
    private final static JPanel jPanel = new JPanel();
    private static JButton setExcelFileButton = new JButton("Выбрать файл");
    private final static JButton setXmlFileButton = new JButton("Выбрать файл");
    private final static JButton convertButton = new JButton("Конвертировать");
    private final static JFileChooser excelFileOpen = new JFileChooser();
    private final static JFileChooser xmlFileOpen = new JFileChooser();
    private static File xmlFile = null;
    private static File excelFile = null;


    private static JLabel label1 = new JLabel("Выберите excel прайс");
    private static JLabel label2 = new JLabel("Выберите пустой xml файл для записи");
    private static JLabel errorLabel = new JLabel("");

    static {

        GridBagLayout layout = new GridBagLayout();
        jPanel.setLayout(layout);

        GridBagConstraints constraintsForExcel1 = new GridBagConstraints();
        constraintsForExcel1.insets = new Insets(10,10,10,10);
        constraintsForExcel1.weightx = 0;
        constraintsForExcel1.weighty = 0;
        constraintsForExcel1.gridx = 0;
        constraintsForExcel1.gridy = 0;
        constraintsForExcel1.gridwidth = 1;
        constraintsForExcel1.gridheight = 1;
        jPanel.add(label1, constraintsForExcel1);

        GridBagConstraints constraintsForExcel2 = new GridBagConstraints();
        constraintsForExcel2.insets = new Insets(10,10,10,10);
        constraintsForExcel2.weightx = 0;
        constraintsForExcel2.weighty = 0;
        constraintsForExcel2.gridx = 1;
        constraintsForExcel2.gridy = 0;
        constraintsForExcel2.gridwidth = 1;
        constraintsForExcel2.gridheight = 1;
        jPanel.add(setExcelFileButton, constraintsForExcel2);

        GridBagConstraints constraintsForXml1 = new GridBagConstraints();
        constraintsForXml1.insets = new Insets(10,10,10,10);
        constraintsForXml1.weightx = 0;
        constraintsForXml1.weighty = 0;
        constraintsForXml1.gridx = 0;
        constraintsForXml1.gridy = 1;
        constraintsForXml1.gridwidth = 1;
        constraintsForXml1.gridheight = 1;
        jPanel.add(label2, constraintsForXml1);

        GridBagConstraints constraintsForXml2 = new GridBagConstraints();
        constraintsForXml2.insets = new Insets(10,10,10,10);
        constraintsForXml2.weightx = 0;
        constraintsForXml2.weighty = 0;
        constraintsForXml2.gridx = 1;
        constraintsForXml2.gridy = 1;
        constraintsForXml2.gridwidth = 1;
        constraintsForXml2.gridheight = 1;
        jPanel.add(setXmlFileButton, constraintsForXml2);

        GridBagConstraints constraintsForConvert = new GridBagConstraints();
        constraintsForConvert.insets = new Insets(10,10,10,10);
        constraintsForConvert.weightx = 0;
        constraintsForConvert.weighty = 0;
        constraintsForConvert.gridx = 0;
        constraintsForConvert.gridy = 2;
        constraintsForConvert.gridwidth = 2;
        constraintsForConvert.gridheight = 1;
        jPanel.add(convertButton, constraintsForConvert);

        GridBagConstraints constraintsForErrorLabel = new GridBagConstraints();
        constraintsForConvert.insets = new Insets(10,10,10,10);
        constraintsForConvert.weightx = 0;
        constraintsForConvert.weighty = 0;
        constraintsForConvert.gridx = 0;
        constraintsForConvert.gridy = 3;
        constraintsForConvert.gridwidth = 2;
        constraintsForConvert.gridheight = 1;
        jPanel.add(errorLabel, constraintsForConvert);

        setExcelFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int ret = excelFileOpen.showDialog(null, "Открыть файл");
                Logs.print("0");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = excelFileOpen.getSelectedFile();
                    if(checkFileFormat(file, "xlsx")) {
                        excelFile = excelFileOpen.getSelectedFile();
                        label1.setText("excel таблица: " + excelFile.toString());
                        setExcelFileButton.setText("Поменять таблицу");
                    } else {
                        errorLabel.setText("ОШИБКА ВВОДА: Выберите файл формата .xlsx");
                    }
                }
            }
        });

        setXmlFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = xmlFileOpen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = xmlFileOpen.getSelectedFile();
                    if(checkFileFormat(file, "xml")) {
                        xmlFile = xmlFileOpen.getSelectedFile();
                        label2.setText("xml файл: " + xmlFile.toString());
                        setXmlFileButton.setText("Поменять файл");
                    } else {
                        errorLabel.setText("ОШИБКА ВВОДА: Выберите файл формата .xml");
                    }
                }
            }
        });

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Converter.checkTrial()) {
                    Converter converter = new Converter();
                    XmlCreator creator = new XmlCreator(excelFile);
                    String xml = creator.createXml();
                    converter.write(xmlFile, xml);
                    Converter.incrementLaunch();
                } else {
                    errorLabel.setText("Trial период закончился. Вы уже совершили 5 запусков программы");
                }
            }
        });
    }

    public static void openApplication() {
        jFrame.add(jPanel);
        setWindow(jFrame);
    }

    private static void setWindow(JFrame jFrame) {
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(700, 500);
        jFrame.setLocation(200,150);
        jFrame.setTitle("Excel to xml converter 1.2 - TRIAL");
    }

    public static void setErrorLabel(String text) {
        String errors = errorLabel.getText();
        errorLabel.setText(errors + text);
    }

    public static void cleanErrorLabel() {
        errorLabel.setText("");
    }

    private static boolean checkFileFormat(File file, String format) {
        String fileName = file.getName();
        int indexOf = fileName.lastIndexOf(".");
        return fileName.substring(indexOf+1).equals(format);
    }
}
