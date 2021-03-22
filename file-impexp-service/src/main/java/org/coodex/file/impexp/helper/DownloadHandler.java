package org.coodex.file.impexp.helper;

public interface DownloadHandler<P> extends RequestParameterCreator<P> {
    ResourceInfo getResource(P queryParam);
}
