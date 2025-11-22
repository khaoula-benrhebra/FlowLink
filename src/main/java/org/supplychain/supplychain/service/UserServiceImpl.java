package org.supplychain.supplychain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.UserDTO;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.UserMapper;
import org.supplychain.supplychain.model.AppUser;
import org.supplychain.supplychain.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", userDTO.getEmail());
        }

        AppUser user = userMapper.toEntity(userDTO);
        AppUser savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        AppUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.findByEmail(userDTO.getEmail())
                .ifPresent(user -> {
                    if (!user.getIdUser().equals(id)) {
                        throw new DuplicateResourceException("User", "email", userDTO.getEmail());
                    }
                });

        userMapper.updateEntityFromDTO(userDTO, existingUser);
        AppUser updatedUser = userRepository.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toDTO(user);
    }
}