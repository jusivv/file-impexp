package org.coodex.file.impexp.config;

import org.coodex.file.impexp.service.FileDownloadResource;
import org.coodex.file.impexp.service.FileExportResource;
import org.coodex.file.impexp.service.FileImportResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;


@ApplicationPath("impexp")
public class FileImpExpJerseyConfig extends ResourceConfig {
    private static Logger log = LoggerFactory.getLogger(FileImpExpJerseyConfig.class);

    public FileImpExpJerseyConfig() {
        log.debug("load file ImpExp jersey configuration.");
        register(FileDownloadResource.class);
        register(FileExportResource.class);
        register(FileImportResource.class);
    }
}
