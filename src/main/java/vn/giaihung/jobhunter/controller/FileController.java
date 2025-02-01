package vn.giaihung.jobhunter.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.giaihung.jobhunter.domain.dto.response.folder.FileUploadResDTO;
import vn.giaihung.jobhunter.service.FileService;
import vn.giaihung.jobhunter.utils.annotation.ApiMessage;
import vn.giaihung.jobhunter.utils.error.FileStorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${upload-file.base-path}")
    private String baseURI;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @SuppressWarnings("null")
    @PostMapping("/files")
    @ApiMessage("Upload file")
    public ResponseEntity<FileUploadResDTO> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, FileStorageException {
        // Validate (empty file, extensions and file size)
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File upload cant' be empty");
        }

        String originalFileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isExtensionValid = allowedExtensions.stream()
                .anyMatch(extension -> originalFileName.endsWith(extension));

        if (!isExtensionValid) {
            throw new FileStorageException(
                    "File extension must be in the following list: " + allowedExtensions.toString());
        }

        // Limit file size = 5MB
        long maxSize = 1024 * 1024 * 5;
        if (file.getSize() > maxSize) {
            throw new FileStorageException("File size must not exceed 5MB");
        }

        // Create directory if not exist
        fileService.createUploadFolder(baseURI + folder);

        // Upload file
        String fileName = fileService.storeFile(file, folder);

        // Response
        FileUploadResDTO fileUploadResDTO = new FileUploadResDTO();
        fileUploadResDTO.setFileName(fileName);
        fileUploadResDTO.setUploadedAt(Instant.now());

        return ResponseEntity.status(HttpStatus.OK).body(fileUploadResDTO);
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder

    ) throws FileStorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new FileStorageException("Missing required params (filename or folder) in query param");
        }

        String uriLink = baseURI + folder + "/" + fileName;
        long fileLength = fileService.getFileLength(uriLink);
        if (fileLength == 0) {
            throw new FileStorageException("File with name = " + fileName + " not found.");
        }

        // Download a file
        Resource resource = fileService.getResource(uriLink);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
