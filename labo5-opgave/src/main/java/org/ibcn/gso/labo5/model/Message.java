package org.ibcn.gso.labo5.model;

import org.ibcn.gso.labo5.visitor.NoteElement;
import org.ibcn.gso.labo5.visitor.NoteElementVisitor;

public class Message implements Content, NoteElement {
    private String body;

    public Message() {
    }

    public Message(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }

    @Override
    public void accept(NoteElementVisitor visitor) {
        visitor.visit(this);
    }
}