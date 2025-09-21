package org.kpah.map;

import lombok.Builder;
import lombok.Data;
import org.kpah.template.NpcServerTemplate;

/**
 *
 * @author ☂️☂️Duy Coder 💖💖
 */
@Data
@Builder
public class NpcServer {

    private NpcServerTemplate template;
    private short x;
    private short y;
}
