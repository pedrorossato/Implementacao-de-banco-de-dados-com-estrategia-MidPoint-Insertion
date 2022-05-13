/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Sergio
 */
public class HashIndex implements Index,  Iterable {

    public Hashtable<Long, IndexRecord> index = new Hashtable();

    @Override
    public void clear() {
        index.clear();
    }

    @Override
    public void addEntry(Long blockId, Long recordId, Long primaryKey) {
        IndexRecord ir = new IndexRecord(blockId, recordId, primaryKey);
        index.put(primaryKey, ir);
    }

    @Override
    public void removeEntry(Long primaryKey) {
        index.remove(primaryKey);
    }

    @Override
    public IndexRecord getEntry(Long primaryKey) {
        return index.get(primaryKey);
    }

    @Override
    public List<IndexRecord> getEntries(Long primaryKey, int comparisonType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRecordsAmount() {
        return index.size();
    }

    @Override
    public Iterator iterator() {
        return new HashIndexIterator(this);
        //return index.values().iterator();
    }
    
    class HashIndexIterator implements Iterator{

        Iterator it;
        
        public HashIndexIterator(HashIndex index){
            this.it = index.index.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Object next() {
            return it.next();
        }
    
    } 
    
}
