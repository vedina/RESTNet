package net.idea.restnet.i.task;

import java.io.Serializable;
import java.util.UUID;

import org.restlet.resource.ResourceException;

public interface ITask<REFERENCE, USERID> extends Serializable {
    void update();

    boolean isDone();

    UUID getUuid();

    String getName();

    void setName(String name);

    /**
     * internal is a flag in AMBIT, in Restnet there are task categories TODO
     * sync
     * 
     * @return
     */
    boolean isInternal();

    void setInternal(boolean internal);

    long getStarted();

    long getTimeCompleted();

    void setTimeCompleted(long completed);

    float getPercentCompleted();

    void setStatus(TaskStatus status);

    REFERENCE getUri();

    void setUri(REFERENCE uri);

    ResourceException getError();

    void setError(ResourceException error);

    Exception getPolicyError();

    void setPolicyError(Exception policyError);

    void setPolicy() throws Exception;

    boolean isExpired(long lifetime);

    TaskStatus getStatus();

    USERID getUserid();

    String toJSON();
}
