package Plugin.spigotPlugin;

import java.util.List;

public class Table {
    String tableName;
    List<String> columns;
    List<String> rows;
    int tableSize;
    public Table(String tableName, int tableSize, List<String>columns, List<String> rows){
        this.tableName = tableName;
        this.tableSize = tableSize;
        this.columns = columns;
        this.rows = rows;
    }

    public void increaseLength(){
        tableSize++;
    }
    public void decreaseLength(){
        tableSize--;
    }

    public String toString(){
        String str = tableName + ";";
        str = str + String.valueOf(tableSize) + ";";
        for(String col : columns){
            str += col + ";";
        }
        str += String.valueOf(tableSize);
        return str;
    }

}
