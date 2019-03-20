package org.ibcn.gso.labo5.storage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.ibcn.gso.labo5.json.JsonToNote;
import org.ibcn.gso.labo5.json.NoteToJson;
import org.ibcn.gso.labo5.model.Note;
import org.ibcn.utils.json.JsonObject;
import org.ibcn.utils.json.JsonValue;
import org.ibcn.utils.json.Json;

public class FileStorageMap implements java.lang.reflect.InvocationHandler{
    String location;
    Map<String, Note> proxiedMap;
    File folder;
    File[] fList;
    
    FileStorageMap(String location, Map<String, Note> proxiedMap) throws IOException, Exception{
        this.location = location;
        this.proxiedMap = proxiedMap;
        Path path = Paths.get(location);
        System.out.println("path: " + path.toAbsolutePath());
        
        folder = new File(location);
        if(!Files.exists(path)){
             folder.mkdirs();
        }
        
        fList = folder.listFiles();
        for (int i = 0; i < fList.length; i++) {
            if (fList[i].isFile()) {
                String tempfile = FileUtils.readFileToString(fList[i], "UTF-8");
                JsonObject testt = (JsonObject)Json.from(tempfile);
                Note testtt = new JsonToNote(testt).getNote();
                proxiedMap.put(tempfile, testtt);
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        switch(methodName) {
            case "put":
                Note tnewNote = (Note) args[1];
                File newfile = new File(folder.getAbsolutePath() + "/" + tnewNote.getId() + ".txt");
                JsonObject tnewObject = (JsonObject)new NoteToJson(tnewNote).getJSON();
                FileUtils.writeStringToFile(newfile, tnewObject.toPrettyString(), "UTF-8");
                break;
                
            case "remove":
                for (int i = 0; i < fList.length; i++) {
                    if (fList[i].isFile()) {
                        String tempfile = FileUtils.readFileToString(fList[i], "UTF-8");
                        JsonObject tremoveObject = (JsonObject)Json.from(tempfile);
                        Note tremoveNote = new JsonToNote(tremoveObject).getNote();
                        proxiedMap.remove(tempfile, tremoveNote);
                        if(tremoveNote.getId().equals(args[0].toString())){
                            boolean succes = fList[i].delete();
                        }
                    }
                }
                break;
                
            case "replace":
                for (int i = 0; i < fList.length; i++) {
                    if (fList[i].isFile()) {
                        String tempfile = FileUtils.readFileToString(fList[i], "UTF-8");
                        JsonObject tremoveObject = (JsonObject)Json.from(tempfile);
                        Note tremoveNote = new JsonToNote(tremoveObject).getNote();
                        proxiedMap.remove(tempfile, tremoveNote);
                        if(tremoveNote.getId().equals(args[0].toString())){
                            boolean succes = fList[i].delete();
                        }
                    }
                }
                
                Note treplaceNote = (Note) args[1];
                File replacedfile = new File(folder.getAbsolutePath() + "/" + treplaceNote.getId() + ".txt");
                JsonObject treplaceObject = (JsonObject)new NoteToJson(treplaceNote).getJSON();
                FileUtils.writeStringToFile(replacedfile, treplaceObject.toPrettyString(), "UTF-8");
                break;
                
            default:
                break;
        }
        return method.invoke(proxiedMap, args);
    }
}
