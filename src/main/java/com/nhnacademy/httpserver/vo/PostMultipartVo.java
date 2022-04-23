package com.nhnacademy.httpserver.vo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class PostMultipartVo {
    String args = "";
    String data = "";
    String files = "";
    String upload = "";
    String form = "";
    String header = "";
    String accept = "";
    String contentLength = "";
    String contentType = "";
    String host = "";
    String userAgent = "";
    String json = "";
    String origin = "";
    String url = "";

    public PostMultipartVo(String request, String origin) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(request));
        String line = "";
        int cnt = 0;
        while (true) {
            line = reader.readLine();
            if (line.startsWith("Host:")) {
                this.host = line.split(" ")[1];
                continue;
            }
            if (line.startsWith("Accept:")) {
                this.accept = line.split(" ")[1];
                continue;
            }
            if (line.startsWith("Content-Length:")) {
                this.contentLength = line.split(" ")[1];
                continue;
            }
            if (line.startsWith("Content-Type:") && cnt == 0) {
                this.contentType = line;
                cnt++;
                continue;
            }
            if (line.startsWith("User-Agent:")) {
                this.userAgent = line.split(" ")[1];
                continue;
            }
            if (line.startsWith("{")) {
                this.upload = line.split("\r\n")[0];
                break;
            }
        }

        this.args = "";
        this.data = "";
        this.files = "";
        this.form = "";
        this.header = "";
        this.json = null;
        this.origin = origin;
        this.url = request.split("\r\n")[1].split(" ")[1];
    }

    @Override
    public String toString() {
        return "{" + "\r\n" +
                "  \"args\": " + "{}," + "\r\n" +
                "  \"data\": " + "\"\","+ "\r\n" +
                "  \"files\": " + "{"  + "\r\n" +
                "    \"upload\": " + upload + "\"" + "\r\n" +
                "  }," + "\r\n" +
                "  \"form\": " + "{}," + "\r\n" +
                "  \"headers\": " + "{" + "\r\n" +
                "    \"Accept\": " +  "\"" + accept + "\"," + "\r\n" +
                "    \"ContentLength\": " + "\"" + contentLength + "\"," + "\r\n" +
                "    \"ContentType\": " + "\"" + contentType + "\"," + "\r\n" +
                "    \"Host\": " + "\"" + host + "\"," + "\r\n" +
                "    \"User-Agent\": " + "\"" + userAgent + "\"" + "\r\n" +
                "  }," + "\r\n" +
                "  \"json\": " + json + "," + "\r\n" +
                "  \"origin\": " + "\"" + origin + "\"," + "\r\n" +
                "  \"url\": " + "\"" + url + "\"" + "\r\n" +
                "}";
    }
}
