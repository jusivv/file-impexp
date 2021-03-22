package org.coodex.file.impexp.helper;

import java.io.IOException;
import java.io.OutputStream;

public interface ExportStream<T> extends RequestParameterCreator<T> {
    void write(OutputStream outputStream, T queryParam) throws IOException;

    String getFileName(T queryParam);

    String getContentType(T queryParam);
}
