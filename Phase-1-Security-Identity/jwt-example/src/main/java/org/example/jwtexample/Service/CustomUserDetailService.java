package org.example.jwtexample.Service;

import lombok.RequiredArgsConstructor;
import org.example.jwtexample.Model.CustomUserDetails;
import org.example.jwtexample.Model.User;
import org.example.jwtexample.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User Not Found"));

        List<GrantedAuthority> auths = mapAuthorities(user);

        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                auths
        );
    }

    private List<GrantedAuthority> mapAuthorities(User user) {
        return user.getUserRoles().stream()
                //menggunakan flatmap agar semua permission role di jadikan satu
                .flatMap(userRole ->
                        userRole.getRole()
                                .getRolePermissions()
                                .stream()
                )
                .map(rolePermission ->
                        rolePermission.getPermission().getCode()
                )
                .distinct()
                .map( code -> (GrantedAuthority)new SimpleGrantedAuthority(code))
                .toList();
    }
}
