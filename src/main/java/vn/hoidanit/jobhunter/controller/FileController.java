package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.jobhunter.domain.response.file.ResUploadFileDto;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.StorageException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final FileService fileService;

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDto> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, StorageException {
        //validate
        if (file == null || file.isEmpty()){
            throw new StorageException("File is empty.Please upload file.");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid){
            throw new StorageException("Invalid file extension. Only allow " + allowedExtensions.toString());
        }
        //create directory if not exist
        this.fileService.createUploadFolder(baseURI + folder);
        //store file
        String uploadFile = this.fileService.store(file, folder);
        ResUploadFileDto resUploadFileDto = new ResUploadFileDto(uploadFile, Instant.now());
        return ResponseEntity.ok().body(resUploadFileDto);
    }

    @GetMapping("/files")
    @ApiMessage("Download file")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder
    ) throws URISyntaxException, IOException, StorageException {
        if (fileName == null || folder == null){
            throw new StorageException("Missing required parameters : (file or folder)");
        }
        //check file exist(and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0){
            throw new StorageException("File with name " + fileName + " not found.");
        }
        //download File
        InputStreamResource resource = this.fileService.getResource(fileName, folder);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
