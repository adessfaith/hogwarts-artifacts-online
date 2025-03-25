package edu.tcu.cs.hogwartsartifactsonline.artifact;


import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;


//every method in this class is in its own transaction
@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;

    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId){
        return
                this.artifactRepository.findById(artifactId).orElseThrow(()->new ArtifactNotFoundException(artifactId));
    }

    public List<Artifact> findAll(){
        return this.artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact){
        newArtifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact newArtifact){
       return this.artifactRepository.findById(artifactId).map(oldArtifact->{
           oldArtifact.setName(newArtifact.getName());
           oldArtifact.setDescription(newArtifact.getDescription());
           oldArtifact.setImageUrl(newArtifact.getImageUrl());
           Artifact updatedArtifact = this.artifactRepository.save(oldArtifact);
           return updatedArtifact;
       })
               .orElseThrow(()->new ArtifactNotFoundException(artifactId));


    }

    public void delete(String artifactId){
       // this.artifactRepository.deleteById(artifactId);
        this.artifactRepository.findById(artifactId).orElseThrow(()->new ArtifactNotFoundException(artifactId));
        this.artifactRepository.deleteById(artifactId);

    }

}
