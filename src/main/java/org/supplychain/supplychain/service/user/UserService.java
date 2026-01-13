package org.supplychain.supplychain.service.user;

import java.util.List;

import org.supplychain.supplychain.dto.UserDTO;
import org.supplychain.supplychain.dto.UserResponse;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    UserDTO getUserByEmail(String email);

    List<UserResponse> getAllUsers();
}
