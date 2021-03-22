package org.coodex.file.impexp.helper;

import javax.servlet.http.HttpServletRequest;

public interface ParameterReader {
    <T> T read(T param, HttpServletRequest request);

    boolean accept(String contentType);
}
