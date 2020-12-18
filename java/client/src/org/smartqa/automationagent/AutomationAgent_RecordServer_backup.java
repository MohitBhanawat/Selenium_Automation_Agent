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

import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collection;

public class AutomationAgent_RecordServer_backup {
  static final int PORT = 5397;
  public static Collection<File> ObjectRepo;
  public static File script_file;
  public static String script_file_path;
  public static String old_object_path;

  public void startserver(){
    try{
      HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
      System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

      server.createContext("/", httpExchange -> {
        byte response[] = "Hello, World!".getBytes("UTF-8");
        System.err.println("test");
        System.err.println(httpExchange.getRequestURI().toString());
        request_manipulation(httpExchange.getRequestURI().toString());
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        httpExchange.sendResponseHeaders(200, 1);

        OutputStream out = httpExchange.getResponseBody();
        out.write(response);
        out.close();
      });
      server.start();
    }catch(Exception e){
      System.err.println("Server Connection error : " + e.getMessage());
    }
  }

  protected void request_manipulation(String request_string){
    System.err.println("request_manipulation");
    AutomationAgent_GlobalVariables globalVariables = new AutomationAgent_GlobalVariables();
    String page_title;
    String request;
    if(request_string.startsWith("/")){
      request = request_string.substring(1);
    }else{
      request = request_string;
    }

    if (request.contains("Page_title%%")) {
      page_title = request.split("Page_title%%")[1].replace("%20", "_").trim();
      page_title = page_title.replace("\\", "").replace("/", "").replace(":", "").replace("*", "")
          .replace("?", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "")
          .replace(".", "");
      File page_title_folder = new File(System.getProperty("user.dir") + "/Object Repository/" + page_title);
      page_title_folder.mkdirs();
      globalVariables.setPage_title(page_title);
    }
  }
}