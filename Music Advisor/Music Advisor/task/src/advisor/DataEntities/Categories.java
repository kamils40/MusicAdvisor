package advisor.DataEntities;

import java.util.Map;

public class Categories {
    Map<String, String> categoryMap;

    public Categories(Map<String, String> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public Map<String, String> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(Map<String, String> categoryMap) {
        this.categoryMap = categoryMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : categoryMap.keySet()) {
            sb.append(s + "\n");
        }
        return sb.toString();
    }
}
