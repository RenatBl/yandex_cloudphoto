package util;

import com.beust.jcommander.Parameter;
import lombok.Data;

@Data
public class Args {

    @Parameter(names  = {"upload"}, description = "Uploading flag")
    private Boolean isUpload = false;

    @Parameter(names  = {"download"}, description = "Downloading flag")
    private Boolean isDownload = false;

    @Parameter(names  = {"list"}, description = "Getting flag")
    private Boolean isGetting = false;

    @Parameter(names  = {"-p", "--path"}, description = "Catalog")
    private String path;

    @Parameter(names  = {"-a","--album"}, description = "Album")
    private String album;
}
