package org.coodex.file.impexp.helper;

import java.util.Optional;
import java.util.ServiceLoader;

public class ParameterReaderSelector {
    public static Optional<ParameterReader> select(String contentType) {
        ServiceLoader<ParameterReader> parameterReaders = ServiceLoader.load(ParameterReader.class);
        for (ParameterReader parameterReader : parameterReaders) {
            if (parameterReader.accept(contentType)) {
                return Optional.of(parameterReader);
            }
        }
        return Optional.empty();
    }
}
