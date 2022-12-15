package domaci3.domain.mapper;

import domaci3.domain.dto.user.UserCreateDto;
import domaci3.domain.dto.user.UserDto;
import domaci3.domain.dto.user.UserUpdateDto;
import domaci3.domain.entities.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userRoles", source = "user", qualifiedByName = "getUserRoles")
    UserDto userToUserDto(User user);

    User userCreateDtoToUser(UserCreateDto userCreateDto);

    User updateUser(@MappingTarget User user, UserUpdateDto userUpdateDto);

    @Named("getUserRoles")
    default List<String> getUserRoles(User user) {
        return user.getRoles().stream().map(role -> role.getRole().name()).collect(Collectors.toList());
    }

}
