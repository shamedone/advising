package edu.sfsu.db;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseRequirements {

    public static List<Object> core      = new ArrayList<>();
    public static List<Object> electives = new ArrayList<>();


    private static void scan(List<Object> section, JSONArray courses) {
        for (Object o : courses) {
            if (o instanceof String) {
                section.add(o);
            } else if (o instanceof JSONArray) {
                List<Object> subsection = new ArrayList<>();
                scan(subsection, (JSONArray) o);
                section.add(subsection);
            } else {
                throw new RuntimeException("Bad JSON format");
            }
        }
    }

    public static void init(JSONObject json) {
        scan(core, (JSONArray) json.get("core"));
        scan(electives, (JSONArray) json.get("electives"));
    }
}
