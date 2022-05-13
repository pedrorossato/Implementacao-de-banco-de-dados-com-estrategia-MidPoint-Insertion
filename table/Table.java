/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table;

import ibd.block.BufferManager;
import ibd.block.LRUBufferManager;
import ibd.index.HashIndex;
import ibd.index.Index;
import ibd.index.IndexRecord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Table implements Iterable{

    public final static Long BLOCKS_AMOUNT = 64L;

    //header
    public final static Long INDEX_LEN = Long.BYTES + 3 * BLOCKS_AMOUNT * Block.RECORDS_AMOUNT * Long.BYTES;
    public final static Long FREE_BLOCKS_LEN = Long.BYTES + BLOCKS_AMOUNT * Long.BYTES;
    public final static Long FILLER = 0L;
    public final static Long HEADER_LEN = INDEX_LEN + FREE_BLOCKS_LEN + FILLER;

    public final static Long BD_LEN = HEADER_LEN + BLOCKS_AMOUNT * Block.BLOCK_LEN;

    protected FreeBlocks freeBlocks = new FreeBlocks();
    protected TableIO tableIO = null;
    
    public BufferManager bufferManager = new LRUBufferManager();
    Index index = new HashIndex();
    
    public boolean loaded = false;
    
    public String key;
    
    public Table() {
    }
    
    
    public Table(String folder, String name) throws Exception {
        tableIO = new TableIO(folder, name);
    }

    public void initLoad() throws Exception {

        if (loaded) return;
        index.clear();
        tableIO.loadIndex(index);

        bufferManager.clear();
        freeBlocks.clear();
        tableIO.loadFreeBlocks(freeBlocks);

        loaded = true;
        
    }

    public Block getBlock(long block_id) throws Exception{
        
        if (!loaded) initLoad();
        
        return bufferManager.getBlock(block_id, tableIO);
    }
    
    public Record getRecord(Long primaryKey) throws Exception {

        if (!loaded) initLoad();
        
        IndexRecord index_rec = index.getEntry(primaryKey);
        if (index_rec == null) {
            return null;
        }

        //find the block that contains the record
        Block block = getBlock(index_rec.getBlockId());

        //now locate the record within the block
        return (Record) block.getRecord((int) (long) index_rec.getRecordId());
    }
    
    public List<Record> getRecords(Long primaryKey, int comparisonType) throws Exception {

        if (!loaded) initLoad();
        
        List<IndexRecord> list = index.getEntries(primaryKey, comparisonType);
        ArrayList<Record> records = new ArrayList();
        
        for (int i = 0; i < list.size(); i++) {
            IndexRecord index_rec = list.get(i);
            //find the block that contains the record
            Block block = getBlock(index_rec.getBlockId());
            //now locate the record within the block
            Record rec = (Record) block.getRecord((int) (long) index_rec.getRecordId()); 
            records.add(rec);
        }
        
        return records;
    }

    public boolean isFull() throws Exception{
        
        if (!loaded) initLoad();
        
        return (freeBlocks.getFreeBlocksCount() == 0);
    }

    public Record addRecord(long primaryKey, String content) throws Exception {

        
        if (!loaded) initLoad();
        
        if (index.getEntry(primaryKey) != null) {
            throw new Exception("ID already exists");
        }

        if (isFull()) {
            throw new Exception("No Space");
        }

        
        
        Record rec = new CreatedRecord(primaryKey);
        rec.setContent(content);

        
        Long free_block_id = selectBlock(primaryKey);
        
        
        Block block = bufferManager.getBlock(free_block_id, tableIO);

        
        addRecord(block, rec);

        return rec;

    }
    
    public Record updateRecord(long primaryKey, String content) throws Exception {

        if (!loaded) initLoad();
        
        IndexRecord index_rec = index.getEntry(primaryKey);
        if (index_rec == null) {
            return null;
        }

        Block block = getBlock(index_rec.getBlockId());
        Record rec = (Record) block.getRecord((int) (long) index_rec.getRecordId());
        //now the block contains the updated record
        rec.setContent(content);
        return rec;

    }
    
    public Record removeRecord(long primaryKey) throws Exception {

        if (!loaded) initLoad();
        
        IndexRecord index_rec = index.getEntry(primaryKey);
        if (index_rec == null) {
            return null;
        }

        Block block = getBlock(index_rec.getBlockId());
        Record rec = (Record) block.getRecord((int) (long) index_rec.getRecordId());
        //now the block contains the updated record
        removeRecord(rec);
        return rec;

    }

    
    
    protected void addRecord(Block block, Record rec) throws Exception {
    
        if (!loaded) initLoad();
        
        block.addRecord(rec, -1L);
        
        if (block.isFull()) {
            freeBlocks.removeFreeBlock(block.block_id);
        }

        index.addEntry(block.block_id, rec.getRecordId(), rec.getPrimaryKey());
    }
    
    
    protected abstract Long selectBlock(long primaryKey) throws Exception;

    protected void removeRecord(Record record) throws Exception {

        if (!loaded) initLoad();
        
        
        Block block = record.getBlock();
        boolean wasFull = block.isFull();

        block.removeRecord(record);
        
        if (wasFull) {
            freeBlocks.addFreeBlock(block.block_id);
        }

        index.removeEntry(record.getPrimaryKey());

    }

    public void flushDB() throws Exception {

        if (!loaded) return;
        
        tableIO.flushIndex(index);

        tableIO.flushFreeBlocks(freeBlocks.getFreeBlocksIds());

        tableIO.flushBlocks(bufferManager.getBufferedBlocks());

    }

    public void createTable() throws Exception {
        tableIO.createTable();
    }
    
    public int getRecordsAmount(){
        return index.getRecordsAmount();
    }
    
    @Override
    public Iterator<Record> iterator() {
        return new TableIterator();
    }

    class TableIterator implements Iterator{

        Iterator<IndexRecord> it;
        long currentBlock;
        
        public TableIterator() {
            it = index.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Record next(){
            try {
                IndexRecord ir = it.next();
                Block block = getBlock(ir.getBlockId());
                Record rec = (Record) block.getRecord((int) (long) ir.getRecordId());
                return rec;
            } catch (Exception ex) {
                Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("não deveria chegar aqui");
            return null;
        }
    
    }
    
}
