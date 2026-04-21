package org.example.ormwithjpa.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.example.ormwithjpa.Dto.response.UserResponse;
import org.example.ormwithjpa.Model.User;
import org.example.ormwithjpa.Repository.UserRepository;
import org.example.ormwithjpa.Service.Interface.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse save(User user) {
        User newUser = userRepository.save(user);

        return new UserResponse(
                newUser.getId(),
                newUser.getUsername(),
                newUser.getFirstName(),
                newUser.getLastName(),
                newUser.getEmail()
        );
    }

    @Override
    public List<UserResponse> getAllUser() {
        return userRepository.findAll()
                .stream().map(
                        e -> new UserResponse(e.getId(),e.getUsername(),e.getFirstName(),e.getLastName(),e.getEmail())

                ).collect(Collectors.toList());
    }

    @Override
    public UserResponse getUser(Long id) {
        User user = userRepository.findById(id).get();
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}
