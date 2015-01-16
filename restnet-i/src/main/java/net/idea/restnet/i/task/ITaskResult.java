package net.idea.restnet.i.task;

import java.util.List;

import org.restlet.data.Reference;

public interface ITaskResult {
    Reference getReference();

    String getUri();

    boolean isNewResource();

    List<String> getPolicy();

    float getPercentCompleted();
}
