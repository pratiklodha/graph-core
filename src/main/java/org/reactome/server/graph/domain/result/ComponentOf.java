package org.reactome.server.graph.domain.result;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.*;

/**
 * Created by:
 *
 * @author Florian Korninger (florian.korninger@ebi.ac.uk)
 * @since 18.04.16.
 */
@SuppressWarnings("unused")
@QueryResult
public class ComponentOf {

    private String type;
    private List<String> names;
    private List<String> stIds;
    private List<String> schemaClasses;

    public ComponentOf() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getStIds() {
        return stIds;
    }

    public void setStIds(List<String> stIds) {
        this.stIds = stIds;
    }

    public List<String> getSchemaClasses() {
        return schemaClasses;
    }

    public void setSchemaClasses(List<String> schemaClasses) {
        this.schemaClasses = schemaClasses;
    }

    public void sortByName(){
        List<String> sortedList = new ArrayList<>(names.size());
        for (int i = 0; i < names.size(); i++) {
            String sortingLine = names.get(i).concat("###").concat(stIds.get(i)).concat("###").concat(schemaClasses.get(i));
            sortedList.add(sortingLine);
        }

        Collections.sort(sortedList, String::compareToIgnoreCase);
        stIds.clear();
        schemaClasses.clear();
        names.clear();
        for (String sortedLine : sortedList) {
            String[] items = sortedLine.split("###");
            names.add(items[0]);
            stIds.add(items[1]);
            schemaClasses.add(items[2]);
        }
    }
}