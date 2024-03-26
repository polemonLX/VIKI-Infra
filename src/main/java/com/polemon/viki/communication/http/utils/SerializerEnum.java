package com.polemon.viki.communication.http.utils;

import com.polemon.viki.communication.http.utils.json.JsonSerializer;
import com.polemon.viki.communication.http.utils.xml.XmlSerializer;
import lombok.Getter;

/**
 * Enum with all serializers possible
 */
@Getter
public enum SerializerEnum {
    JSON(new JsonSerializer()),
    XML(new XmlSerializer());

    private final Serializer serializer;

    SerializerEnum(Serializer serializer) {
        this.serializer = serializer;
    }

}
