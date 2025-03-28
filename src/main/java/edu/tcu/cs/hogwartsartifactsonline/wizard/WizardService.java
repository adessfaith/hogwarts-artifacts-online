package edu.tcu.cs.hogwartsartifactsonline.wizard;

import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactRepository;
import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {
    private final WizardRepository wizardRepository;
    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }
    public List<Wizard> findAll(){
        return wizardRepository.findAll();
    }
    public Wizard findById(Integer wizardId){
        return this.wizardRepository.findById(wizardId).orElseThrow(()->new WizardNotFoundException(wizardId));
    }
    public Wizard save( Wizard newWizard){
        return this.wizardRepository.save(newWizard);
    }

    public Wizard update(Integer wizardId, Wizard update){
        return this.wizardRepository.findById(wizardId).map(oldWizard->{
            oldWizard.setName(update.getName());
            oldWizard.setArtifacts(update.getArtifacts());
            Wizard updatedWizard = this.wizardRepository.save(oldWizard);
            return oldWizard;
        }).orElseThrow(()->new WizardNotFoundException(wizardId));


    }

    public void delete(Integer wizardId){
        this.wizardRepository.findById(wizardId).orElseThrow(()->new WizardNotFoundException(wizardId));
        this.wizardRepository.deleteById(wizardId);
    }


}
