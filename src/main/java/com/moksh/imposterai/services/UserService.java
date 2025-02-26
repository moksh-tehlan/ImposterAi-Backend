package com.moksh.imposterai.services;

import com.moksh.imposterai.dtos.UserDto;
import com.moksh.imposterai.entities.UserEntity;
import com.moksh.imposterai.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

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

    public UserEntity createBot() {
        UserEntity bot = UserEntity.builder()
                .username("Imposter-Bot-"+ UUID.randomUUID())
                .password("$2a$10$yLRl9emvq75kvSQ1ONCjY.I0/Hn3zZlAccYv2GPn1ZefWSjgpnPSC")
                .build();
        return userRepository.save(bot);
    }

    public void delete(String botUserId) {
        userRepository.deleteById(botUserId);
    }
}
