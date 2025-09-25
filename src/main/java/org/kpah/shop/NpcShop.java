package org.kpah.shop;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NpcShop {

    private byte id;
    private String nameShop;
    private byte typeShop;
    private short[] idItems;
}
