package org.reactome.server.tools.repository;

import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.reactome.server.tools.domain.model.DatabaseObject;
import org.reactome.server.tools.domain.model.Pathway;
import org.reactome.server.tools.domain.model.Species;
import org.reactome.server.tools.repository.util.RepositoryUtils;
import org.reactome.server.tools.service.helper.RelationshipDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by:
 *
 * @author Florian Korninger (florian.korninger@ebi.ac.uk)
 * @since 11.11.15.
 */
@SuppressWarnings("unused")
@Repository
public class GeneralRepository {

    private static final Logger logger = LoggerFactory.getLogger(GeneralRepository.class);

    @Autowired
    private Session session;

    @Autowired
    private Neo4jOperations neo4jTemplate;

//    Match (n:DatabaseObject{stableIdentifier:"R-ALL-113592"})-[r]->(m),(n)-[e:regulator|physicalEntity]-(l),(l)-[w:catalystActivity|regulatedBy]-(k) RETURN n,r,m,e,l,w,k

//    public DatabaseObject findByDbId(Long dbId, RelationshipDirection direction) {
//        String query;
//        if (direction.equals(RelationshipDirection.INCOMING)) {
//            query = "Match (n:DatabaseObject{dbId:{dbId}})<-[r]-(m) RETURN n,r,m";
//        } else {
//            query = "Match (n:DatabaseObject{dbId:{dbId}})-[r]->(m) RETURN n,r,m";
//        }
//        Map<String,Object> map = new HashMap<>();
//        map.put("dbId", dbId);
//        Result result =  neo4jTemplate.query(query, map);
//        if (result != null && result.iterator().hasNext())
//            return (DatabaseObject) result.iterator().next().get("n");
//        return null;
//    }
//
//
//    public DatabaseObject findByStableIdentifier(String stId, RelationshipDirection direction) {
//        String query;
//        if (direction.equals(RelationshipDirection.INCOMING)) {
//            query = "Match (n:DatabaseObject{stableIdentifier:{stId}})<-[r]-(m) RETURN n,r,m";
//        } else {
//            query = "Match (n:DatabaseObject{stableIdentifier:{stId}})-[r]->(m) RETURN n,r,m";
//        }
//        Map<String,Object> map = new HashMap<>();
//        map.put("stId", stId);
//        Result result =  neo4jTemplate.query(query, map);
//        if (result != null && result.iterator().hasNext())
//            return (DatabaseObject) result.iterator().next().get("n");
//        return null;
//    }


    public Object findByPropertyWithRelations (Long dbId, RelationshipDirection direction, String... relationships) {
        String query;
        switch (direction) {
            case OUTGOING:
                query = "MATCH (n:DatabaseObject{dbId:{dbId}})-[r" + RepositoryUtils.getRelationshipAsString(relationships) + "]->(m) RETURN n,r,m";
                break;
            case INCOMING:
                query = "MATCH (n:DatabaseObject{dbId:{dbId}})<-[r" + RepositoryUtils.getRelationshipAsString(relationships) + "]-(m) RETURN n,r,m";
                break;
            default: //UNDIRECTED
                query = "MATCH (n:DatabaseObject{dbId:{dbId}})-[r" + RepositoryUtils.getRelationshipAsString(relationships) + "]-(m) RETURN n,r,m";
                break;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("dbId",dbId);
        Result result =  neo4jTemplate.query(query,map);
        if (result != null && result.iterator().hasNext())
            return result.iterator().next().get("n");
        return null;
    }

//    @Override
//    public Object findByPropertyWithRelations (String property, Object value, String... relationships) {
//        String query = "MATCH (n:DatabaseObject{" + property + ":{" + property + "}})-[r";
//        query += RepositoryUtils.getRelationshipAsString(relationships);
//        query += "]-(m) RETURN n,r,m";
//        Map<String,Object> map = new HashMap<>();
//        map.put(property,value);
//        Result result =  neo4jTemplate.query(query,map);
//        if (result != null && result.iterator().hasNext())
//            return result.iterator().next().get("n");
//        return null;
//    }
//
//    @Override
//    public Object findByPropertyWithoutRelations (String property, Object value, String... relationships) {
//        String query = "MATCH (n:DatabaseObject{" + property + ":{" + property + "}})-[r]-(m) WHERE NOT (n)-[r";
//        query += RepositoryUtils.getRelationshipAsString(relationships);
//        query += "]-(m) RETURN n,r,m";
//        Map<String,Object> map = new HashMap<>();
//        map.put(property,value);
//        Result result =  neo4jTemplate.query(query,map);
//        if (result != null && result.iterator().hasNext())
//            return  result.iterator().next().get("n");
//        return null;
//    }

    public void load(Long id) {
        neo4jTemplate.load(DatabaseObject.class,id);
    }

//    public <T> Collection<T> getObjectsByClassName(Class<T> clazz, Integer page, Integer offset) {
//        String query = "MATCH (n:" +
//                clazz.getSimpleName() +
//                ") RETURN n ORDER BY n.displayName SKIP {skip} LIMIT {limit}";
//
//        Map<String,Object> map = new HashMap<>();
//        map.put("limit", offset);
//        map.put("skip", (page-1) * offset);
//        return (Collection<T>) neo4jTemplate.queryForObjects(clazz, query, map);
//    }


    public <T> T findByProperty(Class<T> clazz, String property, Object value, Integer depth) {
        return neo4jTemplate.loadByProperty(clazz, property, value, depth);
    }

    public <T> T findById(Class<T> clazz, Long id, Integer depth) {
        return neo4jTemplate.load(clazz, id, depth);
    }

    public <T> T findByDbId(Class<T> clazz, Long dbId, Integer depth) {
        Collection<T> collection = session.loadAll(clazz, new Filter("dbId", dbId), depth);
        if (collection != null && !collection.isEmpty()) {
            return collection.iterator().next();
        }
        return null;
    }

    public <T> T findByStableIdentifier(Class<T> clazz, String stableIdentifier, Integer depth) {
        Collection<T> collection = session.loadAll(clazz, new Filter("stableIdentifier", stableIdentifier), depth);
        if (collection != null && !collection.isEmpty()) {
            return collection.iterator().next();
        }
        return null;
    }


    public Collection<Pathway> getTopLevelPathways() {
        String query = "Match (n:TopLevelPathway) RETURN n";
        return (Collection<Pathway>) neo4jTemplate.queryForObjects(Pathway.class, query, Collections.<String,Object>emptyMap());
    }


    public Collection<Pathway> getTopLevelPathways(Long speciesId) {
        String query = "Match (n:TopLevelPathway)-[:species]-(s) Where s.dbId = {speciesId} RETURN n";
        Map<String,Object> map = new HashMap<>();
        map.put("speciesId", speciesId);
        return (Collection<Pathway>) neo4jTemplate.queryForObjects(Pathway.class, query, map);
    }


    public Collection<Pathway> getTopLevelPathways(String speciesName) {
        String query = "Match (n:TopLevelPathway)-[:species]-(s) Where s.displayName = {speciesName} RETURN n";
        Map<String,Object> map = new HashMap<>();
        map.put("speciesName", speciesName);
        return (Collection<Pathway>) neo4jTemplate.queryForObjects(Pathway.class, query, map);
    }

    //TODO
    public Pathway getEventHierarchy(Long dbId) {
        String query = "Match (n:Event{dbId:{dbId}})-[r:hasEvent*]->(m:Event) return n,r,m";
        Map<String,Object> map = new HashMap<>();
        map.put("dbId", dbId);
        Result result =  neo4jTemplate.query(query, map);
        if (result != null && result.iterator().hasNext())
            return (Pathway) result.iterator().next().get("n");
        return null;
    }

    //TODO
    public Object getSomeHierarchy(Long dbId) {
        String query = "Match (n:DatabaseObject{dbId:{dbId}})<-[r:hasComponent|hasMember|input|output|hasEvent*]-(m) " +
                "RETURN n.dbId,n.stableIdentifier,n.displayName, " +
                "EXTRACT(rel IN r | [endNode(rel).dbId, endNode(rel).stableIdentifier, endNode(rel).displayName]) as nodePairCollection";
        Map<String,Object> map = new HashMap<>();
        map.put("dbId", dbId);
        Result result =  neo4jTemplate.query(query, map);
        return null;
    }


    //TODO
    public DatabaseObject getLocationsHierarchy(String stId) {
        String query = "Match (n:DatabaseObject{dbId:373624})<-[r:regulatedBy|regulator|physicalEntity|catalystActivity|hasMember|hasComponent|input|output|hasEvent*]-(m) Return n,r,m";
        Map<String,Object> map = new HashMap<>();
        map.put("stableIdentifier", stId);
        Result result =  neo4jTemplate.query(query, map);
        if (result != null && result.iterator().hasNext())
            return (DatabaseObject) result.iterator().next().get("n");
        return null;
    }

    //TODO
//    public Result getLocationsInPathwayBrowser(String stId) {
//        String query = "Match (n:DatabaseObject{stableIdentifier:{stableIdentifier}})<-[r:regulatedBy|regulator|physicalEntity|entityFunctionalStatus|activeUnit|catalystActivity|repeatedUnit|hasMember|hasCandidate|hasComponent|input|output|hasEvent*]-(m) Return  EXTRACT(rel IN r | [startNode(rel).stableIdentifier, startNode(rel).displayName, startNode(rel).hasDiagram,startNode(rel).speciesName, labels(startNode(rel)) ]) as nodePairCollection";
//        Map<String,Object> map = new HashMap<>();
//        map.put("stableIdentifier", stId);
//        Result result =  neo4jTemplate.query(query, map);
//        return result;
//    }

    //TODO
    public DatabaseObject getReferral(Long dbId, String relationshipName) {

        String query = "Match (n:DatabaseObject{dbId:{dbId}})<-[r:" + relationshipName + "]-(m) Return n";
        Map<String,Object> map = new HashMap<>();
        map.put("dbId", dbId);
        Result result =  neo4jTemplate.query(query, map);
        if (result != null && result.iterator().hasNext())
            return (DatabaseObject) result.iterator().next().get("n");
        return null;
    }
    //TODO
    public Collection<DatabaseObject> getReferrals(Long dbId, String relationshipName) {

        String query = "Match (n:DatabaseObject{dbId:{dbId}})<-[r:" + relationshipName + "]-(m) Return n";
        Map<String,Object> map = new HashMap<>();
        map.put("dbId", dbId);
        Result result =  neo4jTemplate.query(query, map);
        List<DatabaseObject> referrers = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : result) {
            referrers.add((DatabaseObject) stringObjectMap.get("n"));
        }
        return referrers;
    }

    public Collection<DatabaseObject> findCollectionByPropertyWithRelationships (String property, Collection<Object> values, String... relationships) {


        String query = "Match (n:DatabaseObject)-[r" + RepositoryUtils.getRelationshipAsString(relationships) + "]-(m) WHERE n." + property + " IN {values} RETURN n,r,m";
        Map<String,Object> map = new HashMap<>();
        map.put("values", values);
        Result result = neo4jTemplate.query( query, map);
        List<DatabaseObject> databaseObjects = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : result) {
            databaseObjects.add((DatabaseObject) stringObjectMap.get("n"));
        }
        return databaseObjects;

    }



    //TODO
    //Match (n:DatabaseObject{stableIdentifier:"R-HSA-445133"})<-[r:hasMember|hasComponent|input|output|hasEvent*]-(m) Return EXTRACT(rel IN r | [endNode(rel).dbId, endNode(rel).stableIdentifier, endNode(rel).displayName, endNode(rel).hasDiagram ]) as nodePairCollection
    //TODO
    //Match (n:DatabaseObject{stableIdentifier:"R-HSA-445133"})<-[r:regulatedBy|regulator|physicalEntity|catalystActivity|hasMember|hasComponent|input|output|hasEvent*]-(m) Return EXTRACT(rel IN r | [endNode(rel).dbId, endNode(rel).stableIdentifier, endNode(rel).displayName, labels(endNode(rel))  ]) as nodePairCollection


    public Collection<Species> getSpecies() {
        String query = "Match (n:Species) Return n";
        return (Collection<Species>) neo4jTemplate.queryForObjects(Species.class, query, Collections.<String,Object>emptyMap());
    }


    public Result query (String query, Map<String,Object> map) {
        return neo4jTemplate.query(query,map);
    }


    public Long countEntries(Class<?> clazz) {
        return neo4jTemplate.count(clazz);
    }


    public boolean fitForService() {
        String query = "Match (n) Return Count(n)>0 AS fitForService";
        try {
            Result result = neo4jTemplate.query(query, Collections.<String,Object>emptyMap());
            if (result != null && result.iterator().hasNext())
                return (boolean) result.iterator().next().get("fitForService");
        } catch (Exception e) {
            logger.error("A connection with the Neo4j Graph could not be established. Tests will be skipped", e);
        }
        return false;
    }


    public void clearCache() {
        neo4jTemplate.clear();
    }
}