package edu.tcu.cs.hogwartsartifactsonline.wizard;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WizardRepository extends JpaRepository<Wizard, Integer> {

}
