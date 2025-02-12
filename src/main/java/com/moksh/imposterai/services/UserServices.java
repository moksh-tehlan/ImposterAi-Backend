package com.moksh.imposterai.services;

import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.entities.UserEntity;
import com.moksh.imposterai.exceptions.ResourceNotFoundException;
import com.moksh.imposterai.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServices implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserEntity loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserEntity loadUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDto findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userEntity -> modelMapper.map(userEntity, UserDto.class))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDto findById(String id) {
        return userRepository.findById(id)
                .map(userEntity -> modelMapper.map(userEntity, UserDto.class))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void save(UserEntity user) {
        userRepository.save(user);
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    public UserEntity getBot() throws ResourceNotFoundException {
        return userRepository.findById("53d20640-33af-4e4b-b48f-b15599c7248a").orElseThrow(() -> new ResourceNotFoundException("Bot not found"));
    }
}
