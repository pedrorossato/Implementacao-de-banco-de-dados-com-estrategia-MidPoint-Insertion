package ibd.block;

import ibd.table.Block;
import ibd.table.TableIO;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class PedroRossatoMidPointBufferManager implements BufferManager {

    private LinkedList<Block> newBlockList = new LinkedList<>();
    private LinkedList<Block> oldBlockList = new LinkedList<>();
    private Hashtable<Long,Block> blocksBuffer = new Hashtable<>();

    @Override
    public void clear() {
        newBlockList.clear();
        oldBlockList.clear();
        blocksBuffer.clear();
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
        Block block = blocksBuffer.get(block_id);
        if (block!=null) {
            if (oldBlockList.contains(block)){
                oldBlockList.remove(block);
                newBlockList.addFirst(block);
            } else {
                newBlockList.remove(block);
                newBlockList.addFirst(block);
            }
            return block;
        }
        return loadBlock(block_id,databaseIO);
    }

    private Block loadBlock(Long block_id, TableIO databaseIO) {
        Block block = new Block(block_id);
        if (blocksBuffer.size() == BUFFER_SIZE){
//            Block b =;
        }
        return null;
    }

}
