package org.reactome.server.tools.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.reactome.server.tools.domain.model.DatabaseObject;
import org.reactome.server.tools.domain.model.Event;
import org.reactome.server.tools.domain.model.NegativeRegulation;
import org.reactome.server.tools.domain.model.PositiveRegulation;
import org.reactome.server.tools.repository.EventRepository;
import org.reactome.server.tools.service.util.DatabaseObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by:
 *
 * @author Florian Korninger (florian.korninger@ebi.ac.uk)
 * @since 28.02.16.
 */
@Service
public class EventServiceImpl extends ServiceImpl<Event> implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public GraphRepository<Event> getRepository() {
        return eventRepository;
    }

    @Override
    public Event findById(String id) {
        id = DatabaseObjectUtils.trimId(id);
        if (DatabaseObjectUtils.isStId(id)) {
            return eventRepository.findByStableIdentifier(id);
        } else if (DatabaseObjectUtils.isDbId(id)){
            return eventRepository.findByDbId(Long.parseLong(id));
        }
        return null;
    }

    @Override
    public Event findByDbId(Long dbId) {
        return eventRepository.findByDbId(dbId);
    }

    @Override
    public Event findByStableIdentifier(String stableIdentifier) {
        return eventRepository.findByStableIdentifier(stableIdentifier);
    }

    @Override
    @Transactional
    public Event findByIdWithLegacyFields(String id) {
        Event event = findById(id);
        if (event == null) return null;
        if (event.getNegativelyRegulatedBy() != null) {
            List<DatabaseObject> regulator = new ArrayList<>();
            for (NegativeRegulation negativeRegulation : event.getNegativelyRegulatedBy()) {
                eventRepository.findOne(negativeRegulation.getId());
                regulator.add(negativeRegulation.getRegulator());
            }
            event.setNegativeRegulators(regulator);
        }
        if (event.getPositivelyRegulatedBy() != null) {
            List<DatabaseObject> regulator = new ArrayList<>();
            for (PositiveRegulation positiveRegulation : event.getPositivelyRegulatedBy()) {
                eventRepository.findOne(positiveRegulation.getId());
                regulator.add(positiveRegulation.getRegulator());
            }
            event.setPositiveRegulators(regulator);
        }
        return event;
    }
}