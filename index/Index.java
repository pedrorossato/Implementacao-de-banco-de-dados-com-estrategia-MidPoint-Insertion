/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.index;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author pccli
 */
public interface Index {
    
    public void clear();
    public void addEntry(Long blockId, Long recordId, Long primaryKey);
    public void removeEntry(Long primaryKey);
    public IndexRecord getEntry(Long primaryKey);
    public List<IndexRecord> getEntries(Long primaryKey, int comparisonType);
    public int getRecordsAmount();
    public Iterator iterator();
}
