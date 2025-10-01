package org.kpah.player;

import org.json.JSONArray;
import org.json.JSONException;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Skill {

    private byte[] levelSkill;
    private long[] timeLastUseSkills;
    private byte typeSkill;
    private byte typeBuffSkill;

    @Override
    public String toString() {
        JSONArray skills = new JSONArray();
        try {
            skills.put(new JSONArray(levelSkill));
        } catch (JSONException ex) {
        }
        return skills.toString();
    }
}
