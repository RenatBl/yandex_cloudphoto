package service;

public interface YandexCloudService {

    void uploadFile(String path, String album);

    void downloadFile(String path, String album);

    void getAlbums();

    void getFiles(String album);

}
