package ru.Bogachev.fileHosting.web.mappers;

import org.mapstruct.Mapper;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.web.dto.user.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserDto> {
}
