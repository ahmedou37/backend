package com.example.demo.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.model.User;
import com.example.demo.model.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);


    List<UserDTO> toDtoList(List<User> users);
    
    List<User> toEntityList(List<UserDTO> users);

}
