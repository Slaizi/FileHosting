package ru.Bogachev.fileHosting.web.mappers;

import org.mapstruct.Mapper;
import ru.Bogachev.fileHosting.domain.model.file.BootFile;
import ru.Bogachev.fileHosting.web.dto.file.BootFileDto;

@Mapper(componentModel = "spring")
public interface BootFileMapper extends Mappable<BootFile, BootFileDto> {
}
