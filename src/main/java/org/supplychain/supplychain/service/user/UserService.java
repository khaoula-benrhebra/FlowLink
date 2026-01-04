package org.supplychain.supplychain.service.user;

import org.supplychain.supplychain.dto.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    UserDTO getUserByEmail(String email);
}
