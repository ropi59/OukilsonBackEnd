package fr.oukilson.backend.service;

import fr.oukilson.backend.entity.Icon;
import fr.oukilson.backend.exception.MyFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import fr.oukilson.backend.exception.FileStorageException;
import fr.oukilson.backend.payload.UploadFileResponse;
import fr.oukilson.backend.property.FileStorageProperties;
import fr.oukilson.backend.repository.IconRepository;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class IconService {

    /**
     * Path definition
     */
    private Path fileStorageLocation;

    private IconRepository iconRepository;
    private ModelMapper mapper;

    public IconService iconService(
            FileStorageProperties fileStorageProperties,
            IconRepository iconRepository,
            ModelMapper mapper){
        this.iconRepository = iconRepository;
        this.mapper = mapper;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try{
            Files.createDirectories(this.fileStorageLocation);
        }catch (Exception e){
            throw new FileStorageException("Could not create directory where uploaded files will be stored", e);
        }
    }

    /**
     * save file on server method
     * @param icon
     * @return
     */
    public UploadFileResponse storeFile(MultipartFile icon){
        //file name normalization
        String fileName = StringUtils.cleanPath(icon.getOriginalFilename());

        try{
            //check if file's name contains invalid characters
            if(fileName.contains("..")){
                throw new FileStorageException("Filename contains invalid characters " + fileName);
            }
            //copy file to the target location, replacing existing file with same name
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(icon.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            UploadFileResponse response = this.getUploadFileResponse(icon, fileName);
            this.save(response);
            return response;
        } catch (IOException e){
            throw new FileStorageException("Could not record file " + fileName + ". Please try again.", e);
        }
    }

    /**
     * function to get icon on server
     * @param fileName
     * @return
     */
    public Resource loadFileAsResource(String fileName){
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()){
                return resource;
            }else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        }catch (MalformedURLException e){
            throw new MyFileNotFoundException("File not found " + fileName);
        }
    }

    /**
     * format UploadFileResponse function
     * @param icon
     * @param fileName
     * @return
     */
    private UploadFileResponse getUploadFileResponse(MultipartFile icon, String fileName){
        //uri definition for download
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        return new UploadFileResponse(fileName, fileDownloadUri, icon.getContentType(), icon.getSize());
    }

    /**
     * save icon in BDD
     * @param icon
     * @return
     */
    public UploadFileResponse save(UploadFileResponse icon){
        Icon iconToSave = this.mapper.map(icon, Icon.class);
        Icon iconSaved = this.iconRepository.save(iconToSave);
        return this.mapper.map(iconSaved, UploadFileResponse.class);
    }

    /**
     * find all icons in list
     * @return
     */
    public List<UploadFileResponse> findAll(){
        List<UploadFileResponse> uploadFileResponseList = new ArrayList<>();
        this.iconRepository.findAll().forEach(icon ->{
            uploadFileResponseList.add(mapper.map(icon, UploadFileResponse.class));
        });
        return uploadFileResponseList;
    }





}
