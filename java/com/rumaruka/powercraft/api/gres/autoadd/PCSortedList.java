package com.rumaruka.powercraft.api.gres.autoadd;

import java.util.*;

public class PCSortedList<T extends  Comparable<T>>implements List<T> {
    private List<T>sortedList = new ArrayList<T>();



    @Override
    public int size() {
        return this.sortedList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.sortedList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.sortedList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.sortedList.iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.sortedList.toArray(a);
    }

    @Override
    public boolean add(T t) {
        ListIterator<T> li = this.sortedList.listIterator();
        while(li.hasNext()){
            T s = li.next();
            int comp = s.compareTo(t);
            if(comp==0){
                return false;
            }else if(comp>0){
                li.previous();
                li.add(t);
                return true;
            }
        }
        li.add(t);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        return this.sortedList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.sortedList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for(T s:c){
            add(s);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.sortedList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
this.sortedList.clear();
    }

    @Override
    public T get(int index) {
        return this.sortedList.get(index);
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        add(element);
    }

    @Override
    public T remove(int index) {
        return this.sortedList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.sortedList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new LI<T>(this.sortedList.listIterator());
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new LI<T>(this.sortedList.listIterator(index));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
       throw new UnsupportedOperationException();
    }
    private static class LI<T> implements ListIterator<T>{

        private ListIterator<T> li;

        LI(ListIterator<T> li){
            this.li = li;
        }

        @Override
        public void add(T e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return this.li.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.li.hasPrevious();
        }

        @Override
        public T next() {
            return this.li.next();
        }

        @Override
        public int nextIndex() {
            return this.li.nextIndex();
        }

        @Override
        public T previous() {
            return this.li.previous();
        }

        @Override
        public int previousIndex() {
            return this.li.previousIndex();
        }

        @Override
        public void remove() {
            this.li.remove();
        }

        @Override
        public void set(T e) {
            throw new UnsupportedOperationException();
        }

    }
}
