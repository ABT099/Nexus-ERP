package com.nexus.file;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
import com.nexus.project.Project;
import com.nexus.project.ProjectFinder;
import com.nexus.utils.UpdateHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("files")
public class FileController {

    private final FileRepository fileRepository;
    private final FileService fileService;
    private final ProjectFinder projectFinder;
    private final FileMapper fileMapper;
    private final MonitorManager monitorManager;

    public FileController(
            FileRepository fileRepository,
            FileService fileService,
            ProjectFinder projectFinder,
            FileMapper fileMapper,
            MonitorManager monitorManager
    ) {
        this.fileRepository = fileRepository;
        this.fileService = fileService;
        this.projectFinder = projectFinder;
        this.fileMapper = fileMapper;
        this.monitorManager = monitorManager;
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

    @PostMapping
    public ResponseEntity<Integer> upload(@Valid @RequestBody UploadFileRequest request) {
        if (request.file().isEmpty()) {
            throw new ResourceNotFoundException("file not found");
        }

        try {
            File file = getFile(request);

            if (request.projectId() != null) {
                Project project = projectFinder.findById(request.projectId());

                project.addFile(file);
            }

            fileRepository.saveAndFlush(file);

            monitorManager.monitor(file, ActionType.CREATE);

            return ResponseEntity.created(URI.create("/files/" + file.getId())).body(file.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public void update(@Valid @RequestBody UpdateFileRequest request) {
        File file = fileService.findById(request.id());

        UpdateHandler.updateEntity(file, tracker -> {
            tracker.updateField(file::getName, request.name(), file::setName);
            tracker.updateField(file::getDescription, request.description(), file::setDescription);
        }, () -> fileRepository.saveAndFlush(file), monitorManager);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        // delete the file from the storage, ie: s3 or any else
        File file = fileService.findById(id);

        fileRepository.delete(file);

        monitorManager.monitor(file, ActionType.DELETE);
    }

    private static File getFile(UploadFileRequest request) throws IOException {
        String type = request.file().getContentType();
        String name;
        if (request.name().isEmpty()) {
            name = request.file().getOriginalFilename();
        } else {
            name = request.name();
        }

        byte[] fileContent = request.file().getBytes();

        // Add your file handling logic here
        String fileUrl = "";

        return new File(
                name,
                request.description(),
                type,
                fileUrl
        );
    }
}
