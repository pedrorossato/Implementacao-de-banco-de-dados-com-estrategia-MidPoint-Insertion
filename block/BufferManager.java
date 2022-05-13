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
import java.util.TreeSet;

/**
 *
 * @author pccli
 */
public interface BufferManager {
    
    public static final int BUFFER_SIZE = 6;
    
    public void clear();
    public Block[] getBufferedBlocks();
    public Block getBlock(Long block_id, TableIO databaseIO) throws Exception;
    
    
}
