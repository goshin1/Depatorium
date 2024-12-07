package com.example.departorium.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UploadService {

    public File write(MultipartFile file) throws IOException, IllegalAccessException {


        String projectPath = System.getProperty("user.dir") + "/images/";

        UUID uuid = UUID.randomUUID();
        String filename = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(projectPath, filename);
        file.transferTo(saveFile);

        return saveFile;
    }

}
