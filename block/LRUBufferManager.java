/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.block;

import ibd.table.Block;
import ibd.table.TableIO;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author pccli
 */
public class LRUBufferManager implements BufferManager{
    
    
    protected Hashtable<Long, Block> blocksBuffer = new Hashtable();
    
    LinkedList<Block> blocksList = new LinkedList();
    
    
    
    @Override
    public void clear(){
        blocksBuffer.clear();
        //freeBlocks.clear();
        blocksList.clear();
    
    }
    
    
    
    @Override
    public Block[] getBufferedBlocks() {
    Block[] blocks = new Block[blocksBuffer.size()];
    Iterator<Block> it = blocksBuffer.values().iterator();
    int x=0;
        while (it.hasNext()) {
            Block bl = it.next();
            blocks[x] = bl;
            x++;
        }
    return blocks;
    }
    
    
    
    @Override
    public Block getBlock(Long block_id, TableIO databaseIO) throws Exception {
    //check if record is in buffer
        Block block = blocksBuffer.get(block_id);
        if (block!=null) {
            blocksList.remove(block);
            blocksList.addFirst(block);
            return block;
        }

        //needs to load block and all its records
        //System.out.println("loading block "+block_id);
        return loadBlock(block_id, databaseIO);
    
    }
    
    //load block and all its records
    private Block loadBlock(Long block_id, TableIO databaseIO) throws Exception {


        Block block = new Block(block_id);

        //if buffer is full, needs to flush a block
        if (blocksBuffer.size() == BUFFER_SIZE) {
            Block b = blocksList.removeLast();
            removeBlock(b, databaseIO);
        }

        //System.out.println("needed to load block "+block_id);
        
        //add block into buffer
        blocksBuffer.put(block_id, block);
        blocksList.addFirst(block);

        //load from disk
        databaseIO.loadBlock(block, block_id);

        return block;
    }
    
    

    private void removeBlock(Block block, TableIO databaseIO) throws Exception {

        //save block
        databaseIO.flushBlock(block);

        blocksBuffer.remove(block.block_id);
    }
    
}
