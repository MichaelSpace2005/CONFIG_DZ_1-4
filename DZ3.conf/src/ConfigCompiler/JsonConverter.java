package ConfigCompiler;

import java.util.List;
import java.util.Map;

public class JsonConverter {
    public static String toJson(Object object) {
        if (object instanceof List) {
            return listToJson((List<?>) object);
        } else if (object instanceof Map) {
            return mapToJson((Map<?, ?>) object);
        } else if (object instanceof String) {
            return "\"" + object + "\"";
        } else {
            return String.valueOf(object);
        }
    }

    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":").append(toJson(entry.getValue()));
            if (i < map.size() - 1) {
                sb.append(",");
            }
            i++;
        }
        sb.append("}");
        return sb.toString();
    }
}