package eu.metacloudservice.configuration.dummys.message;

import eu.metacloudservice.configuration.interfaces.IConfigAdapter;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class Messages implements IConfigAdapter {

    public HashMap<String, String> messages;


    public Messages(HashMap<String, String> messages) {
        this.messages = messages;
    }

    public Messages() {
    }

}
