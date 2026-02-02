package Plugin.spigotPlugin;

import org.bukkit.Material;

import java.util.List;

public class WorldEditor implements StoringData<String> {
    public record location (int x, int y, int z){};
    public TableEditor tableEditor;

    public WorldEditor(){
        tableEditor = new TableEditor(this);
    }

    public void addTable(String tableName, List<String> cols){
        tableEditor.addTable(tableName, cols);
    }

    public location getFreeChunkLocation(int tableId){
        int x = 0;
        int y = -60;
        int z = tableId*16;
        while(DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(x,y,z).getType()) != null){
            x += 16;
        }
        return new location(x,y,z);
    }

    public String getAllData(){
        int x = 0;
        int y = -60;
        int z = 0;
        int i =0;
        String allData = "";

        while(DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(x,y,z).getType()) != null){
            String message = readChunk(i);
            String translatedMessage = DataUtils.translateToStringFromHex(message);                  // TODO: optimize this shit
            if(translatedMessage.split(";").length > 1){
                translatedMessage = translatedMessage.split(";")[1];
            }
            message = DataUtils.translateToHex(translatedMessage);
            i++;
            x += 16;
            allData = allData + message + DataUtils.translateToHex(";");
        }
        return allData;
    }

    public void clearChunks(int tableId){
        int chunkId = 0;
        Table table = tableEditor.getTableById(tableId);
        if(table == null) return;
        while(table.tableSize > chunkId){
            clearChunk(chunkId,tableId);
            chunkId++;
        }
    }

    public void clearChunk(int id, int tableId){ // There is a way to optimize this: just stop deleting when the series of correct blocks ends

        int x =id*16, y = -60, z = tableId*16;
            for(int i = 0; i < 16; i++){
                for(int j = -60; j < 255; j++){
                    for(int k = 0; k  < 16; k++){
                        SpigotPlugin.world.getBlockAt(x+i,j,z+k).setType(Material.AIR);
                    }
                }
            }

    }

    public String readChunk(int chunkId){
        return readChunk(chunkId,0);
    }

    public String readChunk(int chunkId, int tableId){

        int startX = chunkId * 16;
        int x = startX;
        int y = -60;
        int z = tableId*16;
        String result = "";
        while(true){
            if(x - startX >= 16){
                if(z >=15){
                    z = 0;
                    y++;
                }else z++;
                x = startX;
            }
            Character c = DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(x,y,z).getType());
            if(c == null) break;
            result += c;
            x++;
        }
        return result;
    }


    public Material setMaterialFromHex(char hex){
        Material material = DataUtils.CHAR_TO_MATERIAL.get(hex);
        return material == null? Material.REDSTONE : material;
    }


    public void buildChunk(String message, int startX, int startY, int startZ){ // TODO: change name to writeToChunk()
        int x = startX,y = startY,z = startZ;
        for(int i = 0; i < message.length(); i++){
            Material material = setMaterialFromHex(message.charAt(i));
            SpigotPlugin.world.getBlockAt(x,y,z).setType(material);
            location nextLoc = findNextXYZ((startX)/16,new location(x,y,z));
            x = nextLoc.x;
            y = nextLoc.y;
            z = nextLoc.z;
        }

    }


    public void overrideChunk(String message, int startX, int startY, int startZ){ // im not sure if its working correctly TODO: test it
        int x = startX,y = startY,z = startZ;
        for(int i = 0; i < message.length(); i++){
            if(x - startX == 16){

                if(z - startZ >= 15){
                    y++;
                    z = startZ;
                }else z++;

                x = startX;
            }
            Material material = setMaterialFromHex(message.charAt(i));
            SpigotPlugin.world.getBlockAt(x,y,z).setType(material);
            x++;
        }
    }

    private location findNextXYZ(int chunkId){
        return findNextXYZ(chunkId, new location(0,0,0));
    }

    private location findNextXYZ(int chunkId, location loc){
        int x = loc.x;
        int y = loc.y;
        int z = loc.z;
        int i = 0;
        while(DataUtils.MATERIAL_TO_CHAR.get(SpigotPlugin.world.getBlockAt(x,y,z).getType()) != null){
            if(x >= chunkId*16+15){
                if(z >= 15){
                    z = 0;
                    y++;
                }else z++;
                x = chunkId*16;
            }else{
                x++;
            }
            i++;
        }
        return new location(x,y,z);
    }


    @Override
    public void addData(String message){
        location loc = getFreeChunkLocation(0);
        int startX = loc.x; int startY = loc.y; int startZ = loc.z;

        buildChunk(message,startX,startY,startZ);
        //tableEditor.increaseLength(startZ);
    }
}
