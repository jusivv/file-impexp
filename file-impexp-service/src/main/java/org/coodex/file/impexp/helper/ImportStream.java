package org.coodex.file.impexp.helper;

import java.io.InputStream;

public interface ImportStream<T> {
    T read(InputStream inputStream, String fileName, long fileSize, String contentType);
}
