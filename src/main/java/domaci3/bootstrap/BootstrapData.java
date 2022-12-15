package domaci3.bootstrap;

import com.sun.tools.javac.util.List;
import domaci3.domain.entities.Role;
import domaci3.domain.entities.RoleType;
import domaci3.domain.entities.User;
import domaci3.repository.RoleRepository;
import domaci3.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapData(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role roleCreate = new Role();
        roleCreate.setRole(RoleType.CAN_CREATE);

        Role roleRead = new Role();
        roleRead.setRole(RoleType.CAN_READ);

        Role roleUpdate = new Role();
        roleUpdate.setRole(RoleType.CAN_UPDATE);

        Role roleDelete = new Role();
        roleDelete.setRole(RoleType.CAN_DELETE);


        User userAdmin = new User();
        userAdmin.setFirstName("Admin");
        userAdmin.setLastName("Admin");
        userAdmin.setEmail("admin@admin.com");
        userAdmin.setPassword(passwordEncoder.encode("admin"));
        userAdmin.setRoles(List.of(roleCreate, roleRead, roleUpdate, roleDelete));

        userRepository.save(userAdmin);

        System.out.println("DATA LOADED!");
    }

}
