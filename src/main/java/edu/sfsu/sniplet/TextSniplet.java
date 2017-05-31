package edu.sfsu.sniplet;

public class TextSniplet extends Sniplet {
    StringBuffer text;

    TextSniplet(TextSniplet original) {
        text = new StringBuffer(original.text);
    }

    TextSniplet(String text) {
        this.text = new StringBuffer(text);
        this.text.append("\n");
    }

    void append(TextSniplet other) {
        this.text.append(other.text);
    }
}
