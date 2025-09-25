package org.kpah.template;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XaPhuTemplate {

    private byte id;

    private short[] mapID;

    private short[] x;

    private short[] y;

    private short[] price;
}
