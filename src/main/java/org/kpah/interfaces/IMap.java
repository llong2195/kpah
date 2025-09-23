package org.kpah.interfaces;

import java.util.List;
import org.kpah.map.ChildMap;
import org.kpah.map.MapData;
import org.kpah.map.Zone;

public interface IMap {

    short getMapId();

    String getName();

    boolean isOfflineMap();

    boolean isChildMap();

    byte getCountry();

    void setCountry(byte country);

    void startUpdateMap();

    MapData getMapData();

    Runnable update();

    IMap getMapParent();

    List<ChildMap> getChildMaps();

    List<Zone> getZones();

    Zone getZone(int index);

    boolean isMapVillage();
}
