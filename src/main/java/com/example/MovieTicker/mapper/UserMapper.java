package com.example.MovieTicker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.MovieTicker.entity.User;
import com.example.MovieTicker.request.UserCreationRequest;
import com.example.MovieTicker.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);

    // @Mapping(target = "id", ignore = true)
    User toUser(UserCreationRequest request);

    // @Mapping(target = "id", ignore = true)
    void updateUser(@MappingTarget User user, UserCreationRequest request);
}
