package com.polemon.viki.communication.http.utils.xml;

import com.polemon.viki.commons.communication.exception.VikiCommunicationConsumerException;
import com.polemon.viki.communication.http.utils.Deserializer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.StringReader;

/**
 * This class accepts xml as data.
 *
 * @see Deserializer
 */
public class XmlDeserializer implements Deserializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deserialize(String xml, Class<?> modelType) throws VikiCommunicationConsumerException {
        if (xml == null) {
            return null;
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(modelType);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(xml);
            return jaxbUnmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new VikiCommunicationConsumerException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> modelType) throws VikiCommunicationConsumerException {
        if (bytes == null) {
            return null;
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(modelType);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return jaxbUnmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new VikiCommunicationConsumerException(e.getMessage());
        }
    }

}
