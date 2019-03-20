package org.ibcn.gso.labo5.visitor;

public interface NoteElement {
    public void accept(NoteElementVisitor visitor);
}