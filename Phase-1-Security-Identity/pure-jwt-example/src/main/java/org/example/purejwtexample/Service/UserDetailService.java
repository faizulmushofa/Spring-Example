package org.example.purejwtexample.Service;

import lombok.AllArgsConstructor;
import org.example.purejwtexample.Model.CustomUserDetails;
import org.example.purejwtexample.Model.User;
import org.example.purejwtexample.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User Not Found")
                );


        return new CustomUserDetails(
                user.getUsername()
                ,user.getPassword());

    }
}
