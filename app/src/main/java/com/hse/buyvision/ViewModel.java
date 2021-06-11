package com.hse.buyvision;

public class ViewModel {
    private long counter = -1;
    private DBWrapper dbWrapper;
    
    public ViewModel(DBWrapper dbWrapper){
        this.dbWrapper = dbWrapper;
        dbWrapper.loadResults();
    }
    public ItemModel getNext(){
        System.out.println("GO!!!!!!!!!!!!!!");
        return dbWrapper.getNext();
    }
    public ItemModel getPrev(){
        return dbWrapper.getPrev();
    }
    public Boolean hasPrev(){
        return dbWrapper.hasPrev();
    }
    public Boolean hasNext(){
        return dbWrapper.hasNext();
    }
}
