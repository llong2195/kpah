package org.kpah.map;

import org.json.JSONArray;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WayPoint {

    private String nameWayPoint;
    private short toMap;
    private short toX;
    private short toY;

    @Override
    public String toString() {
        JSONArray arr = new JSONArray();
        arr.put(nameWayPoint);
        arr.put(toMap);
        arr.put(toX);
        arr.put(toY);
        return arr.toString();
    }
}
