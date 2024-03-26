package com.polemon.viki.communication.http.utils;

import com.polemon.viki.communication.http.utils.json.JsonDeserializer;
import com.polemon.viki.communication.http.utils.xml.XmlDeserializer;
import lombok.Getter;

/**
 * Enum with all deserializers possible
 */
@Getter
public enum DeserializerEnum {
    JSON(new JsonDeserializer()),
    XML(new XmlDeserializer());

    private final Deserializer deserializer;

    DeserializerEnum(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

}
