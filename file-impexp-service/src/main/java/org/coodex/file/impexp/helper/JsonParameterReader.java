package org.coodex.file.impexp.helper;

import com.alibaba.fastjson.JSON;
import org.coodex.util.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class JsonParameterReader implements ParameterReader{
    private static Logger log = LoggerFactory.getLogger(JsonParameterReader.class);

    @Override
    public <T> T read(T param, HttpServletRequest request) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Common.copyStream(request.getInputStream(), baos);
            String jsonStr = new String(baos.toByteArray(), Charset.forName("UTF-8"));
            return  (T) JSON.parseObject(jsonStr, param.getClass());
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean accept(String contentType) {
        return Common.nullToStr(contentType).toLowerCase().contains("application/json");
    }
}
