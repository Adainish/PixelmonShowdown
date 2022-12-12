package io.github.adainish.pixelmonshowdown.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static String formattedString(String s) {
        return s.replaceAll("&", "§");
    }

    public static List<String> formattedArrayList(List<String> list) {

        List<String> formattedList = new ArrayList<>();
        for (String s:list) {
            formattedList.add(formattedString(s));
        }

        return formattedList;
    }
}
