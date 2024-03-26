package com.polemon.viki.commons.context.models;

import com.polemon.viki.commons.context.IVikiContext;
import lombok.Getter;
import lombok.Setter;

/**
 * This class contains all the needed information about a Saga.
 * The user can construct this class all at once, or it can save information using setters from the {@link IVikiContext}
 *
 * @see IVikiContext
 */
@Getter
public class VikiSagaContext {

    /**
     * Time of first creation of this context.
     */
    private final long firstCreationDate;

    /**
     * SagaId of the Saga.
     */
    @Setter
    private String sagaId;

    public VikiSagaContext() {
        this.firstCreationDate = System.currentTimeMillis();
    }

    public VikiSagaContext(String sagaId) {
        this.firstCreationDate = System.currentTimeMillis();
        this.sagaId = sagaId;
    }

}
