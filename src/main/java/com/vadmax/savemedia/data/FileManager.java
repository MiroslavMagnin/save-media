package com.vadmax.savemedia.data;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    private final String filePath;
    private File file;
    private Document document;

    public FileManager(String fileName) {
        String userHome = System.getProperty("user.home");
        this.filePath = userHome + File.separator + "AppData" + File.separator + "Local" + File.separator +
                "SaveMedia" + File.separator + fileName + ".xml";

        this.file = new File(this.filePath);
        loadFields();
    }

    private void loadFields() {
        try {
            SAXBuilder builder = new SAXBuilder();
            document = builder.build(file);
        } catch (IOException e) {
            // The file hasn't been found or error reading the file
            // So create a new XML-file with the root element "fields"
            try {
                if (!file.exists()) {
                    File parentDir = file.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs(); // Create a new direction, if it doesn't exist
                    }
                    file.createNewFile(); // Create a new XML-file, if it doesn't exist
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            document = new Document(new Element("fields"));

            // Default fields
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFields() {
        XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
        try {
            FileWriter fileWriter = new FileWriter(this.filePath);
            xmlOutput.output(document, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setField(String key, String value) {
        Element root = document.getRootElement();
        Element settingElement = root.getChild(key);

        if(settingElement != null) {
            settingElement.setText(value);
        } else {
            settingElement = new Element(key);
            settingElement.setText(value);
            root.addContent(settingElement);
        }

        saveFields();
    }

    private String getFieldValue(Element element, String key) {
        if(element.getName().equals(key)) {
            return element.getText();
        }

        for(Element child : element.getChildren()) {
            String value = getFieldValue(child, key);
            if(value != null) {
                return value;
            }
        }

        return null;
    }

    public String getField(String key) {
        Element root = document.getRootElement();
        return getFieldValue(root, key);
    }
}