package com.rumaruka.powercraft.api.gres.autoadd;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PCStringListPart implements List<PCStringWithInfo> {
    List<PCStringWithInfo> sortedList;
    int start;
    int end;
    private String lastSearch;

    public PCStringListPart(PCSortedList<PCStringWithInfo> sortedList){
        this.sortedList = sortedList;
        this.end = sortedList.size();
    }

    public void searchForAdd(String toAdd) {
        if(this.lastSearch==null){
            searchFor(toAdd);
        }else{
            searchFor(this.lastSearch+toAdd);
        }
    }

    public void searchFor(String s){
        if(this.lastSearch!=null && s.startsWith(this.lastSearch)){
            int oldStart = this.start;
            int oldEnd = this.end;
            this.start = -1;
            this.end = -1;
            for(int i=oldStart; i<oldEnd; i++){
                PCStringWithInfo ss = this.sortedList.get(i);
                if(ss.startsWith(s)){
                    if(this.start==-1)
                        this.start = i;
                    this.end = i+1;
                }else if(this.end!=-1)
                    break;
            }
            if(this.start==-1)
                this.start = 0;
            if(this.end==-1)
                this.end = 0;
        }else if(this.lastSearch!=null && this.lastSearch.startsWith(s)){
            for(int i=0; i<this.start; i++){
                PCStringWithInfo ss = this.sortedList.get(i);
                if(ss.startsWith(s)){
                    this.start = i;
                    break;
                }
            }
            for(int i=this.end; i<this.sortedList.size(); i++){
                PCStringWithInfo ss = this.sortedList.get(i);
                if(ss.startsWith(s)){
                    this.end = i+1;
                }else{
                    break;
                }
            }
        }else{
            this.start = -1;
            this.end = -1;
            for(int i=0; i<this.sortedList.size(); i++){
                PCStringWithInfo ss = this.sortedList.get(i);
                if(ss.startsWith(s)){
                    if(this.start==-1)
                        this.start = i;
                    this.end = i+1;
                }else if(this.end!=-1)
                    break;
            }
            if(this.start==-1)
                this.start = 0;
            if(this.end==-1)
                this.end = 0;
        }
        this.lastSearch = s;
    }

    @Override
    public boolean add(PCStringWithInfo e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, PCStringWithInfo element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends PCStringWithInfo> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends PCStringWithInfo> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        int index = this.sortedList.indexOf(o);
        return index>=this.start && index<this.end;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object o:c){
            if(!contains(o)){
                return false;
            }
        }
        return true;
    }

    @Override
    public PCStringWithInfo get(int index) {
        return this.sortedList.get(index+this.start);
    }

    @Override
    public int indexOf(Object o) {
        int index = this.sortedList.indexOf(o);
        if(index>=this.start && index<this.end){
            return index;
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return this.start==this.end;
    }

    @Override
    public Iterator<PCStringWithInfo> iterator() {
        return listIterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = this.sortedList.lastIndexOf(o);
        if(index>=this.start && index<this.end){
            return index;
        }
        return -1;
    }

    @Override
    public ListIterator<PCStringWithInfo> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<PCStringWithInfo> listIterator(int index) {
        return new It(index+this.start);
    }

    private class It implements ListIterator<PCStringWithInfo>{

        private int index;

        public It(int index) {
            this.index = index;
        }

        @Override
        public void add(PCStringWithInfo e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return this.index<PCStringListPart.this.end;
        }

        @Override
        public boolean hasPrevious() {
            return this.index>PCStringListPart.this.start;
        }

        @Override
        public PCStringWithInfo next() {
            return PCStringListPart.this.sortedList.get(this.index++);
        }

        @Override
        public int nextIndex() {
            return this.index;
        }

        @Override
        public PCStringWithInfo previous() {
            return PCStringListPart.this.sortedList.get(--this.index);
        }

        @Override
        public int previousIndex() {
            return this.index-1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(PCStringWithInfo e) {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PCStringWithInfo remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PCStringWithInfo set(int index, PCStringWithInfo element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.end-this.start;
    }

    @Override
    public List<PCStringWithInfo> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        Object[] obj = new String[size()];
        for(int i=0; i<obj.length; i++){
            obj[i] = get(i);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        int size = size();
        T[] na = a;
        if(na.length<size){
            na = (T[]) Array.newInstance(na.getClass().getComponentType(), size);
        }
        for(int i=0; i<size; i++){
            na[i] = (T) get(i);
        }
        return na;
    }
}
