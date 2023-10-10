package org.caesar.common.check.handler;

import java.lang.annotation.Annotation;

public abstract class CheckHandler {

    private CheckHandler next;

    public abstract boolean match(Annotation checker);

    public abstract void doCheck(Object attribute, Annotation checker);

    public CheckHandler setNextHandler(CheckHandler next) {
        this.next = next;
        return next;
    }

    public CheckHandler getNext() {
        return next;
    }

}
