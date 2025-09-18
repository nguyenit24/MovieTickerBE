package com.example.MovieTicker.request;

import java.time.LocalDate;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    String username;
    String password;    
    String firstName;
    String lastName;
    LocalDate dob;
}