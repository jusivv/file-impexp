package org.coodex.file.impexp.helper;

import org.coodex.util.Common;
import org.coodex.util.ReflectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.lang.reflect.Field;

public class QueryParameterReader implements ParameterReader {
    private static Logger log = LoggerFactory.getLogger(QueryParameterReader.class);

    @Override
    public <T> T read(T param, HttpServletRequest request) {
        Field[] fields = ReflectHelper.getAllDeclaredFields(param.getClass());
        for (Field field : fields) {
            try {
                QueryParam queryParam = field.getAnnotation(QueryParam.class);
                String paramName = queryParam != null ? queryParam.value() : field.getName();
                if (!Common.isBlank(paramName)) {
                    Object v = getValue(request,paramName, field.getType());
                    if (v != null) {
                        field.setAccessible(true);
                        try {
                            field.set(param, v);
                        } catch (IllegalAccessException e) {
                            log.error(e.getLocalizedMessage(), e);
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (RuntimeException e) {
                log.error(e.getLocalizedMessage(), e);
                throw e;
            }
        }
        return param;
    }

    @Override
    public boolean accept(String contentType) {
        return Common.isBlank(contentType);
    }

    private static Object getValue(HttpServletRequest request, String paramName, Class<?> fieldClass) {
        if (fieldClass.isPrimitive()) {
            String className = fieldClass.getName();
            String paramValue = request.getParameter(paramName);
            if (className.equals("int")) {
                return Common.isBlank(paramValue) ? 0 : Integer.parseInt(paramValue);
            } else if (className.equals("long")) {
                return Common.isBlank(paramValue) ? 0L : Long.parseLong(paramValue);
            } else if (className.equals("double")) {
                return Common.isBlank(paramValue) ? 0D : Double.parseDouble(paramValue);
            } else if (className.equals("float")) {
                return Common.isBlank(paramValue) ? 0F : Float.parseFloat(paramValue);
            } else if (className.equals("boolean")) {
                return Common.isBlank(paramValue) || "0,false,f,no,n".indexOf(paramValue.toLowerCase()) >= 0 ? false :
                        true;
            } else {
                log.warn("Unsupport field class: {}", className);
                return null;
            }
        } else if (Number.class.isAssignableFrom(fieldClass)) {
            String paramValue = request.getParameter(paramName);
            return Common.isBlank(paramValue) ? 0 : fieldClass.cast(paramValue);
        } else if (String.class.isAssignableFrom(fieldClass)) {
            return request.getParameter(paramName);
        } else if (Boolean.class.isAssignableFrom(fieldClass)) {
            String paramValue = request.getParameter(paramName);
            return Common.isBlank(paramValue) || "0,false,f,no,n".indexOf(paramValue.toLowerCase()) >= 0 ? false : true;
        } else if (fieldClass.isArray()) {
            // TODO
            return null;
        } else {
            log.warn("Unsupport field class: {}", fieldClass.getName());
            return null;
        }
    }
}
