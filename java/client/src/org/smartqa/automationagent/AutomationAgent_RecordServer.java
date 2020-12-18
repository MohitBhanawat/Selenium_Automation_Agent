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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class AutomationAgent_RecordServer implements Runnable{
  final int PORT = 5397;
  static Socket connect;


  public AutomationAgent_RecordServer(Socket c){
    connect = c;
  }
  @Override
  public void run() {
    try {
      ServerSocket serverConnect = new ServerSocket(PORT);
      System.err.println("Server Started.\nListening to port: " + PORT + "\n");
      while (true) {
        AutomationAgent_RecordServer
            myServer =
            new AutomationAgent_RecordServer(serverConnect.accept());
        try {
          request_manipulation();
          Thread thread = new Thread(myServer);
          thread.start();
        } catch (Exception e) {
        }
      }
    } catch (IOException e) {
    }
  }

    protected void request_manipulation(){
      BufferedReader in = null;
      PrintWriter out = null;
      BufferedOutputStream dataOut = null;
      String request_string = null;
      AutomationAgent_GlobalVariables globalVariables = AutomationAgent_GlobalVariableDriver.automationAgent_globalVariables;
      try{
        in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        out = new PrintWriter(connect.getOutputStream());
        String input = in.readLine();
        StringTokenizer parse = new StringTokenizer(input);
        String method = parse.nextToken().toUpperCase();
        request_string = parse.nextToken();
        if(method.equals("GET")){
          String page_title;
          String request;
          if(request_string.startsWith("/")){
            request = request_string.substring(1);
          }else{
            request = request_string;
          }
//          System.err.println(request);
          if (request.contains("Page_title%%")) {
            page_title = request.split("Page_title%%")[1].replace("%20", "_").trim();
            page_title = page_title.replace("\\", "").replace("/", "").replace(":", "").replace("*", "")
                .replace("?", "").replace("\"", "").replace("<", "").replace(">", "").replace("|", "")
                .replace(".", "");
            File
                page_title_folder = new File(System.getProperty("user.dir") + "/Object Repository/" + page_title);
            page_title_folder.mkdirs();
            //Set Page Title in Global Variable
            globalVariables.setPage_title(page_title);
          }else{
            globalVariables.setString_from_extension(request);
          }

          out.println("HTTP/1.1 200 OK");
          out.println("Server: Java HTTP Server from SSaurel : 1.0");
          out.println("Date: " + new Date());
          out.println("Content-type: ");
          out.println("Content-length: 0");
          out.println(); // blank line between headers and content, very important !
          out.flush(); // flush character output stream buffer
          dataOut.flush();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }