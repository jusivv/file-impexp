package org.coodex.file.impexp.service;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.coodex.file.impexp.helper.ImportStream;
import org.coodex.file.impexp.helper.SpringBeanTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("file/import")
public class FileImportResource {

    private static Logger log = LoggerFactory.getLogger(FileImportResource.class);

    @Path("/{bizName}")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public void importByForm(@Context final HttpServletRequest request, @Suspended final AsyncResponse asyncResponse,
                             @PathParam("bizName") final String bizName) {
        Thread uploadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
                try {
                    List<FileItem> items = fileUpload.parseRequest(request);
                    List<Object> results = new ArrayList<Object>();
                    ImportStream<?> importStream = SpringBeanTool.getBean(bizName, ImportStream.class);
                    for (FileItem item : items) {
                        if (!item.isFormField()) {
                            try {
                                results.add(importStream.read(item.getInputStream(), item.getName(), item.getSize(),
                                        item.getContentType()));
                            } catch (IOException e) {
                                log.error(e.getLocalizedMessage(), e);
                                asyncResponse.resume(e);
                            }
                        }
                    }
                    if (results.size() == 1) {
                        asyncResponse.resume(results.get(0));
                    } else {
                        asyncResponse.resume(results.toArray());
                    }
                } catch (FileUploadException e) {
                    log.error(e.getLocalizedMessage(), e);
                    asyncResponse.resume(e);
                } catch (BeansException e) {
                    log.error(e.getLocalizedMessage(), e);
                    asyncResponse.resume(e);
                }
            }
        });

        uploadThread.start();
    }
}
