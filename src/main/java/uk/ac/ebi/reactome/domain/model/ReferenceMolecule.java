package uk.ac.ebi.reactome.domain.model;

import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by:
 *
 * @author Florian Korninger (florian.korninger@ebi.ac.uk)
 * @since 13.11.15.
 */
@NodeEntity
public class ReferenceMolecule extends ReferenceEntity{

    public ReferenceMolecule() {}

    public ReferenceMolecule(Long dbId, String stId, String name) {
        super(dbId, stId, name);
    }
}