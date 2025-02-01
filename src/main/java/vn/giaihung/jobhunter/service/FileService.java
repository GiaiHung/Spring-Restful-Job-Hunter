package vn.giaihung.jobhunter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    @Value("${upload-file.base-path}")
    private String baseURI;

    public void createUploadFolder(String folderURI) throws URISyntaxException {
        URI uri = new URI(folderURI);
        Path path = Paths.get(uri);
        File tempDir = new File(path.toString());

        if (!tempDir.isDirectory()) {
            try {
                Files.createDirectory(tempDir.toPath());
                System.out.println(">>> NEW DIRECTORY HAS BEEN CREATED. PATH = " + tempDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY => ALREADY EXISTS");
        }
    }

    public String storeFile(MultipartFile file, String folder) throws IOException, URISyntaxException {
        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }

    public long getFileLength(String uriLink) throws URISyntaxException {
        URI uri = new URI(uriLink);
        Path path = Paths.get(uri);
        File tempDir = new File(path.toString());

        if (!tempDir.exists() || tempDir.isDirectory()) {
            return 0;
        }

        return tempDir.length();
    }

    public Resource getResource(String uriLink)
            throws URISyntaxException, FileNotFoundException {
        URI uri = new URI(uriLink);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }
}
