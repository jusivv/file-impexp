package org.coodex.file.impexp.service;

import org.coodex.file.impexp.helper.ExportStream;
import org.coodex.file.impexp.helper.ParameterReader;
import org.coodex.file.impexp.helper.ParameterReaderSelector;
import org.coodex.file.impexp.helper.SpringBeanTool;
import org.coodex.util.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Path("file/export")
public class FileExportResource {

    private static Logger log = LoggerFactory.getLogger(FileExportResource.class);



    @Path("/{bizName}")
    @GET
    public void exportGet(
            @Context final HttpServletRequest request,
            @Suspended final AsyncResponse asyncResponse,
            @PathParam("bizName") final String bizName) {
        execute(request, asyncResponse, bizName);
    }

    @Path("/{bizName}")
    @POST
    public void exportPost(
            @Context final HttpServletRequest request,
            @Suspended final AsyncResponse asyncResponse,
            @PathParam("bizName") final String bizName) {
        execute(request, asyncResponse, bizName);
    }

    private void execute(final HttpServletRequest request, final AsyncResponse asyncResponse, final String bizName) {
        Thread downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    asyncResponse.resume(download(bizName, request));
                } catch (BeansException e) {
                    log.error(e.getLocalizedMessage(), e);
                    asyncResponse.resume(e);
                } catch (UnsupportedEncodingException e) {
                    log.error(e.getLocalizedMessage(), e);
                    asyncResponse.resume(e);
                }
            }
        });
        downloadThread.start();
    }

    private Response download(String bizName, HttpServletRequest request)
            throws UnsupportedEncodingException {
        ParameterReader parameterReader = ParameterReaderSelector.select(request.getContentType()).orElseThrow();
        final ExportStream exportStream = SpringBeanTool.getBean(bizName, ExportStream.class);
        Object paramInstance = exportStream.newParameterObject();
        final Object param = paramInstance != null ? parameterReader.read(exportStream.newParameterObject(), request) :
                null;
        String contentType = exportStream.getContentType(param);
        String fileName = exportStream.getFileName(param);
        Response.ResponseBuilder builder = Response.ok()
                .header("Content-Type", Common.isBlank(contentType) ? "application/octet-stream" : contentType);
        builder.header("Content-Disposition",
                "attachment; fileName=\""
                        + URLEncoder.encode(!Common.isBlank(fileName) ? fileName :
                        String.valueOf(System.currentTimeMillis()), "UTF-8")
                        + "\"");

        StreamingOutput output = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                exportStream.write(output, param);
            }
        };

        return builder.entity(output).build();
    }
}
