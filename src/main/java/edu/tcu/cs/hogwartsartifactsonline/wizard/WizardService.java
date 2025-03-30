package edu.tcu.cs.hogwartsartifactsonline.wizard;

import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactRepository;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
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
        return this.wizardRepository.findById(wizardId).orElseThrow(()->new ObjectNotFoundException("wizard", wizardId));
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
        }).orElseThrow(()->new ObjectNotFoundException("wizard", wizardId));


    }

    public void delete(Integer wizardId){
        Wizard wizardToBeDeleted = this.wizardRepository.findById(wizardId).orElseThrow(()->new ObjectNotFoundException("wizard", wizardId));
        //before deletion, must unassign wizard's owned artifacts
            wizardToBeDeleted.removeAllArtifacts();
        this.wizardRepository.deleteById(wizardId);
    }

    public void assignArtifact(Integer wizardId, String artifactId){
        //find artifact by id from the database

        Artifact artifactToBeAssigned = this.artifactRepository.findById(artifactId).orElseThrow(()->new ObjectNotFoundException("artifact", artifactId));

        //find wizard by id from the database
         Wizard wizard = this.wizardRepository.findById(wizardId).orElseThrow(()->new ObjectNotFoundException("wizard", wizardId));


        //artifact assignment

        //We need to see if the artifact is already ownerd by some wizard
        if(artifactToBeAssigned.getOwner() !=null){
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        }
        wizard.addArtifact(artifactToBeAssigned);


    }


}
