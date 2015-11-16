package uk.ac.ebi.reactome.service.placeholder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.reactome.domain.model.PhysicalEntity;
import uk.ac.ebi.reactome.repository.placeholder.PhysicalEntityRepository;

/**
 * Created by flo on 02.11.15.
 */

@Service
public class PhysicalEntityService {

    private static Logger logger = Logger.getLogger(PhysicalEntityService.class.getName());

    @Autowired
    private PhysicalEntityRepository physicalEntityRepository;

    public PhysicalEntity findByDbId(Long dbId){
        return physicalEntityRepository.findByDbId(dbId);
    }

    public PhysicalEntity getOrCreate(PhysicalEntity physicalEntity) {
        PhysicalEntity oldPhysicalEntity = physicalEntityRepository.findByDbId(physicalEntity.getDbId());
        if (oldPhysicalEntity == null) {
            return physicalEntityRepository.save(physicalEntity,0);

        }
        return physicalEntity;
    }




    }


//    public PhysicalEntity findByDbId(Long dbId){
//        return participantRepository.findByDbId(dbId);
//    }

//    public void createIndexOnStId(){
//        participantRepository.createIndexOnStId();
//    }


