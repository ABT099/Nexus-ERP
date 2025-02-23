package com.nexus.file;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.project.Project;
import com.nexus.project.ProjectService;
import com.nexus.s3.S3Service;
import com.nexus.tenant.TenantContext;
import com.nexus.utils.UpdateHandler;

import org.springframework.http.MediaType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("files")
public class FileController {

    private final FileRepository fileRepository;
    private final FileService fileService;
    private final ProjectService projectService;
    private final FileMapper fileMapper;
    private final S3Service s3Service;

    public FileController(
            FileRepository fileRepository,
            FileService fileService,
            ProjectService projectService,
            FileMapper fileMapper,
            S3Service s3Service
    ) {
        this.fileRepository = fileRepository;
        this.fileService = fileService;
        this.projectService = projectService;
        this.fileMapper = fileMapper;
        this.s3Service = s3Service;
    }


    @GetMapping
    public ResponseEntity<List<BasicFileResponse>> getAllByTenant(
            @RequestParam(
                required = false,
                name = "a"
            ) ArchivableQueryType archived
    ) {
        List<File> files = ArchivedService.determine(archived, fileRepository);

        return ResponseEntity.ok(files.stream()
                .map(fileMapper::toBasicFileResponse)
                .toList());
    }

    @GetMapping("by-project/{id}")
    public ResponseEntity<List<BasicFileResponse>> getAllByProjectId(@Valid @Positive @PathVariable int id) {
        return ResponseEntity.ok(
                fileRepository.findAllByProjectId(id).stream()
                        .map(fileMapper::toBasicFileResponse)
                        .toList()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<FileResponse> getById(@Valid @Positive @PathVariable int id) {
        File file = fileService.findById(id);

        return ResponseEntity.ok(fileMapper.toFileResponse(file));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Integer> upload(@ModelAttribute UploadFileRequest request) {
        if (request.file().isEmpty()) {
            throw new ResourceNotFoundException("file not found");
        }

        try {
            File file = getFile(request);

            if (request.projectId() != null) {
                Project project = projectService.findById(request.projectId());

                project.addFile(file);
            }

            fileRepository.saveAndFlush(file);
            return ResponseEntity.created(URI.create("/files/" + file.getId())).body(file.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public void update(@Valid @RequestBody UpdateFileRequest request) {
        File file = fileService.findById(request.id());

        if (file.isArchived()) {
            throw new NoUpdateException("Archived file cannot be updated");
        }

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(file::getName, request.name(), file::setName);
            tracker.updateField(file::getDescription, request.description(), file::setDescription);
        }, () -> fileRepository.saveAndFlush(file));
    }

    @PatchMapping("archive/{id}")
    public void archive(@Valid @Positive @PathVariable int id) {
        fileRepository.archiveById(id);
    }

    @DeleteMapping("{id}")
    @Transactional(rollbackFor = Exception.class)
    public void delete(@Valid @Positive @PathVariable int id) {
        try {
            File file = fileService.findById(id);
            s3Service.deleteFile(file.getUrl());
            fileRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }

    private File getFile(UploadFileRequest request) throws IOException {
        String type = request.file().getContentType();
        String name;
        if (request.name().isEmpty()) {
            name = request.file().getOriginalFilename();
        } else {
            name = request.name();
        }

        byte[] fileContent = request.file().getBytes();

        var bucketName = UUID.randomUUID().toString();
        String fileUrl =  s3Service.uploadFile(bucketName, request.file().getContentType(), fileContent);

        return new File(
                name,
                request.description(),
                type,
                fileUrl,
                TenantContext.getTenantId()
        );
    }
}
