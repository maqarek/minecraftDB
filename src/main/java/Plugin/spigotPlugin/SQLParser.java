package Plugin.spigotPlugin;

import org.bukkit.World;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class SQLParser {

    private WorldEditor editor;
    private TableEditor tableEditor;

    public SQLParser(WorldEditor editor) {
        this.editor = editor;
        this.tableEditor = editor.tableEditor;
    }

    public void parseSqlType(String sql){
        String[] arr = sql.split(" ");
        if(arr.length > 3){
            switch(arr[0]){ //TODO
                case "INSERT":
                    parseINSERT(sql);
                    break;
                case "UPDATE":
                    parseUPDATE(sql);
                    break;
                case "DELETE":
                    parseDELETE(sql);
                    break;
                case "SELECT":
                    parseSELECT(sql);
                    break;
                default:
                    throw new NullPointerException(); // you may want to replace it with another exception in the future
            }
        }else{
            throw new NullPointerException(); // same here
        }
    }

    public void parseSELECT(String sql){            //SELECT [COLUMNS] FROM [TABLE] WHERE [CONDITION]
        String[] arr = sql.split(" ");
        String[] selFields = arr[1].split(",");
        String tableName = arr[3];
        Predicate<JSONObject> filterCond =null;
        if(arr.length > 4){
            String condition = arr[5];
            filterCond = parsePredicate(condition);
        }

        Table table;
        try{
            table = findTable(tableName);
        }catch(NullPointerException e){
            e.printStackTrace();
            return;
        }

        String[] rows = table.rows.toArray(new String[0]);

        if(filterCond != null){
            String[] results = TableEditor.filter(rows, filterCond);
            SpigotPlugin.instance.player.sendMessage("SOMETHING IS FILTERED HERE");
            for(String s : results){
                SpigotPlugin.instance.player.sendMessage(s);
            }
        }else{
            SpigotPlugin.instance.player.sendMessage("NOTHING IS FILTERED HERE");
            for(String s : rows){
                SpigotPlugin.instance.player.sendMessage(s);
            }
        }
    }


    public void parseINSERT(String sql){ // INSERT INTO [TABLE] [COLUMN1,COLUMN2] VALUES VALUE1,VALUE2
        String[] arr = sql.split(" ");
        String tableName = arr[2];
        String[] insertFields = arr[3].split(",");
        String[] values = arr[5].split(",");

        String JsonResult = getJSONString(insertFields, values);
        String JsonResultHex = DataUtils.translateToHex(JsonResult);

        int tableZ = tableEditor.findTableZByName(tableName);

        editor.buildChunk(JsonResultHex, editor.getFreeChunkLocation(tableZ / 16).x(), -60, tableZ);// TODO: !IMPORTANT you re not finished here yet


    }
    public void parseUPDATE(String sql){ // UPDATE [TABLE] SET [COLUMN1=VALUE1,COLUMN2=VALUE2] WHERE [CONDITION] //WIP
        String[] arr = sql.split(" ");
        String tableName = arr[1];
        String[] updateFields = arr[3].split(",");
        Predicate<JSONObject> filterCond = parsePredicate(arr[5]);

        int tableZ = tableEditor.findTableZByName(tableName);
        Table table = tableEditor.getTableById(tableZ/16);

        String[] results = TableEditor.filter(table.rows.toArray(new String[0]), filterCond);

        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for(String field : updateFields){
            String[] pair = field.split("=");
            columns.add(pair[0]);
            values.add(pair[1]);
        }
        ///  we need here to create a function updating particular fields in results[]
        String[] columnsSTR = columns.toArray(new String[0]);
        String[] valuesSTR = values.toArray(new String[0]);

        Table resultTable = tableEditor.updateChunks(table, results, columnsSTR, valuesSTR);        // ready to test



    }
    public void parseDELETE(String sql){        // DELETE FROM [TABLE] WHERE [CONDITION]
        String[] arr = sql.split(" ");
        String tableName = arr[2];

        Predicate<JSONObject> filterCond =null;
        String condition = arr[4];
        filterCond = parsePredicate(condition);
        Table table;
        try{
            table = findTable(tableName);
        }catch(NullPointerException e){
            e.printStackTrace();
            return;
        }

        String[] rows = table.rows.toArray(new String[0]);

        String[] results = TableEditor.filter(rows, filterCond);

        List<String> resultList = Arrays.asList(results);

        tableEditor.deleteParticularChunks(tableEditor.findTableZByName(tableName)/16,resultList);
    }

    public Table findTable(String tableName){
        int tableZ = tableEditor.findTableZByName(tableName);
        if(tableZ == -1){
            throw new NullPointerException("[MyError] No such table: " + tableName);
        }

        Table table = tableEditor.getTableById(tableZ/16);
        if(table == null){
            throw new NullPointerException("[MyError] No such table: " + tableName + "[tableZ/16(id)]: " + String.valueOf(tableZ/16));
        }
        return table;
    }

    public String getJSONString(String[] fields, String[] values){
        String result = "{\n";
        for(int i = 0; i < fields.length; i++){
            result += '"' + fields[i] + '"' + ": " + '"' + values[i] + '"' + ",\n";
        }
        result += "}";
        return result;
    }

    public Predicate<JSONObject> parsePredicate(String condition){
        if(condition.contains("=") && !condition.contains(">") &&  !condition.contains("<")){
            String[] arr = condition.split("=");
            return (JSONObject obj) -> obj.getString(arr[0]).equals(arr[1]);
        }
        else if(!condition.contains("=") && !condition.contains(">") &&  condition.contains("<")){ // TODO:handle possible exceptions
            String[] arr = condition.split("<");
            return (JSONObject obj) -> obj.getDouble(arr[0]) < Double.parseDouble(arr[1]);
        }
        else if(!condition.contains("=") && condition.contains(">") &&  !condition.contains("<")){ // TODO:handle possible exceptions
            String[] arr = condition.split(">");
            return (JSONObject obj) -> obj.getDouble(arr[0]) > Double.parseDouble(arr[1]);
        }
        return null;
    }



}
