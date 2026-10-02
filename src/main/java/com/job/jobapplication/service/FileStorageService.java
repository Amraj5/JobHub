package com.job.jobapplication.service;

import com.job.jobapplication.exception.BusinessRuleException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    private static final long MAX_SIZE = 5 * 1024 * 1024L; // 5 MB
    private static final String CV_DIR = "uploads/cvs/";

    public String saveCv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Please select a PDF file to upload.");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessRuleException("File size must be under 5 MB.");
        }
        String original = file.getOriginalFilename();
        if (original == null || !original.toLowerCase().endsWith(".pdf")) {
            throw new BusinessRuleException("Only PDF files are allowed.");
        }

        try {
            Files.createDirectories(Paths.get(CV_DIR));
            String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
            String stored = System.currentTimeMillis() + "-" + safeName;
            Path target = Paths.get(CV_DIR + stored);
            file.transferTo(target.toAbsolutePath());
            return "/uploads/cvs/" + stored;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}