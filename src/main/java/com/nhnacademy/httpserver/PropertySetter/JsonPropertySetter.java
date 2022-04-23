package com.nhnacademy.httpserver.PropertySetter;

import static java.lang.System.lineSeparator;

public class JsonPropertySetter implements PropertySetter{

    String dataProperty;
    String jsonProperty ="";

    @Override
    public void setProperty(String request) {
        dataProperty = request.split("\r\n\r\n")[1];
        String[] strings = dataProperty.split(",");
        strings[0] = strings[0].split("\\{")[1];
        strings[strings.length - 1] = strings[strings.length - 1].split("\\}")[0];
        for (int i = 0; i < strings.length; i++) {
            jsonProperty += "    " + strings[i] + lineSeparator();
        }
    }

    public String getJsonProperty() {
        System.out.println(jsonProperty);
        return jsonProperty;
    }

}
