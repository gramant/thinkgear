package ru.gramant.thinkgear.phase;

import org.junit.Test;

import ru.gramant.thinkgear.Params;

/**
 * Created by fedor.belov on 07.11.13.
 */
public class PhaseTest {

    @Test
    public void testPhaseChangeState() {
        MinMax doesNotMatter = new MinMax(0, 999999999);

        Phase phase = PhaseTestUtils.getPhase("test", doesNotMatter, new MinMax(200, 500), doesNotMatter, new MinMax(10, 50));
        State[] states = new State[]{
                new State(false, false, new Params(4, 10, 0, 15, 0, 0, 0, 0, 0, 0)),
                new State(true, true, new Params(999, 366, 999, 15, 0, 0, 7, 99, 12, 0)),
                new State(false, true, new Params(4, 250, 0, 45, 0, 0, 12, 88, 0, 0)),
                new State(true, false, new Params(4, 350, 0, 55, 0, 0, 0, 0, 7, 12)),
                new State(false, false, new Params(0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        };

        checkPhase(phase, states);

        phase = PhaseTestUtils.getPhase("test-other-params", MinMax.ZERO, MinMax.ZERO, MinMax.ZERO, MinMax.ZERO, new MinMax(90, 100), doesNotMatter, new MinMax(500, 800), doesNotMatter, doesNotMatter, new MinMax(1300, 2000));
        states = new State[]{
                new State(false, false, new Params(4, 10, 0, 15, 200, 700, 400, 987, 77, 1500)),
                new State(true, true, new Params(999, 366, 999, 15, 95, 987, 600, 9, 12, 1900)),
                new State(false, true, new Params(4, 350, 0, 55, 99, 0, 500, 0, 7, 2000)),
                new State(true, false, new Params(4, 250, 0, 45, 0, 0, 12, 88, 0, 0)),
                new State(false, false, new Params(0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        };

        checkPhase(phase, states);
    }



    private void checkPhase(Phase phase, State[] states) {
        for (State state : states) {
            boolean answer = phase.checkStateChange(state.params);
            assert state.changed == answer;
            assert state.isActive == phase.isActive();
        }
    }

    private static class State {
        Params params;
        boolean changed;
        boolean isActive;

        private State( boolean changed, boolean active, Params params) {
            this.params = params;
            this.changed = changed;
            isActive = active;
        }
    }

}
