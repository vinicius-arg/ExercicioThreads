package edu.ufs.os.threads;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeArrayList<T> extends AbstractList<T> {
    private ArrayList<T> arr;
    private ReentrantReadWriteLock mutex;
    private int quantityOfOperations;

    public ThreadSafeArrayList() {
        this.arr = new ArrayList<>();
        this.mutex = new ReentrantReadWriteLock();
        this.quantityOfOperations = 0;
    }

    public int getQuantityOfOperations() {
        return this.quantityOfOperations;
    }

    public int size() {
        return arr.size();
    }

    @Override
    public void add(int index, T e) {
        this.mutex.writeLock().lock();

        this.arr.add(index, e);
        this.quantityOfOperations++;
        
        this.mutex.writeLock().unlock();
    }

    @Override
    public T remove(int index) {
        this.mutex.writeLock().lock();

        T oldElement = null;

        if (this.size() > index) {
            oldElement = this.arr.remove(index);
        }

        this.quantityOfOperations++;

        this.mutex.writeLock().unlock();

        return oldElement;
    }

    @Override
    public boolean contains(Object e) {
        this.mutex.readLock().lock();

        boolean hasElement = false;

        hasElement = this.arr.contains(e);
        
        this.mutex.readLock().unlock();

        this.mutex.writeLock().lock();
        
        this.quantityOfOperations++;
        
        this.mutex.writeLock().unlock();

        return hasElement;
    }

    @Override
    public T get(int index) {
        return this.arr.get(index);
    }
}
