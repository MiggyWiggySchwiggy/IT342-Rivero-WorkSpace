package edu.cit.rivero.workspace.features.space;
import edu.cit.rivero.workspace.features.auth.*;
import edu.cit.rivero.workspace.features.space.*;
import edu.cit.rivero.workspace.features.reservation.*;
import edu.cit.rivero.workspace.features.reservation.strategy.*;
import edu.cit.rivero.workspace.common.*;
import edu.cit.rivero.workspace.security.*;


import edu.cit.rivero.workspace.features.space.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRepository extends JpaRepository<Space, String> {
}
