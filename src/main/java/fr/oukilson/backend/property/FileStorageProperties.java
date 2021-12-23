package fr.oukilson.backend.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

//Custom properties to say to spring where icons downloaded folder is.
@ConfigurationProperties("icon")
public class FileStorageProperties {

    private String uploadDir;

    public String getUploadDir(){
        return uploadDir;
    }

    public void setUploadDir(String uploadDir){
        this.uploadDir = uploadDir;
    }
}
