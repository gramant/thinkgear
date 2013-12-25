package ru.gramant.thinkgear.raw.phase;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by fedor.belov on 07.11.13.
 */
public class PhaseConfigTest {

    @Test
    public void testCorrectParser() {
        String config = "Concentration: 0-0;0-0;0-0;0-0;1000000-88888888;0-0;410000-88888888;0-0;0-0;0-0;\n" +
                "Relaxation: 300000-88888888;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;\n" +
                "Sample: 0-88888888;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;";
        Phase[] phases = new Phase[]{
                PhaseTestUtils.getPhase("Concentration", MinMax.ZERO, MinMax.ZERO, MinMax.ZERO, MinMax.ZERO, new MinMax(1000000, 88888888), MinMax.ZERO, new MinMax(410000, 88888888)),
                PhaseTestUtils.getPhase("Relaxation", new MinMax(300000, 88888888)),
                PhaseTestUtils.getPhase("Sample", new MinMax(0, 88888888))};

        Phase[] answer = PhaseConfig.parseConfig(config);

        assert Arrays.equals(phases, answer);
    }

    @Test
    public void testCorrectSingleRowParser() {
        String config = "Sample: 0-88888888;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;";
        Phase[] phases = new Phase[]{PhaseTestUtils.getPhase("Sample", new MinMax(0, 88888888))};
        Phase[] answer = PhaseConfig.parseConfig(config);

        assert Arrays.equals(phases, answer);
    }

    @Test
    public void testIncorrectConfigValues() {
        String[] values = new String[] {
                null, "", "Concentration: 0-0;0-0;0-0;0-0;1000000-88888888;",
                "0-0;0-0;0-0;0-0;1000000-88888888;0-0;410000-88888888;0-0;0-0;0-0;",
                "Relaxation: 300000-88888888;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;\n" +"Sample: 0-88888888;0-0;0-0;0-0;0-0;0-0;0-0;0-0;0-0;"
        };

        for (String value : values) {
            assert PhaseConfig.parseConfig(value) == null;
        }
    }

}
