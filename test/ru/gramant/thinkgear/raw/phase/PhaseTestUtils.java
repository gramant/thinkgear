package ru.gramant.thinkgear.raw.phase;

import ru.gramant.thinkgear.raw.Params;

/**
 * Created by fedor.belov on 07.11.13.
 */
public class PhaseTestUtils {

    public static Phase getPhase(String name, MinMax... params) {
        int i = 0;
        Phase answer = new Phase(name);
        String[] names = Params.getHistoryConfigNames();

        for(MinMax param : params) {
            answer.set(names[i], param);
            i++;
        }

        for(int j = i; j < names.length; j++) {
            answer.set(names[j], MinMax.ZERO);
        }

        return answer;
    }

}
