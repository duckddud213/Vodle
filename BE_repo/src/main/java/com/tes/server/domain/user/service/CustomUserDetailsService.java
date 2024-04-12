package com.tes.server.domain.user.service;


import com.tes.server.domain.user.detail.CustomUserDetails;
import com.tes.server.domain.user.entity.UserEntity;
import com.tes.server.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userCode) throws UsernameNotFoundException {

        //DB에서 조회
        UserEntity userData = userRepository.findByUserCode(userCode);

        if (userData != null) {
            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(userData);
        }
        return null;
    }
}