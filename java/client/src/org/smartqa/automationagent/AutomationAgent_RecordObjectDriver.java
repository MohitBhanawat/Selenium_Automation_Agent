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

package org.smartqa.automationagent;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class AutomationAgent_RecordObjectDriver {

  protected String get_object_name(Map<String, String> object_properties) {
    int count = 1;
    AutomationAgent_GlobalVariables automationAgent_globalVariables = AutomationAgent_GlobalVariableDriver.automationAgent_globalVariables;
    String folder_path = System.getProperty("user.dir") + "/Object Repository/" + automationAgent_globalVariables.getPage_title() + "/";
    String file_name;
    if(automationAgent_globalVariables.isIn_smart_identifier()){
      file_name = automationAgent_globalVariables.getPage_title()+ "_" + automationAgent_globalVariables
          .getSmartidentifier_locator_string();
    }else {
      file_name =
          automationAgent_globalVariables.getPage_title() + "_" + automationAgent_globalVariables
              .getLocator_string();
    }

    if(file_name.length() > 200){
      file_name = file_name.substring(0,200);
    }

    while(new File(folder_path + file_name + ".xml").exists()){
      if(compare_properties(object_properties, read_object_xml(new File(folder_path + file_name + ".xml")))){
        break;
      }else{
        file_name = automationAgent_globalVariables.getPage_title()+ "_" + automationAgent_globalVariables
            .getLocator_string() + "_" + count;
        count++;
      }
    }
//    if(new File(folder_path + file_name + ".xml").exists()){
//      if(!compare_properties(object_properties, read_object_xml(new File(folder_path + file_name + ".xml")))){
//        while(new File(folder_path + file_name + ".xml").exists()){
//          file_name = automationAgent_globalVariables.getPage_title()+ "_" + automationAgent_globalVariables
//              .getLocator_string() + "_" + count;
//          count++;
//        }
//      }
//    }
    return file_name;
  }

  public void ObjectDriver(String extension_string) {
    if (extension_string.toLowerCase().contains("attributes%%")) {
      Map<String, String> object_properties = new HashMap<String, String>();
      List<String> attr_list = new ArrayList<String>();
      extension_string = extension_string.replace("%20", " ").replace("/attributes%%,", "").trim();
      if (extension_string.endsWith(",")) {
        extension_string = extension_string.substring(0, extension_string.length() - 1);
      }
      attr_list = Arrays.asList(extension_string.split(", "));
      for (String attr : attr_list) {
        String[] key_val = attr.split("%% ");
        if (key_val.length > 1 && !key_val[1].equalsIgnoreCase("undefined")) {
          object_properties.put(key_val[0], key_val[1]);
        }
      }
      create_object(object_properties);
    }
  }

  protected void create_object(Map<String, String> object_properties){
    AutomationAgent_GlobalVariables automationAgent_globalVariables = AutomationAgent_GlobalVariableDriver.automationAgent_globalVariables;
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.newDocument();
      // root element
      Element rootElement = doc.createElement("WebElementEntity");
      doc.appendChild(rootElement);

      for (String key_value : object_properties.keySet()) {
        String val = object_properties.get(key_value).trim();
        if (val != null && val.equalsIgnoreCase("") == false && !val.equalsIgnoreCase("undefined")
            && !val.equalsIgnoreCase("null")) {
          Element webElementProperties = doc.createElement("webElementProperties");
          rootElement.appendChild(webElementProperties);

          Attr attr_name = doc.createAttribute("name");
          attr_name.setValue(key_value.trim());
          webElementProperties.setAttributeNode(attr_name);

          Attr attr_value = doc.createAttribute("value");
          attr_value.setValue(object_properties.get(key_value).trim());
          webElementProperties.setAttributeNode(attr_value);
        }
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      DOMSource source = new DOMSource(doc);
      String file_name = get_object_name(object_properties);

      if(!new File(System.getProperty("user.dir") + "/Object Repository/"
                   + automationAgent_globalVariables.getPage_title() + "/" + file_name + ".xml").exists()) {

        StreamResult
            result =
            new StreamResult(new File(System.getProperty("user.dir") + "/Object Repository/"
                                      + automationAgent_globalVariables.getPage_title() + "/"
                                      + file_name + ".xml"));
        transformer.transform(source, result);
      }
      // Output to console for testing
//      StreamResult consoleResult = new StreamResult(System.out);
//      transformer.transform(source, consoleResult);

      automationAgent_globalVariables.setString_from_extension(null);
    } catch (Exception e) {
      System.err.println("Create Object Exception:");
      e.printStackTrace();
    }
  }

  protected Map<String, String> read_object_xml(File object_file){
    Map<String, String> object_file_properties = new HashMap<>();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(object_file);
      document.getDocumentElement().normalize();
      //Get Root Node
      Element root = document.getDocumentElement();
      NodeList nList = document.getElementsByTagName("webElementProperties");
      for (int temp = 0; temp < nList.getLength(); temp++){
        Node node = nList.item(temp);
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          Element eElement = (Element) node;
          object_file_properties.put(eElement.getAttribute("name").trim(), eElement.getAttribute("value").trim());
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    return object_file_properties;
  }

  protected boolean compare_properties(Map<String, String> recorded_properties, Map<String, String> xml_properties){
    boolean isMatch = false;
    if(recorded_properties.size() == xml_properties.size()){
      for(String key : recorded_properties.keySet()){
        if(xml_properties.get(key).equals(recorded_properties.get(key))){
          isMatch = true;
        }else{
          isMatch = false;
          break;
        }
      }
    }
    return isMatch;
  }

}
