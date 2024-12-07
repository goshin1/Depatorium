package com.example.departorium.service;

import com.example.departorium.dto.ChatDTO;
import com.example.departorium.entity.*;
import com.example.departorium.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ChattingService {
    @Autowired
    private UserService userService;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private DepartRepository departRepository;
    @Autowired
    private MemberRepository memberRepository;

    public ChatEntity saveProcess(String refresh, Long project_id, Long depart_id, ChatDTO chatDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);
        log.info(isMember.toString());

        if (!isMember.getDepart().getId().equals(depart_id)) {
            System.out.println("오류 1");
            return null;
        }

        DepartEntity isDepart = departRepository.findById(depart_id).orElse(null);
        log.info(isDepart.toString());

        String content = chatDTO.getContent();
        String type = chatDTO.getType();
        log.info(chatDTO.toString());

        ChatEntity isChat = new ChatEntity();
        isChat.setContent(content);
        isChat.setLog(LocalDateTime.now());
        isChat.setType(type);
        isChat.setMember(isMember);
        isChat.setDepart(isDepart);
        ChatEntity saved = chatRepository.save(isChat);
        log.info(saved.toString());

        return saved;
    }

    // 이전 채팅 내역 목록.
    public List<ChatEntity> listProcess(Long depart_id) {
        List<ChatEntity> chatList = chatRepository.findAllByDepart_id(depart_id);
        log.info(chatList.toString());

        return chatList;
    }

//    public File upload(FileDto fileDto, MultipartFile file) throws IllegalAccessException, IOException {
//        ProjectDepartEntity departEntity = projectDepartRepository.findById(fileDto.getRoom_id())
//                .orElseThrow(() -> new IllegalAccessException("해당 채팅방이 없습니다."));
//
//        String projectPath = System.getProperty("user.dir") + "/images/";
//
//        UUID uuid = UUID.randomUUID();
//        String filename = uuid + "_" + file.getOriginalFilename();
//        File saveFile = new File(projectPath, filename);
//        file.transferTo(saveFile);
//        fileDto.setFile_size(saveFile.length());
//        fileDto.setFile_name(filename);
//        fileDto.setFile_path("/webapp/"+filename);
//
//        FileEntity fileEntity = new FileEntity(null, fileDto.getFile_type(), fileDto.getFile_name(), fileDto.getFile_path(), fileDto.getFile_size(), departEntity);
//        fileRepository.save(fileEntity);
//
//        return saveFile;
//
//    }
}
