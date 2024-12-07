package com.example.departorium.service;

import com.example.departorium.dto.CheckDTO;
import com.example.departorium.dto.MemberDTO;
import com.example.departorium.dto.ProjectDTO;
import com.example.departorium.dto.UserDTO;
import com.example.departorium.entity.*;
import com.example.departorium.repository.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final DepartRepository departRepository;
    private final MemberRepository memberRepository;

    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder, TokenRepository tokenRepository, UserRepository userRepository, ProjectRepository projectRepository, DepartRepository departRepository, MemberRepository memberRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.departRepository = departRepository;
        this.memberRepository = memberRepository;
    }

    // 로그인 유저의 프로필 정보.
    public UserEntity userProcess(String refresh) {
        if (refresh == null) {
            return null;
        }
        System.out.println("refresh : " + refresh);

        TokenEntity isToken = tokenRepository.findByRefresh(refresh);
        log.info(isToken.toString());
        UserEntity isUser = userRepository.findById(isToken.getUser().getId()).orElse(null);
        log.info(isUser.toString());

        return isUser;
    }

    // 로그인 유저의 프로필 수정.
    public UserEntity updateProcess(String refresh, UserDTO userDTO) {
        UserEntity oldProfile = userProcess(refresh);
        log.info(oldProfile.toString());
        System.out.println("==================");
        System.out.println(userDTO.toString());
        String nickname = userDTO.getNickname();
        String email = userDTO.getEmail();
        LocalDate birth = userDTO.getBirth();
        String phone = userDTO.getPhone();
        String group = userDTO.getGroup();
        String theme = userDTO.getTheme();
        String url = userDTO.getUrl();
        log.info(userDTO.toString());

        UserEntity newProfile = new UserEntity();
        newProfile.setNickname(nickname);
        newProfile.setEmail(email);
        newProfile.setBirth(birth);
        newProfile.setPhone(phone);
        newProfile.setGroup(group);
        newProfile.setTheme(theme);
        newProfile.setUrl((url));
        log.info(newProfile.toString());

        oldProfile.patch(newProfile);
        System.out.println("----------------------");
        System.out.println(oldProfile.toString());
        UserEntity updated = userRepository.save(oldProfile);
        log.info(updated.toString());

        return updated;
    }

    // 로그인 한 유저의 멤버 정보.
    public MemberEntity memberProcess(String refresh, Long project_id) {
        if (refresh == null) {
            return null;
        }

        TokenEntity isToken = tokenRepository.findByRefresh(refresh);
        log.info(isToken.toString());
        UserEntity isUser = userRepository.findById(isToken.getUser().getId()).orElse(null);
        log.info(isUser.toString());
        ProjectEntity isProject = projectRepository.findById(project_id).orElse(null);
        log.info(isProject.toString());
        MemberEntity isMember = memberRepository.findByUser_IdAndProject_Id(isUser.getId(), isProject.getId());
        log.info(isMember.toString());

        return isMember;
    }



    // 각 프로젝트의 활동/비활동 유저 목록
    public List<MemberEntity> projectUserListProcess(Long project_id) {
        ProjectEntity isProject = projectRepository.findById(project_id).orElse(null);
        log.info(isProject.toString());
        List<MemberEntity> memberList = memberRepository.findAllByProject_IdOrderByStatus(isProject.getId());
        log.info(memberList.toString());


        return memberList;
    }

    // 유저의 초대코드 입력.
    public MemberEntity inviteProcess(String refresh, ProjectDTO projectDTO) {
        String invitation = projectDTO.getInvitation();
        log.info(projectDTO.toString());

        UserEntity isUser = userProcess(refresh);
        log.info(isUser.toString());

        ProjectEntity isProject = projectRepository.findByInvitation(invitation);

        Boolean existsMember = memberRepository.existsByUser_IdAndProject_Id(isUser.getId(), isProject.getId());

        if (existsMember) {
            return null;
        }

        MemberEntity waitMember = new MemberEntity();
        waitMember.setRole("WAITING");
        waitMember.setStatus("INACTIVE");
        waitMember.setUser(isUser);
        waitMember.setDepart(null);
        waitMember.setProject(isProject);
        MemberEntity saved = memberRepository.save(waitMember);
        log.info(saved.toString());

        return saved;
    }

    // 초대코드 입력한 유저 직무 배정.
    public MemberEntity assignProcess(String refresh, Long project_id, MemberDTO memberDTO) {
        MemberEntity isMember = memberProcess(refresh, project_id);
        log.info(isMember.toString());

        // 프로젝트 대표인지 검증.
        if (!isMember.getRole().equals("LEADER")) {
            return null;
        }

        Long member_id = memberDTO.getId();
        String role = memberDTO.getRole();
        Long depart_id = memberDTO.getDepart_id();

        // 직무를 배정할 팀원.
        MemberEntity checkMember = memberRepository.findById(member_id).orElse(null);
        log.info(checkMember.toString());

        // 배정할 직무회의실.
        DepartEntity isDepart = departRepository.findById(depart_id).orElse(null);
        log.info(isDepart.toString());

        MemberEntity assignMember = new MemberEntity();

        if (checkMember.getStatus().equals("INACTIVE")) {
            assignMember.setRole(role);
            assignMember.setStatus("ACTIVE");
            assignMember.setDepart(isDepart);
        } else {
            assignMember.setRole(role);
            assignMember.setDepart(isDepart);
        }
        log.info(assignMember.toString());

        checkMember.patch(assignMember);
        MemberEntity assigned = memberRepository.save(checkMember);
        log.info(assigned.toString());

        return assigned;
    }

    public UserEntity checkUser(String refresh, CheckDTO checkDTO) {

        UserEntity oldUser = userProcess(refresh);
        String username = checkDTO.getUsername();
        if(oldUser.getUsername().equals(username)){
            System.out.println("인증 성공");
            return oldUser;
        }
        System.out.println("인증 실패");
        return null;
    }
}
