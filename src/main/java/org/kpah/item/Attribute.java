package org.kpah.item;

import lombok.Data;
import org.kpah.manager.Manager;
import org.json.JSONArray;
import org.kpah.template.AttributeEquipTemplate;

/**
 *
 * @author ☂️☂️Duy Coder 💖💖
 */
@Data
public class Attribute {

    private AttributeEquipTemplate template;
    private short value;

    public Attribute(short id, short value) {
        this.template = Manager.getAttributeTemplate(id);
        this.value = value;
    }

    public String getInfo() {
        StringBuilder builder = new StringBuilder(template.getName()).append(": ");
        switch (template.getIsPercent()) {
            case 0 ->
                builder.append(value);
            case 1 ->
                builder.append(value).append("%");
            case 2 -> {
                builder.append(value / 10).append(".").append(value % 10).append("%");
            }
        }
        return builder.toString();
    }

    public void dispose() {
        template = null;
    }

    public void plusValue(short plus) {
        if (plus <= 0 || value + plus <= 0) {
            return;
        }
        value += plus;
    }

    @Override
    public String toString() {
        JSONArray arr = new JSONArray();
        arr.put(template.getId());
        arr.put(value);
        return arr.toString();
    }
}
