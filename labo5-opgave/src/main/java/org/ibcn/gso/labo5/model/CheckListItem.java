package org.ibcn.gso.labo5.model;

import org.ibcn.gso.labo5.visitor.NoteElement;
import org.ibcn.gso.labo5.visitor.NoteElementVisitor;

public class CheckListItem implements NoteElement {
    private boolean checked;
    private String label;

    public CheckListItem() {
    }

    public CheckListItem(String label, boolean checked) {
        this.label = label;
        this.checked = checked;
    }

    /**
     * @return the checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * @param checked the checked to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        String mark = checked ? "(V)" : "(X)";
        return label + " " + mark;
    }

    @Override
    public void accept(NoteElementVisitor visitor) {
        visitor.visit(this);
    }
}