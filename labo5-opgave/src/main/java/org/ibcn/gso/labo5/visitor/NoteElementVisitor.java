package org.ibcn.gso.labo5.visitor;

import org.ibcn.gso.labo5.model.CheckList;
import org.ibcn.gso.labo5.model.CheckListItem;
import org.ibcn.gso.labo5.model.Message;
import org.ibcn.gso.labo5.model.Note;

public interface NoteElementVisitor {
    void visit(Note note);
    
    void visit(Message message);
    
    void visit(CheckList checkList);
    
    void visit(CheckListItem checkListItem);
}