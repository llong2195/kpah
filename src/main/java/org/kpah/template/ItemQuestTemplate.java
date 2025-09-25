package org.kpah.template;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemQuestTemplate {

    private byte id;
    private String name;
    private short idIcon;
}
