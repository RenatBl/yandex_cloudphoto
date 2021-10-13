package service.impl;

import org.springframework.util.ObjectUtils;
import service.CommandsProcessingService;
import service.YandexCloudService;
import util.Args;

public class CommandsProcessingServiceImpl implements CommandsProcessingService {

    private final YandexCloudService yandexCloudService = new YandexCloudServiceImpl();

    @Override
    public void process(Args args) {
        if (Boolean.TRUE.equals(args.getIsUpload())) {
            yandexCloudService.uploadFile(args.getPath(), args.getAlbum());
        } else if (Boolean.TRUE.equals(args.getIsDownload())) {
            yandexCloudService.downloadFile(args.getPath(), args.getAlbum());
        } else {
            getList(args);
        }
    }

    private void getList(Args args) {
        if (ObjectUtils.isEmpty(args.getAlbum())) {
            yandexCloudService.getAlbums();
        } else {
            yandexCloudService.getFiles(args.getAlbum());
        }
    }
}
