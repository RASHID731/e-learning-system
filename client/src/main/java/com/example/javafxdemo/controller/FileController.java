package com.example.javafxdemo.controller;

import com.example.javafxdemo.GUIApplication;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.DrawableComponent;
import com.example.javafxdemo.usecaseeditor.drawablecomponent.connectiontype.ConnectionType;
import javafx.scene.control.Alert;
import org.javatuples.Pair;
import org.reflections.ReflectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the saving and loading of diagram components
 */
public class FileController {
    private final CanvasContentManagementController canvasContentManagementController;

    /**
     * Constructor for the FileController class.
     *
     * @param canvasContentManagementController The controller for managing the canvas content.
     */
    public FileController(CanvasContentManagementController canvasContentManagementController){
        this.canvasContentManagementController = canvasContentManagementController;
    }

    /**
     * Converts the list of drawable components to an XML string.
     * A POST Request is made and XML String is saved in the loesung property of Gruppe
     *
     * @param drawableComponents The list of drawable components to save.
     * @return The XML string representing the saved components.
     */
    public String saveDrawnComponents(ArrayList<DrawableComponent> drawableComponents) {
        try {
            // Create a StringBuilder to construct the XML string
            StringBuilder xmlBuilder = new StringBuilder();

            // Append XML declaration and root element
            xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
            xmlBuilder.append("<canvas>");

            // Add element for each drawn component
            for (DrawableComponent component : drawableComponents) {
                String componentXml = "<" + component.getClass().getName() + ">";

                // Invoke all getters of each component to populate component tag
                Set<Method> getters = ReflectionUtils.getAllMethods(component.getClass(),
                        ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("get"),
                        ReflectionUtils.withParametersCount(0));

                for (Method getter : getters) {
                    if (!getter.getName().equals("getClass")) {
                        Object fieldValue = getter.invoke(component);
                        if (fieldValue != null) {
                            String fieldName = getter.getName().substring(3);
                            if (fieldValue instanceof Pair) {
                                Pair<Double, Double> pair = (Pair<Double, Double>) fieldValue;
                                String pairValueXml = "<" + fieldName + ">" + pair.getValue0() + ", " + pair.getValue1() + "</" + fieldName + ">";
                                componentXml += pairValueXml;
                            } else {
                                String fieldValueXml = "<" + fieldName + ">" + fieldValue.toString() + "</" + fieldName + ">";
                                componentXml += fieldValueXml;
                            }
                        }
                    }
                }

                componentXml += "</" + component.getClass().getName() + ">";
                xmlBuilder.append(componentXml);
            }

            // Close the root element
            xmlBuilder.append("</canvas>");
            String xmlString = xmlBuilder.toString();
            System.out.println(xmlString);

            // Set up the connection
            URL url = new URL("http://" + GUIApplication.URL + ":8080/gruppe/" + GUIApplication.gruppeId + "/save-drawn-components"); // Replace with your actual API endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setDoOutput(true);

            // Send the request body
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = xmlString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            // Read the response from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.lines().collect(Collectors.joining("\n"));
            System.out.println("Response: " + response);

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                // Handle the successful response
                System.out.println("Components saved to the database successfully.");
            } else {
                // Handle the error case
                System.out.println("Failed to save components to the database. Response code: " + responseCode);
            }

            connection.disconnect();

            // Return the final XML string
            return xmlString;
        } catch (Exception e) {
//            showErrorAlert("Save failed:", e.getMessage());
            return null;
        }
    }

    /**
     * Sends GET Request to get the XML String from database
     * Converts the XML String
     *
     * @return The list of loaded drawable components.
     */
    public ArrayList<DrawableComponent> loadDrawnComponents() {
        ArrayList<DrawableComponent> drawableComponents = new ArrayList<>();
        try {
            // Set up the connection
            URL url = new URL("http://" + GUIApplication.URL + ":8080/gruppe/" + GUIApplication.gruppeId + "/get-drawn-components");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the XML response from the input stream
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(connection.getInputStream());

            // Get the root element
            Element rootElement = document.getDocumentElement();

            // Get a list of all the component tags
            NodeList componentTags = rootElement.getChildNodes();
            for (int i = 0; i < componentTags.getLength(); i++) {
                Node componentTag = componentTags.item(i);
                if (componentTag.getNodeType() == Node.ELEMENT_NODE) {
                    // Create a new instance of the class associated with the tag using its no-args constructor
                    Class<?> clazz = Class.forName(componentTag.getNodeName());
                    DrawableComponent drawableComponent = (DrawableComponent) clazz.getConstructor().newInstance();

                    // Get all the setters for the class associated with the tag
                    Set<Method> setters = ReflectionUtils.getAllMethods(clazz,
                            ReflectionUtils.withModifier(Modifier.PUBLIC), ReflectionUtils.withPrefix("set"),
                            ReflectionUtils.withParametersCount(1));

                    // Get all the sub tags for each field
                    NodeList fieldTags = componentTag.getChildNodes();
                    for (Method setter : setters) {
                        String setterName = setter.getName();
                        if (setterName.startsWith("set")) {
                            String fieldName = setterName.substring(3);
                            for (int j = 0; j < fieldTags.getLength(); j++) {
                                Node fieldTag = fieldTags.item(j);
                                if (fieldTag.getNodeName().equals(fieldName)) {
                                    String fieldValue = fieldTag.getTextContent();
                                    Class<?> parameterType = setter.getParameterTypes()[0];
                                    if (parameterType.equals(double.class)) {
                                        setter.invoke(drawableComponent, Double.parseDouble(fieldValue));
                                    } else if (parameterType.equals(String.class)) {
                                        setter.invoke(drawableComponent, fieldValue);
                                    } else if (parameterType.equals(Pair.class)) {
                                        String fieldValueWithoutBrackets = fieldValue.replace("[", "").replace("]", "");
                                        String[] pairAsTextList = fieldValueWithoutBrackets.split(", ");
                                        double firstValue = Double.parseDouble(pairAsTextList[0]);
                                        double secondValue = Double.parseDouble(pairAsTextList[1]);
                                        Pair<Double, Double> pair = Pair.with(firstValue, secondValue);
                                        setter.invoke(drawableComponent, pair);
                                    } else if (parameterType.equals(ConnectionType.class)) {
                                        ConnectionType connectionType = ConnectionType.valueOf(fieldValue);
                                        setter.invoke(drawableComponent, connectionType);
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    // Add component to the list
                    drawableComponents.add(drawableComponent);
                }
            }
        } catch (Exception e) {
//            e.printStackTrace();
//            showErrorAlert("Load failed:", e.getMessage());
            System.out.println("error");
//            return null;
        }
        return drawableComponents;
    }

    /**
     * displays an alert box with the passed message
     *
     * @param message the message to display on the alert box
     * @param error details to add to the message
     */
    private void showErrorAlert(String message, String error){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        System.out.println(message);
        alert.setContentText(error);
        System.out.println(error);
        alert.showAndWait();
    }
}
