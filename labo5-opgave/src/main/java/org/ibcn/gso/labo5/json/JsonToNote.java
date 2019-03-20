package org.ibcn.gso.labo5.json;

import java.util.stream.Collectors;
import org.ibcn.gso.labo5.model.CheckList;
import org.ibcn.gso.labo5.model.CheckListItem;
import org.ibcn.gso.labo5.model.Message;
import org.ibcn.gso.labo5.model.Note;
import org.ibcn.utils.json.JsonArray;
import org.ibcn.utils.json.JsonObject;

public class JsonToNote {
    private final JsonObject json;

    public JsonToNote(JsonObject json) {
        this.json = json;
    }

    public Note getNote() {
        Note note = new Note();
        note.setId(json.getString("id"));
        note.setTitle(json.getString("title"));
        if (json.get("content") instanceof JsonArray) {
            //content is a checklist
            CheckList list = new CheckList();
            list.setItems(json.get("content").asArray().stream().map(jsonEntry -> new CheckListItem(jsonEntry.getString("label"), jsonEntry.getBool("checked"))).collect(Collectors.toList()));
            note.setContent(list);
        } else {
            //content is a simple message
            note.setContent(new Message(json.getString("content")));
        }
        return note;
    }
}