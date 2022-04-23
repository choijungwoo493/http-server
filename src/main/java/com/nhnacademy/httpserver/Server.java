package com.nhnacademy.httpserver;

import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.nhnacademy.httpserver.PropertySetter.ArgsPropertySetter;
import com.nhnacademy.httpserver.PropertySetter.ContentPropretySetter;
import com.nhnacademy.httpserver.PropertySetter.DataPropertySetter;
import com.nhnacademy.httpserver.PropertySetter.JsonPropertySetter;
import com.nhnacademy.httpserver.vo.PostMultipartVo;

import java.awt.desktop.OpenURIEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class Server {
    public static void main(String[] args) {
        // Server: 192.168.71.79
        // Client: 192.168.71.40
        // http://192.168.71.79/get
        // backlog- 들어오는 연결 대기열의 요청된 최대 길이입니다.
        try (ServerSocket serverSocket = new ServerSocket(80)) {
            Socket socket = serverSocket.accept();

            byte[] bytes = new byte[2048];
            int numberOfBytes = socket.getInputStream().read(bytes);
            // 요청 헤더
            String request = new String(bytes, 0, numberOfBytes, UTF_8);

            ObjectMapper mapper = new ObjectMapper();

            // 응답 본문
            ObjectNode payload = mapper.createObjectNode();

            String origin = socket.getInetAddress().getHostAddress();
            payload.put("origin", origin);

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            json += System.lineSeparator();

            StringBuilder responseHeader = new StringBuilder();
            StringBuilder responseBody = new StringBuilder();

            String query = getQuery(request);
            String argsProperty = "";
            String dataProperty = "";
            String jsonProperty = "";
            String contentType = "";

            //todo: 쿼리가 get이고 인자가 하나 이상일땐 args 프로퍼티가 세팅 되야함.
            if (query.startsWith("/get?")) {
                ArgsPropertySetter argsPropertySetter = new ArgsPropertySetter();
                argsPropertySetter.setProperty(query);
                argsProperty = argsPropertySetter.getArgsProperty();
            }

            //todo: 쿼리에 ip,post 가 들어가면  Content-Type 프로퍼티가 세팅 되야함.
            if (!query.startsWith("/get")) {
                ContentPropretySetter contentPropretySetter = new ContentPropretySetter();
                contentPropretySetter.setProperty(request);
                contentType = contentPropretySetter.getContentProperty();
            }

            //todo: application/json 형태의 데이터를 post하면 data와 json 프로퍼티가 세팅되야함
            //todo: {주의} 파일 입출력 때는 고려안함
            if (query.equals("/post") && contentType.equals("application/json")) {

                DataPropertySetter dataPropertySetter = new DataPropertySetter();
                dataPropertySetter.setProperty(request);
                dataProperty = dataPropertySetter.getDataProperty();

                JsonPropertySetter jsonPropertySetter = new JsonPropertySetter();
                jsonPropertySetter.setProperty(request);
                jsonProperty = jsonPropertySetter.getJsonProperty();

            }
            if (query.equals("/post") && contentType.startsWith("multipart/form-data")) {
                PostMultipartVo postMultipartVo = new PostMultipartVo(request, origin);
                responseBody.append(postMultipartVo);
            }



            //todo
            if (query.startsWith("/post") && !contentType.startsWith("multipart")){
                responseBody.append("{").append(lineSeparator())
                            .append("   \"args\": {},").append(lineSeparator())
                            .append("   \"data\": ")
                            .append(dataProperty).append(lineSeparator())
                            .append("   \"files\": {},").append(lineSeparator())
                            .append("   \"form\": {},").append(lineSeparator())
                            .append("   \"headers\": {").append(lineSeparator())
                            .append("      \"Accept\": \"*/*\",").append(lineSeparator())
                            .append("      \"Content-Length\": ").append("\"").append(dataProperty.length()).append("\",").append(lineSeparator())
                            .append("      \"Content-Type\": ").append("\"").append(contentType).append("\",").append(lineSeparator())
                            .append("      \"Host\": \"").append(socket.getInetAddress().getHostAddress()).append("\",").append(lineSeparator())
                            .append("      \"User-Agent\": \"curl/7.64.1\"").append(lineSeparator())
                            .append("   },").append(lineSeparator())
                            .append("   \"json\": {").append(lineSeparator())
                            .append(jsonProperty)
                            .append("   }").append(lineSeparator())
                            .append("   \"origin\": \"").append(origin).append(",").append(lineSeparator())
                            .append("   \"url\": \"").append(request.split("\r\n")[1].split(" ")[1]).append("\"").append(lineSeparator())
                            .append("}").append(lineSeparator());
}

            if (query.startsWith("/get")){
                responseBody.append("{").append(lineSeparator()).append("   \"args\": {");
                if (query.contains("?")) {
                    responseBody.append(lineSeparator()).append(argsProperty);  //여기에 메시지
                }
                responseBody.append("},").append(lineSeparator())
                        .append("   \"headers\": {").append(lineSeparator())
                        .append("      \"Accept\": \"*/*\",").append(lineSeparator())
                        .append("      \"Host\": \"" + socket.getInetAddress().getHostAddress() + "\",").append(lineSeparator())
                        .append("      \"User-Agent\": \"curl/7.64.1\"").append(lineSeparator())
                        .append("   },").append(lineSeparator())
                        .append("   \"origin\": \"").append(origin).append(",").append(lineSeparator())
                        .append("   \"url\": \"").append(request.split("\r\n")[1].split(" ")[1]).append("\"").append(lineSeparator())
                        .append("}").append(lineSeparator());

                responseBody.append("{").append(lineSeparator()).append("   \"args\": {");
            }

            // 응답 헤더
            responseHeader.append("HTTP/1.1 200 OK").append(lineSeparator())
                          // FIXME: Date 를 yoda time으로 바꾸기
                          .append("Date: " + new Date()).append(lineSeparator())
                          .append("Content-Type: ").append(contentType).append(lineSeparator())
//                    .append("Content-Length: ").append(json.length()).append(lineSeparator())
                          .append("Content-Length: ").append(responseHeader.length() + responseBody.length()).append(lineSeparator())
                          .append("Server: gunicorn/19.9.0").append(lineSeparator())
                          .append("Access-Control-Allow-Origin: *").append(lineSeparator())
                          .append("Access-Control-Allow-Credentials: true").append(lineSeparator()).append(lineSeparator());

            // curl 요청이 GET 으로 왔을 때
            if (request.split(lineSeparator())[0].split(" ")[1].equals("/ip")) {
                responseBody.append(json);
            }

            System.out.println("wqe");
            System.out.print(request);
            System.out.println("wd");
            System.out.println(responseHeader);
            System.out.println("we");
            System.out.println(responseBody);
            System.out.println("wrq");

            try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()))) {
                writer.write(String.valueOf(responseHeader));
                writer.newLine();
                writer.write(String.valueOf(responseBody));

                writer.write(lineSeparator());
                writer.flush();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getQuery(String request) {
        return request.split(lineSeparator())[0].split(" ")[1];
    }
}
