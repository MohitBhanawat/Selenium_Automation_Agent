// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.smartqa.automationagent.remote;

import com.google.common.collect.ImmutableMap;

import org.smartqa.automationagent.AutomationAgent_GlobalVariableDriver;
import org.smartqa.automationagent.AutomationAgent_GlobalVariables;
import org.smartqa.automationagent.AutomationAgent_RecordObjectDriver;
import org.smartqa.automationagent.Beta;
import org.smartqa.automationagent.By;
import org.smartqa.automationagent.Dimension;
import org.smartqa.automationagent.NoSuchElementException;
import org.smartqa.automationagent.OutputType;
import org.smartqa.automationagent.Point;
import org.smartqa.automationagent.Rectangle;
import org.smartqa.automationagent.SearchContext;
import org.smartqa.automationagent.TakesScreenshot;
import org.smartqa.automationagent.WebDriver;
import org.smartqa.automationagent.WebDriverException;
import org.smartqa.automationagent.WebElement;
import org.smartqa.automationagent.WrapsDriver;
import org.smartqa.automationagent.WrapsElement;
import org.smartqa.automationagent.interactions.Coordinates;
import org.smartqa.automationagent.interactions.Locatable;
import org.smartqa.automationagent.io.Zip;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.midi.SysexMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class RemoteWebElement implements WebElement, WrapsDriver, TakesScreenshot, Locatable {

  private String foundBy;
  protected String id;
  protected RemoteWebDriver parent;
  protected FileDetector fileDetector;

  protected void setFoundBy(SearchContext foundFrom, String locator, String term) {
    this.foundBy = String.format("[%s] -> %s: %s", foundFrom, locator, term);
  }

  public void setParent(RemoteWebDriver parent) {
    this.parent = parent;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setFileDetector(FileDetector detector) {
    fileDetector = detector;
  }

  @Override
  public void click() {
    execute(DriverCommand.CLICK_ELEMENT(id));
    AutomationAgent_GlobalVariables automationAgent_globalVariables = AutomationAgent_GlobalVariableDriver.automationAgent_globalVariables;
    while (automationAgent_globalVariables.getString_from_extension() == null || automationAgent_globalVariables.getString_from_extension().equals(null)) {
      System.err.println("Waiting for attributes from extension");
    }
//    System.err.println("Click Method: " + automationAgent_globalVariables.getString_from_extension());
    AutomationAgent_RecordObjectDriver
        automationAgent_recordObjectDriver =
        new AutomationAgent_RecordObjectDriver();
    automationAgent_recordObjectDriver
        .ObjectDriver(automationAgent_globalVariables.getString_from_extension());
    if(automationAgent_globalVariables.isIn_smart_identifier()){
      update_learning_xml();
      update_page_factory();
      automationAgent_globalVariables.setIn_smart_identifier(false);
    }
  }

  protected void update_page_factory(){
    AutomationAgent_GlobalVariables automationAgent_globalVariables = AutomationAgent_GlobalVariableDriver.automationAgent_globalVariables;
    String page_factory_file = automationAgent_globalVariables.getPage_title() + ".java";
    String user_defined_xpath = automationAgent_globalVariables.getLocator_as_is().trim();
    String valid_xpath = automationAgent_globalVariables.getValid_xpath().trim();
    //Get all java files
    String Project_Repo_Path = System.getProperty("user.dir");
    List<String> files;
    try (Stream<Path> paths = Files.walk(Paths.get(Project_Repo_Path))) {
      files = paths.map(path -> path.toString()).filter(f -> f.endsWith(page_factory_file))
          .collect(Collectors.toList());
      if(files.size() == 1){
        String file_path = files.get(0);
        File file = new File(file_path); // creates a new file instance
        FileReader fr = new FileReader(file); // reads the file
        BufferedReader br = new BufferedReader(fr); // creates a buffering character input stream
        StringBuffer sb = new StringBuffer(); // constructs a string buffer with no characters
        String line;
        while ((line = br.readLine()) != null) {
          if (line.contains(user_defined_xpath)) {
            line = line.replace(user_defined_xpath, valid_xpath.replace("\"", "'"));
          }
          sb.append(line); // appends line to string buffer
          sb.append("\n"); // line feed
        }
        fr.close(); // closes the stream and release the resources
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(sb.toString());
        myWriter.close();
        System.out.println("Page Factory Updated Successfully.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


  }
  protected void update_learning_xml(){
    String or_path = System.getProperty("user.dir") + "/Object Repository/";
    String learning_xml_name = "Learning_Index.xml";
    String learning_xml_path = or_path + "Master_Data/";
    AutomationAgent_GlobalVariables automationAgent_globalVariables = AutomationAgent_GlobalVariableDriver.automationAgent_globalVariables;
    boolean is_combination_present = false;
    String combination = automationAgent_globalVariables.getCombination();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new File(learning_xml_path + learning_xml_name));
      document.getDocumentElement().normalize();
      // Here comes the root node
      Element root = document.getDocumentElement();
      NodeList nList = document.getElementsByTagName("Combination");
      Element eElement = null;
      for (int temp = 0; temp < nList.getLength(); temp++) {
        Node node = nList.item(temp);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          eElement = (Element) node;
          if (eElement.getElementsByTagName("name").item(0).getTextContent().trim().equalsIgnoreCase(combination.trim())) {
            is_combination_present = true;
            break;
          }
        }
      }
      if (is_combination_present) {
        int score = Integer.valueOf(eElement.getElementsByTagName("priority_score").item(0).getTextContent().trim());
        eElement.getElementsByTagName("priority_score").item(0).setTextContent(String.valueOf(score+1));

      } else {
        Element combination_node = document.createElement("Combination");
        Element name = document.createElement("name");
        name.appendChild(document.createTextNode(combination));
        combination_node.appendChild(name);
        Element priority_score_node = document.createElement("priority_score");
        priority_score_node.appendChild(document.createTextNode("1"));
        combination_node.appendChild(priority_score_node);
        root.appendChild(combination_node);
      }

      DOMSource source = new DOMSource(document);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      StreamResult result = new StreamResult(learning_xml_path + learning_xml_name);
      transformer.transform(source, result);
    }catch (Exception e){

    }
  }

  @Override
  public void submit() {
    execute(DriverCommand.SUBMIT_ELEMENT(id));
  }

  @Override
  public void sendKeys(CharSequence... keysToSend) {
    if (keysToSend == null || keysToSend.length == 0) {
      throw new IllegalArgumentException("Keys to send should be a not null CharSequence");
    }
    for (CharSequence cs : keysToSend) {
      if (cs == null) {
        throw new IllegalArgumentException("Keys to send should be a not null CharSequence");
      }
    }

    String allKeysToSend = String.join("", keysToSend);

    List<File> files = Arrays.stream(allKeysToSend.split("\n"))
                             .map(fileDetector::getLocalFile)
                             .collect(Collectors.toList());
    if (!files.isEmpty() && !files.contains(null)) {
      allKeysToSend = files.stream()
                           .map(this::upload)
                           .collect(Collectors.joining("\n"));
    }

    execute(DriverCommand.SEND_KEYS_TO_ELEMENT(id, new CharSequence[]{allKeysToSend}));
    AutomationAgent_GlobalVariables automationAgent_globalVariables = AutomationAgent_GlobalVariableDriver.automationAgent_globalVariables;
    while (automationAgent_globalVariables.getString_from_extension() == null || automationAgent_globalVariables.getString_from_extension().equals(null)) {
      System.err.println("Waiting for attributes from extension");
    }
//    System.err.println("Sendkeys Method: " + automationAgent_globalVariables.getString_from_extension());
    AutomationAgent_RecordObjectDriver
        automationAgent_recordObjectDriver =
        new AutomationAgent_RecordObjectDriver();
    automationAgent_recordObjectDriver
        .ObjectDriver(automationAgent_globalVariables.getString_from_extension());
    if(automationAgent_globalVariables.isIn_smart_identifier()){
      update_learning_xml();
      update_page_factory();
      automationAgent_globalVariables.setIn_smart_identifier(false);
    }
  }

  private String upload(File localFile) {
    if (!localFile.isFile()) {
      throw new WebDriverException("You may only upload files: " + localFile);
    }

    try {
      String zip = Zip.zip(localFile);
      Response response = execute(DriverCommand.UPLOAD_FILE(zip));
      return (String) response.getValue();
    } catch (IOException e) {
      throw new WebDriverException("Cannot upload " + localFile, e);
    }
  }

  @Override
  public void clear() {
    execute(DriverCommand.CLEAR_ELEMENT(id));
  }

  @Override
  public String getTagName() {
    return (String) execute(DriverCommand.GET_ELEMENT_TAG_NAME(id))
      .getValue();
  }

  @Override
  public String getAttribute(String name) {
    return stringValueOf(
      execute(DriverCommand.GET_ELEMENT_ATTRIBUTE(id, name))
        .getValue());
  }

  private static String stringValueOf(Object o) {
    if (o == null) {
      return null;
    }
    return String.valueOf(o);
  }

  @Override
  public boolean isSelected() {
    Object value = execute(DriverCommand.IS_ELEMENT_SELECTED(id))
      .getValue();
    try {
      return (Boolean) value;
    } catch (ClassCastException ex) {
      throw new WebDriverException("Returned value cannot be converted to Boolean: " + value, ex);
    }
  }

  @Override
  public boolean isEnabled() {
    Object value = execute(DriverCommand.IS_ELEMENT_ENABLED(id))
      .getValue();
    try {
      return (Boolean) value;
    } catch (ClassCastException ex) {
      throw new WebDriverException("Returned value cannot be converted to Boolean: " + value, ex);
    }
  }

  @Override
  public String getText() {
    Response response = execute(DriverCommand.GET_ELEMENT_TEXT(id));
    return (String) response.getValue();
  }

  @Override
  public String getCssValue(String propertyName) {
    Response response = execute(DriverCommand.GET_ELEMENT_VALUE_OF_CSS_PROPERTY(id, propertyName));
    return (String) response.getValue();
  }

  @Override
  public List<WebElement> findElements(By locator) {
    if (locator instanceof By.StandardLocator) {
      return ((By.StandardLocator) locator).findElements(this, this::findElements);
    } else {
      return locator.findElements(this);
    }
  }

  @Override
  public WebElement findElement(By by) {
    System.err.println("Remote Web Element-FindElement: By by");
    if (by instanceof By.StandardLocator) {
      return ((By.StandardLocator) by).findElement(this, this::findElement);
    } else {
      return by.findElement(this);
    }
  }

  protected WebElement findElement(String using, String value) {
    System.err.println("Remote Web Element-FindElement: String using, String value");
    Response response = execute(DriverCommand.FIND_CHILD_ELEMENT(id, using, value));

    Object responseValue = response.getValue();
    if (responseValue == null) { // see https://github.com/SeleniumHQ/automationagent/issues/5809
      throw new NoSuchElementException(String.format("Cannot locate an element using %s=%s", using, value));
    }
    WebElement element;
    try {
      element = (WebElement) responseValue;
    } catch (ClassCastException ex) {
      throw new WebDriverException("Returned value cannot be converted to WebElement: " + value, ex);
    }
    parent.setFoundBy(this, element, using, value);
    return element;
  }

  @SuppressWarnings("unchecked")
  protected List<WebElement> findElements(String using, String value) {
    Response response = execute(DriverCommand.FIND_CHILD_ELEMENTS(id, using, value));
    Object responseValue = response.getValue();
    if (responseValue == null) { // see https://github.com/SeleniumHQ/automationagent/issues/4555
      return Collections.emptyList();
    }
    List<WebElement> allElements;
    try {
      allElements = (List<WebElement>) responseValue;
    } catch (ClassCastException ex) {
      throw new WebDriverException("Returned value cannot be converted to List<WebElement>: " + responseValue, ex);
    }
    allElements.forEach(element -> parent.setFoundBy(this, element, using, value));
    return allElements;
  }

  protected Response execute(CommandPayload payload) {
    return parent.execute(payload);
  }

  protected Response execute(String command, Map<String, ?> parameters) {
    return parent.execute(command, parameters);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof WebElement)) {
      return false;
    }

    WebElement other = (WebElement) obj;
    while (other instanceof WrapsElement) {
      other = ((WrapsElement) other).getWrappedElement();
    }

    if (!(other instanceof RemoteWebElement)) {
      return false;
    }

    RemoteWebElement otherRemoteWebElement = (RemoteWebElement) other;

    return id.equals(otherRemoteWebElement.id);
  }

  /**
   * @return This element's hash code, which is a hash of its internal opaque ID.
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.smartqa.automationagent.internal.WrapsDriver#getWrappedDriver()
   */
  @Override
  public WebDriver getWrappedDriver() {
    return parent;
  }

  @Override
  public boolean isDisplayed() {
    Object value = execute(DriverCommand.IS_ELEMENT_DISPLAYED(id))
      .getValue();
    try {
      return (Boolean) value;
    } catch (ClassCastException ex) {
      throw new WebDriverException("Returned value cannot be converted to Boolean: " + value, ex);
    }
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Point getLocation() {
    Response response = execute(DriverCommand.GET_ELEMENT_LOCATION(id));
    Map<String, Object> rawPoint = (Map<String, Object>) response.getValue();
    int x = ((Number) rawPoint.get("x")).intValue();
    int y = ((Number) rawPoint.get("y")).intValue();
    return new Point(x, y);
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Dimension getSize() {
    Response response = execute(DriverCommand.GET_ELEMENT_SIZE(id));
    Map<String, Object> rawSize = (Map<String, Object>) response.getValue();
    int width = ((Number) rawSize.get("width")).intValue();
    int height = ((Number) rawSize.get("height")).intValue();
    return new Dimension(width, height);
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public Rectangle getRect() {
    Response response = execute(DriverCommand.GET_ELEMENT_RECT(id));
    Map<String, Object> rawRect = (Map<String, Object>) response.getValue();
    int x = ((Number) rawRect.get("x")).intValue();
    int y = ((Number) rawRect.get("y")).intValue();
    int width = ((Number) rawRect.get("width")).intValue();
    int height = ((Number) rawRect.get("height")).intValue();
    return new Rectangle(x, y, height, width);
  }

  @Override
  public Coordinates getCoordinates() {
    return new Coordinates() {

      @Override
      public Point onScreen() {
        throw new UnsupportedOperationException("Not supported yet.");
      }

      @Override
      public Point inViewPort() {
        Response response = execute(DriverCommand.GET_ELEMENT_LOCATION_ONCE_SCROLLED_INTO_VIEW(getId()));

        @SuppressWarnings("unchecked")
        Map<String, Number> mapped = (Map<String, Number>) response.getValue();
        return new Point(mapped.get("x")
                               .intValue(), mapped.get("y")
                                                  .intValue());
      }

      @Override
      public Point onPage() {
        return getLocation();
      }

      @Override
      public Object getAuxiliary() {
        return getId();
      }
    };
  }

  @Override
  @Beta
  public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
    Response response = execute(DriverCommand.ELEMENT_SCREENSHOT(id));
    Object result = response.getValue();
    if (result instanceof String) {
      String base64EncodedPng = (String) result;
      return outputType.convertFromBase64Png(base64EncodedPng);
    } else if (result instanceof byte[]) {
      String base64EncodedPng = new String((byte[]) result);
      return outputType.convertFromBase64Png(base64EncodedPng);
    } else {
      throw new RuntimeException(String.format("Unexpected result for %s command: %s",
        DriverCommand.ELEMENT_SCREENSHOT,
        result == null ? "null" : result.getClass()
                                        .getName() + " instance"));
    }
  }

  public String toString() {
    if (foundBy == null) {
      return String.format("[%s -> unknown locator]", super.toString());
    }
    return String.format("[%s]", foundBy);
  }

  public Map<String, Object> toJson() {
    return ImmutableMap.of(
      Dialect.OSS.getEncodedElementKey(), getId(),
      Dialect.W3C.getEncodedElementKey(), getId());
  }
}
