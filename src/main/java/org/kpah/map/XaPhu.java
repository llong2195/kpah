package org.kpah.map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XaPhu {

    private byte id;
    private short idMap;
    private short x;
    private short y;

}
