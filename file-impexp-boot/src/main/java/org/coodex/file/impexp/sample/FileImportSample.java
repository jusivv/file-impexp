package org.coodex.file.impexp.sample;

import org.coodex.file.impexp.helper.ImportStream;
import org.coodex.filerepository.api.FileMetaInf;
import org.coodex.filerepository.api.IFileRepository;
import org.coodex.filerepository.local.LocalFileRepository;
import org.coodex.util.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component("impSample")
public class FileImportSample implements ImportStream<ImpExpParam> {
    private static Profile profile = Profile.get("config.properties");

    @Override
    public ImpExpParam read(InputStream inputStream, String fileName, long fileSize, String contentType) {
        FileMetaInf fileMetaInf = new FileMetaInf();
        fileMetaInf.setFileName(fileName.substring(0, fileName.lastIndexOf('.')));
        fileMetaInf.setExtName(fileName.substring(fileName.lastIndexOf('.') + 1));
        fileMetaInf.setFileSize(fileSize);
        fileMetaInf.setClientId(profile.getString("clientId", "client"));
        IFileRepository fileRepository = new LocalFileRepository(
                profile.getString("workspace", "~/Downloads/temp"));
        String fileId = fileRepository.save(inputStream, fileMetaInf);
        ImpExpParam param = new ImpExpParam();
        param.setFileId(fileId);
        return param;
    }
}
