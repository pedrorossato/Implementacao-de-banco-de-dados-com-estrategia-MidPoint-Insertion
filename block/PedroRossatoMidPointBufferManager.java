package ibd.block;

import ibd.table.Block;
import ibd.table.TableIO;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

public class PedroRossatoMidPointBufferManager implements BufferManager {

    private LinkedList<Block> newBlockList = new LinkedList<>();
    private static final Integer NEW_BLOCK_LIST_MAX_SIZE = 3;
    private LinkedList<Block> oldBlockList = new LinkedList<>();
    private static final Integer OLD_BLOCK_LIST_MAX_SIZE = 3;
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
            if (oldBlockList.contains(block)) {
                oldBlockList.remove(block);
            } else {
                newBlockList.remove(block);
            }
            if (newBlockList.size() == NEW_BLOCK_LIST_MAX_SIZE) {
                Block oldBlockOnNewBlockList = newBlockList.removeLast();
                oldBlockList.addFirst(oldBlockOnNewBlockList);
            }
            newBlockList.addFirst(block);
            return block;
        }
        return loadBlock(block_id,databaseIO);
    }

    private Block loadBlock(Long block_id, TableIO databaseIO) throws Exception {
        Block block = new Block(block_id);

        if (oldBlockList.size() == OLD_BLOCK_LIST_MAX_SIZE) {
            Block removedBlockFromOld = oldBlockList.removeLast();
            removeBlock(removedBlockFromOld,databaseIO);
        }

        blocksBuffer.put(block_id,block);
        oldBlockList.addFirst(block);

        databaseIO.loadBlock(block,block_id);

        return null;
    }

    private void removeBlock(Block block, TableIO databaseIO) throws Exception{

        databaseIO.flushBlock(block);

        blocksBuffer.remove(block.block_id);
    }

}
