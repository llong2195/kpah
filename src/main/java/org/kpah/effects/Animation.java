package org.kpah.effects;

import org.json.JSONArray;
import org.json.JSONException;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Animation {

    private byte[] frame;

    @Override
    public String toString() {
        try {
            JSONArray arr = new JSONArray(frame);
            return arr.toString();
        } catch (JSONException ex) {
        }
        return "[]";
    }
}
