package org.ibcn.gso.labo5.model;

import org.ibcn.gso.labo5.visitor.NoteElement;
import org.ibcn.gso.labo5.visitor.NoteElementVisitor;

public class Note implements NoteElement {
    private String id;
    private String title;
    private Content content;

    public Note() {
    }

    public Note(String id, String title, Content content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the content
     */
    public Content getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(Content content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return title + " => " + content;
    }

    @Override
    public void accept(NoteElementVisitor visitor) {
        visitor.visit(this);
        content.accept(visitor);
    }
}