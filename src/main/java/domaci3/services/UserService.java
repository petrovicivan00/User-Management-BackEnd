package domaci3.services;

import domaci3.domain.dto.user.UserCreateDto;
import domaci3.domain.dto.user.UserDto;
import domaci3.domain.dto.user.UserUpdateDto;
import domaci3.domain.entities.Role;
import domaci3.domain.entities.RoleType;
import domaci3.domain.entities.User;
import domaci3.domain.mapper.UserMapper;
import domaci3.repository.RoleRepository;
import domaci3.repository.UserRepository;
import domaci3.utils.PermissionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(UserCreateDto userCreateDto) {

        if (!PermissionUtils.hasPermission(RoleType.CAN_CREATE))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, PermissionUtils.permissionMessage);

        List<Role> roles = roleRepository.findAllByRoleIn(userCreateDto.getUserRoles().stream().map(RoleType::valueOf).collect(Collectors.toList()));

        User user = UserMapper.INSTANCE.userCreateDtoToUser(userCreateDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);

        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
    }

    public List<UserDto> findAllUsers() {

        if (!PermissionUtils.hasPermission(RoleType.CAN_READ))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, PermissionUtils.permissionMessage);

        return userRepository.findAll().stream().map(UserMapper.INSTANCE::userToUserDto).collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {

        if (!PermissionUtils.hasPermission(RoleType.CAN_READ))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, PermissionUtils.permissionMessage);

        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper.INSTANCE::userToUserDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid user id"));
    }

    public UserDto findUserByEmail(String email) {

        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserMapper.INSTANCE::userToUserDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid email"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> myUser = userRepository.findByEmail(email);
        if (!myUser.isPresent())
            throw new UsernameNotFoundException("Username " + email + " not found");

        List<RoleType> roles = myUser.get().getRoles().stream().map(Role::getRole).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(myUser.get().getEmail(), myUser.get().getPassword(), roles);
    }

    public UserDto updateUserById(Long id, UserUpdateDto userUpdateDto) {

        if (!PermissionUtils.hasPermission(RoleType.CAN_UPDATE))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, PermissionUtils.permissionMessage);

        List<Role> roles = roleRepository.findAllByRoleIn(userUpdateDto.getUserRoles().stream().map(RoleType::valueOf).collect(Collectors.toList()));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid id"));

        user = UserMapper.INSTANCE.updateUser(user, userUpdateDto);
        user.setRoles(roles);

        return UserMapper.INSTANCE.userToUserDto(userRepository.save(user));
    }

    public void deleteUserById(Long id) {

        if (!PermissionUtils.hasPermission(RoleType.CAN_DELETE))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, PermissionUtils.permissionMessage);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid id"));

        userRepository.delete(user);
    }
}
