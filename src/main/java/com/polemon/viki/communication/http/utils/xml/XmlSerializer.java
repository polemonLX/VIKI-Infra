package com.polemon.viki.communication.http.utils.xml;

import com.polemon.viki.commons.communication.exception.VikiCommunicationProducerException;
import com.polemon.viki.communication.http.utils.Serializer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;

/**
 * This class converts objects into xml format.
 *
 * @see Serializer
 */
public class XmlSerializer implements Serializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(Object payload) throws VikiCommunicationProducerException {
        if (payload == null) {
            return new byte[]{};
        }

        try {
            JAXBContext contextObj = JAXBContext.newInstance(payload.getClass());
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            marshallerObj.marshal(payload, os);
            return os.toByteArray();
        } catch (JAXBException e) {
            throw new VikiCommunicationProducerException(e.getMessage());
        }
    }

}
