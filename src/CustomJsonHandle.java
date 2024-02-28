import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class CustomJsonHandle{

    public static JsonElement GetJsonElementByMultiKey(JsonObject jObject, String key)
    {
        String[] lstKey = key.split("\\.");
        JsonObject jObjCopy = jObject.deepCopy();
        JsonElement jEle = null;
        int keyCount = lstKey.length;
        try 
        {
            for (int i = 0; i < keyCount; i++) {
                String str = lstKey[i];
                if(i == keyCount -1 ){
                    jEle = jObjCopy.get(str);
                    return jEle;
                }
                
                if(jObjCopy.get(str).isJsonArray()){
                    return GetElementFromArray(jObjCopy.get(str),lstKey, i);
                }
                else{                    
                    jObjCopy = jObjCopy.getAsJsonObject(str);
                }
            }
            return jEle;
        } catch (Exception e) {
            throw e;
            
        }        
    } 

    private static JsonElement GetElementFromArray(JsonElement jElement, String[] lstKey, int currentPos){
        try {            
            JsonArray jArray = jElement.getAsJsonArray();
            List<String> lstValue = new ArrayList<String>();
            int countKey = lstKey.length;
            int nextPos = currentPos + 1;
            for(JsonElement jEl:jArray){
                if(nextPos == countKey){
                    lstValue.add(jEl.getAsString());
                }else{                
                    JsonObject jObj = jEl.getAsJsonObject();                
                    for(int i = nextPos; i < countKey; i++){
                        JsonElement tmpElement =jObj.get(lstKey[i]);
                        if(i == countKey -1 ){
                            lstValue.add(tmpElement.getAsString());
                            continue;
                        }
                        if(tmpElement.isJsonArray()){
                            return GetElementFromArray(tmpElement,lstKey,i);
                        }else{
                            jObj = jObj.getAsJsonObject(lstKey[i]);
                        }
                    }
                }
            }
            String propName = lstKey[countKey - 1];
            String value = String.join("#",lstValue);
            JsonObject result = new JsonObject();
            result.addProperty(propName, value);
            JsonElement elRsl = result.get(propName); 
            return elRsl;
        } catch (Exception e) {
            throw e;
        }
    }
    public static String GetMultiLevelValueConfig(String kName, String filePath) throws Exception
    {
        FileReader fReader = new FileReader(filePath);
        try
        {            
            return GetMultiLevelValueConfig(kName, fReader);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw e;
        }finally{
            fReader.close();
        }
    }

    public static String GetMultiLevelValueConfig(String kName, FileReader fReader) throws Exception
    {
        try (JsonReader jReader = new JsonReader(fReader)) 
        {            
            String kValue = GetMultiLevelValueConfig(kName, jReader);
            return kValue;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw e;
        }
    }

    public static String GetMultiLevelValueConfig(String kName, JsonReader jReader) throws Exception
    {
        try
        {            
            String kValue;
            JsonObject jObject = JsonParser.parseReader(jReader).getAsJsonObject();
            kValue = CustomJsonHandle.GetJsonElementByMultiKey(jObject, kName).getAsString();
            return kValue;
        } catch (JsonIOException | JsonSyntaxException e) {
            throw e;
        }
    }
}