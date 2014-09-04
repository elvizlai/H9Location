package com.elvizlai.h9location.util;



import com.elvizlai.h9location.entity.MessagesInfo;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by elvizlai on 14-3-26.
 */
public class JSONUtil {
    private static final String json4test = "{\"success\":true,\"A\":{\"address\": \"address2\",\"name\":\"haha2\",\"id\":2,\"email\":\"email2\"}," +
            "\"B\":{\"address\":\"address\",\"name\":\"haha\",\"id\":1,\"email\":\"email\"}}";

    private static JsonGenerator jsonGenerator;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void close() {
        if (objectMapper != null)
            objectMapper = null;
        if (jsonGenerator != null)
            jsonGenerator = null;
    }

    public static String format(Object obj) {
        if (objectMapper == null)
            objectMapper = new ObjectMapper();
        String result = null;
        try {
            result = objectMapper.writeValueAsString(obj);
        } catch (JsonGenerationException jsongenerationexception) {
            jsongenerationexception.printStackTrace();
        } catch (JsonMappingException jsonmappingexception) {
            jsonmappingexception.printStackTrace();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return result;
    }

    public static List JsonStr2List(byte[] bytes) {
        return JsonStr2List(new String(bytes));
    }

    public static List JsonStr2List(String str) {
        if (!str.startsWith("["))
            str = "[" + str + "]";

        LogUtil.d("JsonStr: " + str);

        try {
            List<LinkedHashMap<String, Object>> list = objectMapper.readValue(str, List.class);
            return list;
//            for (int i = 0; i < list.size(); i++) {
//                Map<String, Object> map = list.get(i);
//                Set<String> set = map.keySet();
//                for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
//                    String key = it.next();
//                    System.out.println(key + ":" + map.get(key));
//                }
//            }

        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Map JsonStr2Map(byte[] bytes) {
        return JsonStr2Map(new String(bytes));
    }

    public static Map JsonStr2Map(String str) {
        LogUtil.d("JsonStr: " + str);

        try {
            Map<String, Map<String, Object>> maps = objectMapper.readValue(str, Map.class);
            return maps;
//            System.out.println(maps.size());
//            Set<String> key = maps.keySet();
//            Iterator<String> iter = key.iterator();
//            while (iter.hasNext()) {
//                String field = iter.next();
//                System.out.println(field + ":" + maps.get(field));
//            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends MessagesInfo> T parse(String paramString, Class<T> paramClass) throws POAException {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        MessagesInfo localMessagesInfo;
        try {
            if (jsonGenerator == null) {
                jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
            }
            localMessagesInfo = objectMapper.readValue(paramString, paramClass);
            if (localMessagesInfo.getSuccess() != 1) {
                throw new POAException(localMessagesInfo.getSuccess(), localMessagesInfo.getMes());
            }
        } catch (JsonParseException localJsonParseException) {
            localJsonParseException.printStackTrace();
            throw new POAException(25);
        } catch (JsonMappingException localJsonMappingException) {
            localJsonMappingException.printStackTrace();
            throw new POAException(25);
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
            throw new POAException(25);
        }
        return (T) localMessagesInfo;
    }

    public static <T> T parseForDB(String paramString, Class<T> paramClass) throws POAException {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            if (jsonGenerator == null) {
                jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
            }
            Object localObject = objectMapper.readValue(paramString, paramClass);
            return (T) localObject;
        } catch (JsonParseException localJsonParseException) {
            localJsonParseException.printStackTrace();
            throw new POAException(25);
        } catch (JsonMappingException localJsonMappingException) {
            localJsonMappingException.printStackTrace();
            throw new POAException(25);
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
            throw new POAException(25);
        }
    }

    public static <T> T parseWithNoMessageInfo(String paramString, Class<T> paramClass) throws POAException {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        try {
            if (jsonGenerator == null) {
                jsonGenerator = objectMapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);
            }
            Object localObject = objectMapper.readValue(paramString, paramClass);
            return (T) localObject;
        } catch (JsonParseException localJsonParseException) {
            localJsonParseException.printStackTrace();
            throw new POAException(25);
        } catch (JsonMappingException localJsonMappingException) {
            localJsonMappingException.printStackTrace();
            throw new POAException(25);
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
            throw new POAException(25);
        }
    }

}
