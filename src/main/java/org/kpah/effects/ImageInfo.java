package org.kpah.effects;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageInfo {

    private byte w;

    private byte h;

    private short ID;

    private short x0;

    private short y0;

    @Override
    public String toString() {
        try {
            JSONObject arr = new JSONObject();
            arr.put("id", ID);
            arr.put("w", w);
            arr.put("h", h);
            arr.put("x0", x0);
            arr.put("y0", y0);
            return arr.toString();
        } catch (JSONException ex) {
        }
        return "{}";
    }
}
