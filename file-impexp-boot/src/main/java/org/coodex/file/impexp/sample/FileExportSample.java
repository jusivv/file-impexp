package org.coodex.file.impexp.sample;

import com.alibaba.fastjson.JSON;
import org.coodex.file.impexp.helper.ExportStream;
import org.coodex.filerepository.api.IFileRepository;
import org.coodex.filerepository.api.StoredFileMetaInf;
import org.coodex.filerepository.local.LocalFileRepository;
import org.coodex.util.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component("expSample")
public class FileExportSample implements ExportStream<ImpExpParam> {
    private static Logger log = LoggerFactory.getLogger(FileExportSample.class);

    private static Profile profile = Profile.get("config.properties");

    private IFileRepository fileRepository = new LocalFileRepository(profile.getString("workspace", "~/Downloads/temp"));

    @Override
    public void write(OutputStream outputStream, ImpExpParam queryParam) throws IOException {
        fileRepository.get(queryParam.getFileId(), outputStream);
        outputStream.flush();
    }

    @Override
    public String getFileName(ImpExpParam queryParam) {
        log.debug("queryParam: {}", JSON.toJSONString(queryParam));
        StoredFileMetaInf fileMetaInf = fileRepository.getMetaInf(queryParam.getFileId());
        log.debug("fileMetaInf: {}", JSON.toJSONString(fileMetaInf));
        return fileMetaInf.getFileName() + "." + fileMetaInf.getExtName();
    }

    @Override
    public String getContentType(ImpExpParam queryParam) {
        return "application/octet-stream";
    }

    @Override
    public ImpExpParam newParameterObject() {
        return new ImpExpParam();
    }
}
