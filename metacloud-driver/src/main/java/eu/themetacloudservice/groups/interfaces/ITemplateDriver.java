package eu.themetacloudservice.groups.interfaces;

import java.util.ArrayList;

public interface ITemplateDriver {


    void create(String template, boolean bungee);
    void copy(String template, String directory);
    void delete(String template);
    ArrayList<String> get();


}