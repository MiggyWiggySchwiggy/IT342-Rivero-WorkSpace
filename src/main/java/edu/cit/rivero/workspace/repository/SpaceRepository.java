package edu.cit.rivero.workspace.repository;

import edu.cit.rivero.workspace.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRepository extends JpaRepository<Space, String> {
}
