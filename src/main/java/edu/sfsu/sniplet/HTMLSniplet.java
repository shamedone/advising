package edu.sfsu.sniplet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLSniplet extends Sniplet {

    final static private Pattern PATTERN_BEGIN = Pattern.compile("(%%BLOCK:)(\\w+)(%%)");
    final static private Pattern PATTERN_END = Pattern.compile("%%END%%");

    private String name;
    private List<Sniplet> sniplets;
    private Map<String, String> params;

    private HTMLSniplet(BufferedReader br, String name) {
        this.name = name;
        this.params = new HashMap<>();
        sniplets = new ArrayList<>();
        try {
            scan(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HTMLSniplet(HTMLSniplet original) {
        this(original, original.name);
    }

    private HTMLSniplet(HTMLSniplet original, String name) {
        this.name = name;
        this.params = new HashMap<>();
        this.sniplets = new ArrayList<>();
        for (Sniplet sniplet : original.sniplets) {
            if (sniplet instanceof TextSniplet) {
                TextSniplet textSniplet = (TextSniplet) sniplet;
                sniplets.add(new TextSniplet(textSniplet));
            } else {
                HTMLSniplet fragment = (HTMLSniplet) sniplet;
                sniplets.add(new HTMLSniplet(fragment));
            }
        }
    }

    private void scan(BufferedReader br) throws IOException {
        while(true) {
            String line = br.readLine();
            if (line == null) {
                return;
            }
            Matcher matcher = PATTERN_BEGIN.matcher(line);
            if (matcher.find()) {
                String name = matcher.group(2);
                HTMLSniplet nestedFragment = new HTMLSniplet(br, name);
                addSniplet(nestedFragment);
                continue;
            }
            matcher = PATTERN_END.matcher(line);
            if (matcher.find()) {
                return;
            }
            addSniplet(line);
        }
    }

    private void addSniplet(HTMLSniplet fragment) {
        sniplets.add(fragment);
    }

    private void addSniplet(String text) {
        TextSniplet sniplet = new TextSniplet(text);
        if (sniplets.isEmpty()) {
            sniplets.add(sniplet);
            return;
        }
        Sniplet last = sniplets.get(sniplets.size() - 1);
        if (last instanceof HTMLSniplet) {
            sniplets.add(sniplet);
            return;
        }
        ((TextSniplet) last).append(sniplet);
    }

    public HTMLSniplet copy() {
        return new HTMLSniplet(this);
    }

    public HTMLSniplet instantiate(String name) {
        for (int i = 0; i < sniplets.size(); i++) {
            Sniplet sniplet = sniplets.get(i);
            if (sniplet instanceof HTMLSniplet) {
                HTMLSniplet fragment = (HTMLSniplet) sniplet;
                if (fragment.name.equals(name)) {
                    HTMLSniplet clone = new HTMLSniplet(fragment, "");
                    sniplets.add(i, clone);
                    return clone;
                }
            }
        }
        return null;
    }

    public HTMLSniplet p(String param, String value) {
        params.put("%%" + param + "%%", value);
        return this;
    }

    public StringBuffer render() {
        StringBuffer r = new StringBuffer();
        for (Sniplet sniplet : sniplets) {
            if (sniplet instanceof TextSniplet) {
                StringBuffer text = ((TextSniplet) sniplet).text;
                r.append(substituteParameters(text.toString()));
            } else {
                HTMLSniplet fragment = (HTMLSniplet) sniplet;
                if (fragment.name.equals("")) {
                    r.append(fragment.render());
                }
            }
        }
        return r;
    }

    private String substituteParameters(String text) {
        for (String param : params.keySet()) {
            text = text.replaceAll(param, params.get(param));
        }
        return text;
    }

    public static HTMLSniplet fromInputStream(InputStream is) {
        if (is == null) {
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return new HTMLSniplet(br, "");
    }
}
