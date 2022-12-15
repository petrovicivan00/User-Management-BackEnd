package domaci3.repository;

import domaci3.domain.entities.Role;
import domaci3.domain.entities.RoleType;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface RoleRepository extends JpaRepositoryImplementation<Role, Long> {

    List<Role> findAllByRoleIn(List<RoleType> roles);

}
