package org.kpah.effects;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartFrame {

    private short dx;

    private short dy;

    private byte idSmallImg;

    private byte flip;

    private byte onTop;

    private byte xShadow;

    private byte yShadow;

    @Override
    public String toString() {
        try {
            JSONObject arr = new JSONObject();
            arr.put("id", idSmallImg);
            arr.put("dx", dx);
            arr.put("dy", dy);
            arr.put("onTop", onTop);
            arr.put("flip", flip);
            arr.put("xShadow", xShadow);
            arr.put("yShadow", yShadow);
            return arr.toString();
        } catch (JSONException ex) {
        }
        return "{}";
    }
}
