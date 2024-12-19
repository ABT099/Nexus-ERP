package com.nexus.file;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File findById(int id) {
        return fileRepository.findById(id)
            .orElseThrow(
                    () -> new ResourceNotFoundException("File not found with id: " + id)
            );
    }
}
