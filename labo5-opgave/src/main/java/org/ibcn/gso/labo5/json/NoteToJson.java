package org.ibcn.gso.labo5.json;

import org.ibcn.gso.labo5.model.CheckList;
import org.ibcn.gso.labo5.model.CheckListItem;
import org.ibcn.gso.labo5.model.Message;
import org.ibcn.gso.labo5.model.Note;
import org.ibcn.gso.labo5.visitor.NoteElementVisitor;
import org.ibcn.utils.json.JsonArray;
import org.ibcn.utils.json.JsonObject;

/**
 *
 * @author Benjamin
 */
public class NoteToJson implements NoteElementVisitor {
    Note note;
    JsonObject json = new JsonObject();
    
    public NoteToJson(Note note){
        this.note = note;
    }
    
    public JsonObject getJSON(){
        note.accept(this);
        return json;
    }
    
    @Override
    public void visit(Note note) {
        json.put("id", note.getId());
        json.put("title", note.getTitle());
    }

    @Override
    public void visit(Message message) {
        json.put("content", message.getBody());
    }

    @Override
    public void visit(CheckList checkList) {
        json.put("content", new JsonArray());
    }

    @Override
    public void visit(CheckListItem checkListItem) {
        JsonObject temp = new JsonObject();
        temp.put("label", checkListItem.getLabel());
        temp.put("checked", checkListItem.isChecked());
        ((JsonArray)json.get("content")).add(temp);
    }
}