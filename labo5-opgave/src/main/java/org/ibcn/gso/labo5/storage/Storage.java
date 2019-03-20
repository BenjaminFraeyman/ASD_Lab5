package org.ibcn.gso.labo5.storage;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.ibcn.gso.labo5.model.Credentials;
import org.ibcn.gso.labo5.model.Note;

public class Storage {
    public static final Storage INSTANCE = new Storage();

    private Storage() {}
    
    public Map<String, Note> getStorageForUser(Credentials credentials) throws Exception {
        String location = "./storage/" + credentials.getUsername();
        System.out.println("location: " + location);
        return (Map<String, Note>) Proxy.newProxyInstance(
                Map.class.getClassLoader(),
                new Class<?>[]{Map.class},
                new FileStorageMap(location, new HashMap<>())
                );
    }
}