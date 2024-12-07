package com.example.departorium.service;



import com.example.departorium.dto.CustomOAuth2UserDetails;
import com.example.departorium.dto.GoogleDTO;
import com.example.departorium.dto.OAuth2DTO;
import com.example.departorium.entity.UserEntity;
import com.example.departorium.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2DTO oAuth2DTO = null;

        if (registrationId.equals("google")) {

            oAuth2DTO = new GoogleDTO(oAuth2User.getAttributes());
        } else {

            return null;
        }

        String username = oAuth2DTO.getProvider() + oAuth2DTO.getProviderId();
        String nickname = oAuth2DTO.getName();
        String email = oAuth2DTO.getEmail();
        log.info(oAuth2User.toString());

        UserEntity existUser = userRepository.findByUsername(username).orElse(null);

        if (existUser == null) {
            UserEntity isUser = new UserEntity();
            isUser.setUsername(username);
            isUser.setNickname(nickname);
            isUser.setEmail(email);
            isUser.setUrl("C:\\Departorium\\Download");
            isUser.setRole("ROLE_USER");
            isUser.setStatus("ACTIVE");
            userRepository.save(isUser);

            return new CustomOAuth2UserDetails(isUser);
        } else {
            UserEntity newData = new UserEntity();
            newData.setNickname(nickname);
            newData.setEmail(email);

            existUser.patch(newData);
            userRepository.save(existUser);

            return new CustomOAuth2UserDetails(existUser);
        }
    }
}
