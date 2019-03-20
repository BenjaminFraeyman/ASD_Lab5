package org.ibcn.gso.labo5.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.ibcn.gso.labo5.visitor.NoteElement;
import org.ibcn.gso.labo5.visitor.NoteElementVisitor;

public class CheckList implements Content, NoteElement {
    private List<CheckListItem> items;

    public CheckList() {
        this.items = new LinkedList<>();
    }

    public CheckList(CheckListItem... items) {
        this.items = Arrays.asList(items);
    }

    public List<CheckListItem> getItems() {
        return items;
    }
    
    public void setItems(List<CheckListItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return items.toString();
    }

    @Override
    public void accept(NoteElementVisitor visitor) {
        visitor.visit(this);
        for (CheckListItem item : items){
            item.accept(visitor);
        }
    }
}