package com.nhnacademy.httpserver.PropertySetter;

public class ArgsPropertySetter implements PropertySetter{

    String argsProperty = "";

    @Override
    public void setProperty(String query) {
        String[] strings = query.split("\\?")[1].split("&");
        for (int i = 0; i < strings.length; i++) {
            String[] keyAndValue = strings[i].split("=");
            argsProperty += "  \"" + keyAndValue[0] + "\": " + "\"" + keyAndValue[1] + "\",\r\n";

        }
        argsProperty += "  ";
    }
    public String getArgsProperty() {
        return argsProperty;
    }
}
