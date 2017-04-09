package org.reactome.server.graph.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.reactome.server.graph.domain.model.*;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.helper.RelationshipDirection;
import org.reactome.server.graph.util.CustomQueryResult;
import org.reactome.server.graph.util.DatabaseObjectFactory;
import org.reactome.server.graph.util.JunitHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Florian Korninger <florian.korninger@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AdvancedServiceTest extends BaseTest {

    private static final Long dbId = 5205685L;
    private static final Long dbId2 = 199420L;
    private static final String stId = "R-HSA-5205685";
    private static final String stId2 = "R-HSA-199420";

    @Autowired
    private AdvancedDatabaseObjectService advancedDatabaseObjectService;

    @BeforeClass
    public static void setUpClass() {
        logger.info(" --- !!! Running " + AdvancedServiceTest.class.getName() + "!!! --- \n");
    }

    // --------------------------------------- Enhanced Finder Methods -------------------------------------------------

    @Test
    public void findEnhancedPhysicalEntityByIdTest() {

        logger.info("Started testing advancedDatabaseObjectService.findEnhancedPhysicalEntityByIdTest");
        long start, time;
        start = System.currentTimeMillis();
        PhysicalEntity peObserved = advancedDatabaseObjectService.findEnhancedObjectById("R-HSA-60140");
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertEquals("R-HSA-113454", peObserved.getPositivelyRegulates().get(0).getRegulatedEntity().getStId());
        logger.info("Finished");
    }

    @Test
    public void findEnhancedEventByIdTest() {

        logger.info("Started testing advancedDatabaseObjectService.findEnhancedPathwayByIdTest");
        long start, time;
        start = System.currentTimeMillis();
        Event pathwayObserved = advancedDatabaseObjectService.findEnhancedObjectById(1368139L);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertFalse(pathwayObserved.getDisplayName() + " should have regulators", pathwayObserved.getRegulatedBy().isEmpty());
        logger.info("Finished");
    }

    // --------------------------------------- Limited Finder Methods --------------------------------------------------

    @Test
    public void findLimitedObjectByIdTest() {

        logger.info("Started testing advancedDatabaseObjectService.findAllByProperty");
        long start, time;
        start = System.currentTimeMillis();
        Pathway databaseObjectObserved = advancedDatabaseObjectService.findById("R-HSA-5205685", 10);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertTrue(databaseObjectObserved.getHasEvent() != null);
        logger.info("Finished");
    }


    // --------------------------------------- Generic Finder Methods --------------------------------------------------

    @Test
    public void findByPropertyTest() throws InvocationTargetException, IllegalAccessException {

        logger.info("Started testing advancedDatabaseObjectService.findByProperty");
        long start, time;
        start = System.currentTimeMillis();
        DatabaseObject databaseObjectObserved = advancedDatabaseObjectService.findByProperty(DatabaseObject.class, "stId", stId, 1);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        start = System.currentTimeMillis();
        DatabaseObject databaseObjectExpected = DatabaseObjectFactory.createObject(dbId.toString());
        time = System.currentTimeMillis() - start;
        logger.info("GkInstance execution time: " + time + "ms");

        JunitHelper.assertDatabaseObjectsEqual(databaseObjectExpected, databaseObjectObserved);
        logger.info("Finished");
    }

    @Test
    public void findAllByPropertyTest() {

        logger.info("Started testing advancedDatabaseObjectService.findAllByProperty");
        long start, time;
        start = System.currentTimeMillis();
        Collection<DatabaseObject> databaseObjectObserved = advancedDatabaseObjectService.findAllByProperty(DatabaseObject.class, "stId", stId, 1);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertEquals(1, databaseObjectObserved.size());
        logger.info("Finished");
    }

    // ---------------------- Methods with RelationshipDirection and Relationships -------------------------------------

    @Test
    public void findByDbIdWithRelationshipDirectionTest() throws InvocationTargetException, IllegalAccessException {
        logger.info("Started testing advancedDatabaseObjectService.findByDbIdWithRelationshipDirectionTest");
        long start, time;
        start = System.currentTimeMillis();
        DatabaseObject databaseObjectObserved = advancedDatabaseObjectService.findById(dbId, RelationshipDirection.UNDIRECTED);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        start = System.currentTimeMillis();
        DatabaseObject databaseObjectExpected = DatabaseObjectFactory.createObject(dbId.toString());
        time = System.currentTimeMillis() - start;
        logger.info("GkInstance execution time: " + time + "ms");

        JunitHelper.assertDatabaseObjectsEqual(databaseObjectExpected, databaseObjectObserved);
        logger.info("Finished");
    }

    @Test
    public void findByStIdWithRelationshipDirectionTest() throws InvocationTargetException, IllegalAccessException {
        logger.info("Started testing advancedDatabaseObjectService.findByStIdWithRelationshipDirectionTest");
        long start, time;
        start = System.currentTimeMillis();
        DatabaseObject databaseObjectObserved = advancedDatabaseObjectService.findById(stId, RelationshipDirection.UNDIRECTED);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        start = System.currentTimeMillis();
        DatabaseObject databaseObjectExpected = DatabaseObjectFactory.createObject(dbId.toString());
        time = System.currentTimeMillis() - start;
        logger.info("GkInstance execution time: " + time + "ms");

        JunitHelper.assertDatabaseObjectsEqual(databaseObjectExpected, databaseObjectObserved);
        logger.info("Finished");
    }

    @Test
    public void findByDbIdWithRelationshipDirectionAndRelationshipsTest() {
        logger.info("Started testing advancedDatabaseObjectService.findByDbIdWithRelationshipDirectionAndRelationshipsTest");
        long start, time;
        start = System.currentTimeMillis();
        Pathway databaseObjectObserved = advancedDatabaseObjectService.findById(dbId, RelationshipDirection.OUTGOING, "hasEvent");
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertEquals(8, databaseObjectObserved.getHasEvent().size());
        logger.info("Finished");
    }

    @Test
    public void findByStIdWithRelationshipDirectionAndRelationshipsTest() {
        logger.info("Started testing advancedDatabaseObjectService.findByStIdWithRelationshipDirectionAndRelationshipsTest");
        long start, time;
        start = System.currentTimeMillis();
        Pathway databaseObjectObserved = advancedDatabaseObjectService.findById(stId, RelationshipDirection.OUTGOING, "hasEvent");
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertEquals(8, databaseObjectObserved.getHasEvent().size());
        logger.info("Finished");
    }

    @Test
    public void findByDbIdsWithRelationshipDirectionTest() {
        logger.info("Started testing advancedDatabaseObjectService.findByDbIdsWithRelationshipDirectionTest");
        long start, time;
        start = System.currentTimeMillis();
        Collection<DatabaseObject> databaseObjectObserved = advancedDatabaseObjectService.findByDbIds(Arrays.asList(dbId, dbId2), RelationshipDirection.OUTGOING);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertEquals(2, databaseObjectObserved.size());
        logger.info("Finished");
    }

    @Test
    public void findByStIdsWithRelationshipDirectionTest() {
        logger.info("Started testing advancedDatabaseObjectService.findByStIdsWithRelationshipDirectionTest");
        long start, time;
        start = System.currentTimeMillis();
        Collection<DatabaseObject> databaseObjectObserved = advancedDatabaseObjectService.findByStIds(Arrays.asList(stId, stId2), RelationshipDirection.OUTGOING);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertEquals(2, databaseObjectObserved.size());
        logger.info("Finished");
    }

    @Test
    public void findByDbIdsWithRelationshipDirectionAndRelationshipsTest() {
        logger.info("Started testing advancedDatabaseObjectService.findByDbIdsWithRelationshipDirectionAndRelationshipsTest");
        long start, time;
        start = System.currentTimeMillis();
        Collection<DatabaseObject> databaseObjectObserved = advancedDatabaseObjectService.findByDbIds(Arrays.asList(dbId, dbId2), RelationshipDirection.OUTGOING, "hasEvent", "referenceEntity");
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertEquals(2, databaseObjectObserved.size());
        logger.info("Finished");
    }

    @Test
    public void findByStIdsWithRelationshipDirectionAndRelationshipsTest() {
        logger.info("Started testing advancedDatabaseObjectService.findByStIdsWithRelationshipDirectionAndRelationshipsTest");
        long start, time;
        start = System.currentTimeMillis();
        Collection<DatabaseObject> databaseObjectObserved = advancedDatabaseObjectService.findByStIds(Arrays.asList(stId, stId2), RelationshipDirection.OUTGOING, "hasEvent", "referenceEntity");
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        assertEquals(2, databaseObjectObserved.size());
        logger.info("Finished");
    }

    // ------------------------------- Methods with Custom Query -------------------------------------
    @Test
    public void customQueryWithCustomObjectTest() throws CustomQueryException {
        logger.info("Started testing advancedDatabaseObjectService.customQueryForObject");

        String query = "MATCH (p:Pathway{dbId:{dbId}})-[:hasEvent]->(m) RETURN p.dbId as dbId, p.displayName as name, Collect(m.dbId) AS events, Collect(m.dbId) AS eventsPrimitiveArray, Collect(m.displayName) AS eventsArray ";
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("dbId", 1640170);
        CustomQueryResult customQueryResult = advancedDatabaseObjectService.customQueryForObject(CustomQueryResult.class, query, parametersMap);
        assertEquals(4, customQueryResult.getEvents().size());
        assertEquals(4, customQueryResult.getEventsArray().length);
        assertEquals(4, customQueryResult.getEventsPrimitiveArray().length);
    }

    @Test
    public void customQueryListOfCustomObjectTest() throws CustomQueryException {
        logger.info("Started testing advancedDatabaseObjectService.customQueryForObjects");

        String query = "MATCH (p:Pathway)-[:hasEvent]->(m) RETURN p.dbId as dbId, p.displayName as name, Collect(m.dbId) AS events, Collect(m.dbId) AS eventsPrimitiveArray, Collect(m.displayName) AS eventsArray ORDER BY p.dbId LIMIT 20";
        Collection<CustomQueryResult> customQueryResultList = advancedDatabaseObjectService.customQueryForObjects(CustomQueryResult.class, query, null);
        assertNotNull(customQueryResultList);
        assertEquals(20, customQueryResultList.size());
        assertTrue(customQueryResultList.iterator().next().getEvents().size() > 0);
        assertTrue(customQueryResultList.iterator().next().getEventsArray().length > 0);
        assertTrue(customQueryResultList.iterator().next().getEventsPrimitiveArray().length > 0);
    }

    @Test
    public void customQueryDatabaseObjectTest() throws CustomQueryException {
        logger.info("Started testing advancedDatabaseObjectService.customQueryForDatabaseObject");

        String query = "MATCH (p:Pathway{dbId:{dbId}}) RETURN p";
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("dbId", 1640170);
        Pathway pathway = advancedDatabaseObjectService.customQueryForDatabaseObject(Pathway.class, query, parametersMap);
        assertNotNull(pathway);

        // by default, lazy loading is disabled in our tests, enable here for a particular test
        lazyFetchAspect.setEnableAOP(true);
        assertNotNull(pathway.getHasEvent());
        assertEquals(4, pathway.getHasEvent().size());

        // disable it for further tests in this particular test class
        lazyFetchAspect.setEnableAOP(false);
    }

    @Test
    public void customQueryListOfDatabaseObjectTest() throws CustomQueryException {
        logger.info("Started testing advancedDatabaseObjectService.customQueryForDatabaseObjects");

        String query = "MATCH (p:Pathway{dbId:{dbId}})-[r:hasEvent]->(m) RETURN p,r,m ORDER BY p.dbId";
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("dbId", 1640170);
        // In this test case, the relationships are mapped in the object Pathway inside the Collection
        Collection<Pathway> pathways = advancedDatabaseObjectService.customQueryForDatabaseObjects(Pathway.class, query, parametersMap);
        assertNotNull(pathways);
        assertEquals(5, pathways.size());
        assertEquals(4, pathways.iterator().next().getHasEvent().size());
    }

    @Test
    public void customQueryTest() throws CustomQueryException {
        String query = "MATCH (n:ReferenceEntity) RETURN DISTINCT n.identifier AS identifier";
        Collection<String> accessions = advancedDatabaseObjectService.customQueryResults(query, null);
        assertNotNull(accessions);
        assertTrue(accessions.size() >= 200000);
    }

    @Test
    public void customSimpleQueryTest() throws CustomQueryException {
        String query = "MATCH (n:ReferenceEntity) RETURN COUNT(n.identifier)";
        String pathways = advancedDatabaseObjectService.customQueryResult(query, null);
        assertNotNull(pathways);
    }

    @Test
    public void activeUnitContentTest() {
        logger.info("Started testing advancedDatabaseObjectService.activeUnitContentTest");
        long start, time;
        start = System.currentTimeMillis();
        CatalystActivity ca = advancedDatabaseObjectService.findById(5643997, 1000);
        assertTrue("There should be only one active unit", ca.getActiveUnit().size() == 1);
        time = System.currentTimeMillis() - start;
        logger.info("GraphDb execution time: " + time + "ms");

        logger.info("Finished");
    }
}