package ru.gramant.thinkgear.utils;

import java.util.Collection;

/**
 * Created by fedor.belov on 12.12.13.
 */
public class CollectionUtils {

    public static Integer sum(Collection<Integer> collection) {
        if (collection == null || collection.size() == 0) {
            return 0;
        }

        Integer answer = 0;

        for (Integer value : collection) {
            answer += value;
        }

        return answer;
    }

}
