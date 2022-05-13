/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.table;

/**
 *
 * @author pccli
 */
public abstract class AbstractRecord {
    
    
    public abstract Long getRecordId();
    
    public abstract String getContent();
    
    public abstract Long getBlockId();

    public abstract Long getPrimaryKey();
    
    
}
