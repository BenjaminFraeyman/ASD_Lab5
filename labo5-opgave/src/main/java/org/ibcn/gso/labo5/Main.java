package org.ibcn.gso.labo5;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.ibcn.gso.labo5.model.Credentials;
import spark.Request;
import spark.Spark;
import org.apache.commons.codec.binary.Base64;
import org.ibcn.gso.labo5.json.JsonToNote;
import org.ibcn.gso.labo5.json.NoteToJson;
import org.ibcn.gso.labo5.model.CheckList;
import org.ibcn.gso.labo5.model.CheckListItem;
import org.ibcn.gso.labo5.model.Message;
import org.ibcn.gso.labo5.model.Note;
import org.ibcn.gso.labo5.storage.Storage;
import org.ibcn.utils.json.JsonArray;
import org.ibcn.utils.json.JsonObject;
import org.slf4j.LoggerFactory;
import org.ibcn.utils.json.Json;

public class Main {

    private static final Credentials[] REGISTERED_USERS = new Credentials[]{new Credentials("gso1", "gso1"), new Credentials("gso2", "gso2")};
    private static Map<String , Note> map;
        public static void main(String[] args) throws Exception {
        Spark.staticFiles.location("/public");
        
//        // TEST CODE ---------------------------------------------
//        CheckList list = new CheckList();
//        list.getItems().add(new CheckListItem("lol", false));
//        list.getItems().add(new CheckListItem("oll", true));
//        // Message message = new Message();
//        // message.setBody("testjeeee");
//        Note test = new Note("1", "text", list);
//        NoteToJson jsonn = new NoteToJson(test);
//        System.out.println(jsonn.getJSON());
//        // ---------------------------------------------------------
        
        
        Spark.before((req, resp)->{
            Optional<Credentials> temp  = getAuth(req);
            if(!temp.isPresent()){
                resp.header("WWW-Authenticate", "Basic realm=\"GSO Notes APP\"");
                Spark.halt(401);          
            }
            map =Storage.INSTANCE.getStorageForUser(getAuth(req).get());
        });
        
        // Listing all notes
        Spark.get("/api/notes", (req, res) -> {
            JsonArray array = new JsonArray();
            for(Map.Entry<String,Note> entry : map.entrySet()){
                Note tmp = entry.getValue();
                JsonObject object = new NoteToJson(tmp).getJSON();
                array.add(object);
            }
            return array;
        });        
        
        // Getting a single note
        Spark.get("/api/notes/:id", (req, res) -> {
            Note note = map.get(req.params(":id"));
            return note;                      
        });

        // Creating a new note
        Spark.post("/api/notes", "application/json", (req, res) -> {
           JsonObject object =  (JsonObject) Json.from(req.body());
           JsonToNote convert = new JsonToNote(object); 
           Note value = convert.getNote();         
           value.setId(UUID.randomUUID().toString());          
           map.put(value.getId(), value);          
           NoteToJson convertNote = new NoteToJson(value);
           JsonObject json =convertNote.getJSON();     
           return json.asString();       
        });

        // Updating an existing note
        Spark.put("/api/notes/:id", "application/json", (req, res) -> {
           JsonObject object =  (JsonObject) Json.from(req.body());
           
           JsonToNote convert = new JsonToNote(object); 
           Note value = convert.getNote();
           value.setId(req.params(":id"));
           
           map.replace(req.params(":id"), value);
                           
           NoteToJson convertNote = new NoteToJson(value);
           JsonObject json =convertNote.getJSON();
           return json.toString();      
        });

        // Deleting a note
        Spark.delete("/api/notes/:id", (req, res) -> {
            Note removed = map.remove(req.params(":id"));
            System.out.println("");
            return null;          
        });
        
        Spark.init();
    }

    private static Optional<Credentials> getAuth(Request request) {
        String authHeader = request.headers("Authorization");
        if (authHeader != null) {
            String encodedCredentials = authHeader.substring(authHeader.indexOf(' ')).trim();
            try {
                String decodedCredentials = new String(Base64.decodeBase64(encodedCredentials), "UTF-8");
                String username = decodedCredentials.split(":")[0];
                String password = decodedCredentials.split(":")[1];
                return Arrays.stream(REGISTERED_USERS).filter(entry -> entry.getUsername().equals(username) && entry.getPassword().equals(password)).findAny();
            } catch (Exception e) {
                LoggerFactory.getLogger(Main.class).warn("Auth Header for request {} could not be processed.", request);
            }
        }
        return Optional.empty();
    }
}