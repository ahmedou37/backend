package com.example.demo.mappers;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-18T07:24:06+0000",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setEmail( user.getEmail() );
        userDTO.setId( user.getId() );
        userDTO.setImageName( user.getImageName() );
        userDTO.setName( user.getName() );
        userDTO.setRole( user.getRole() );
        List<Task> list = user.getTasks();
        if ( list != null ) {
            userDTO.setTasks( new ArrayList<Task>( list ) );
        }

        return userDTO;
    }

    @Override
    public User toEntity(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( userDTO.getEmail() );
        user.setId( userDTO.getId() );
        user.setImageName( userDTO.getImageName() );
        user.setName( userDTO.getName() );
        user.setRole( userDTO.getRole() );
        List<Task> list = userDTO.getTasks();
        if ( list != null ) {
            user.setTasks( new ArrayList<Task>( list ) );
        }

        return user;
    }

    @Override
    public List<UserDTO> toDtoList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDTO> list = new ArrayList<UserDTO>( users.size() );
        for ( User user : users ) {
            list.add( toDto( user ) );
        }

        return list;
    }

    @Override
    public List<User> toEntityList(List<UserDTO> users) {
        if ( users == null ) {
            return null;
        }

        List<User> list = new ArrayList<User>( users.size() );
        for ( UserDTO userDTO : users ) {
            list.add( toEntity( userDTO ) );
        }

        return list;
    }
}
