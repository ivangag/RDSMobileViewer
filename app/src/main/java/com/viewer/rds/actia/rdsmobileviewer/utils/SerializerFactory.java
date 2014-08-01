package com.viewer.rds.actia.rdsmobileviewer.utils;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by igaglioti on 31/07/2014.
 */
public class SerializerFactory {


    /**
     * Help method to convert data from/to Json/Java base classes
     * Note: this use Jackson Library
     */
    public static String convertDataObjectToJSON (Object data) throws IOException {

        final OutputStream outputStream = new ByteArrayOutputStream();
        final JsonGenerator jsonGenerator = new JsonFactory().createJsonGenerator(outputStream);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        mapper.writer().writeValue(jsonGenerator, data);
        return outputStream.toString();
        //convertJSONtoDataObject(jsonRep, data.getClass());
    }

    public static Object convertJSONtoDataObject(String jsonData, Class<?> type) throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
        final Object dataRes = mapper.reader(type).readValue(jsonData);
        return dataRes;
    }
}
