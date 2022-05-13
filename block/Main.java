/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.block;

import ibd.table.Table;
import ibd.table.Directory;
import ibd.table.Block;
import ibd.table.Utils;
import ibd.table.Params;
import ibd.table.Record;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Sergio
 */
public class Main {

    public long execMultipleInsertions(Table table, int amount, boolean ordered, boolean display) throws Exception {
        table.createTable();
        table.initLoad();

        Long[] array = new Long[amount];
        for (int i = 0; i < array.length; i++) {
            array[i] = new Long(i);
        }

        if (!ordered) {
            Utils.shuffleArray(array);
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            if (display) {
                System.out.println("adding primary key =  " + array[i]);
            }
            table.addRecord(array[i], "Novo registros " + array[i]);

        }

        table.flushDB();
        long end = System.currentTimeMillis();
        return (end - start);
    }

    public Long[] generateRecordIDs(int blocksAmount1, int blocksAmount2,
                                    int recordsAmount1, int recordsAmount2) {
        ArrayList<Long> list = new ArrayList();
        Random r = new Random();

        int min = 0;
        int max = blocksAmount1;

        if (max > Table.BLOCKS_AMOUNT) {
            max = Table.BLOCKS_AMOUNT.intValue();
        }

        for (int j = 0; j < recordsAmount1; j++) {
            long v = (long) (min + r.nextInt(max));
            list.add(v * Block.RECORDS_AMOUNT);
        }

        min = max;
        max = min + blocksAmount2;
        if (max > Table.BLOCKS_AMOUNT) {
            max = Table.BLOCKS_AMOUNT.intValue();
        }

        for (int j = 0; j < recordsAmount2; j++) {
            long v = (long) (min + r.nextInt(max - min));
            list.add(v * Block.RECORDS_AMOUNT);
        }

        Long array[] = list.toArray(new Long[list.size()]);

        Utils.shuffleArray(array);

        return array;
    }

    public long execMultipleSearches(Table table, Long[] recIDs, boolean display) throws Exception {

        table.initLoad();

        long start = System.currentTimeMillis();

        for (int i = 0; i < recIDs.length; i++) {
            Record rec = table.getRecord(recIDs[i]);

            if (rec != null) {
                if (!display) {
                    continue;
                }

                System.out.println(rec.getContent() + " in block " + rec.getBlockId());
            } else {
                System.out.println("erro: inexistente " + recIDs[i]);
            }
        }
        long end = System.currentTimeMillis();
        return (end - start);
    }

    public static void main(String[] args) {
        try {
            Main m = new Main();

            Long[] recIDs = m.generateRecordIDs(6, 60, 1000, 100);

            Table table1 = Directory.getTable("c:\\teste\\ibd", "table.ibd");
            m.execMultipleInsertions(table1, (int) (Block.RECORDS_AMOUNT * Table.BLOCKS_AMOUNT), true, false);
            table1.bufferManager = new LRUBufferManager();
            Params.BLOCKS_LOADED = 0;
            long time = m.execMultipleSearches(table1, recIDs, false);
            System.out.println("BLOCKS_LOADED " + Params.BLOCKS_LOADED);
            System.out.println("time " + time);

            Table table2 = Directory.getTable("c:\\teste\\ibd", "table.ibd");
            table2.bufferManager = new PedroRossatoMidPointBufferManager();
            Params.BLOCKS_LOADED = 0;
            time = m.execMultipleSearches(table2, recIDs, false);
            System.out.println("BLOCKS_LOADED " + Params.BLOCKS_LOADED);
            System.out.println("time " + time);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}