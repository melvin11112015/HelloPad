package com.gki.v107.entity;

public class Polymorph<T, K> {
    private T addonEntity;
    private K infoEntity;
    private State state;

    public Polymorph(T addonEntity, K infoEntity, State state) {
        this.addonEntity = addonEntity;
        this.infoEntity = infoEntity;
        this.state = state;
    }

    public K getInfoEntity() {
        return infoEntity;
    }

    public void setInfoEntity(K infoEntity) {
        this.infoEntity = infoEntity;
    }

    public T getAddonEntity() {
        return addonEntity;
    }

    public void setAddonEntity(T addonEntity) {
        this.addonEntity = addonEntity;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {UNCOMMITTED_NEW, COMMITTED, FAILURE_NEW,UNCOMMITTED_EDIT,FAILURE_EDIT}
}
