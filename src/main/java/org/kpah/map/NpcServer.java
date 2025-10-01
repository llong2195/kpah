package org.kpah.map;

import org.kpah.template.NpcServerTemplate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NpcServer {

    private NpcServerTemplate template;
    private short x;
    private short y;
}
