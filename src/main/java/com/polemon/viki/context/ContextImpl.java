package com.polemon.viki.context;

import com.polemon.viki.commons.VikiException;
import com.polemon.viki.commons.VikiProperties;
import com.polemon.viki.commons.context.IVikiContext;
import com.polemon.viki.commons.context.models.VikiSagaContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation for keeping context of a Saga.
 * It saves the context within the thread.
 *
 * @see IVikiContext
 */
@Slf4j
public class ContextImpl implements IVikiContext {

    private static final String LOG_TITLE = "[Context] -";

    private static ContextImpl INSTANCE;

    /**
     * Time to keep the context.
     */
    private final long timeout;

    /**
     * Variable to store all ExagonSagaContext per thread.
     */
    private final Map<Long, VikiSagaContext> contextMap;

    /**
     * Flag for the timeout thread.
     */
    private boolean running;

    private ContextImpl() throws VikiException {
        this.contextMap = new HashMap<>();
        this.timeout = VikiProperties.getINSTANCE().getSagaTimeout() + 100;
    }

    public static ContextImpl getINSTANCE() throws VikiException {
        if (INSTANCE == null) {
            INSTANCE = new ContextImpl();
        }
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        running = true;
        startTimeoutThread();
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        running = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSagaContext(VikiSagaContext vikiSagaContext) {
        contextMap.put(currentThread(), vikiSagaContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VikiSagaContext getSagaContext() {
        return contextMap.get(currentThread());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSagaId(String sagaId) {
        contextMap.computeIfAbsent(currentThread(), x -> new VikiSagaContext()).setSagaId(sagaId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSagaId() {
        VikiSagaContext context = contextMap.get(currentThread());
        if (context == null) {
            if (log.isDebugEnabled())
                log.debug("{} getSagaId() - Context Null", LOG_TITLE);
            return null;
        }
        return context.getSagaId();
    }

    /**
     * Private method to get the current thread.
     *
     * @return Long with the id of the thread
     */
    private Long currentThread() {
        return Thread.currentThread().getId();
    }

    /**
     * Thread to clean the contextMap.
     */
    @SuppressWarnings("BusyWait")
    private void startTimeoutThread() {
        new Thread(() -> {
            while (running) {
                if (contextMap.isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                List<Long> contextIdsToRemove = new LinkedList<>();
                for (long id : contextMap.keySet()) {
                    if (contextMap.get(id).getFirstCreationDate() + timeout < System.currentTimeMillis()) {
                        contextIdsToRemove.add(id);
                    }
                }

                log.trace("{} Removing {} contexts", LOG_TITLE, contextIdsToRemove.size());

                contextIdsToRemove.forEach(contextMap::remove);
            }
        }).start();
    }

}
