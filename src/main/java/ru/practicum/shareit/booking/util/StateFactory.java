package ru.practicum.shareit.booking.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class StateFactory {

    private Map<State, StateStrategy> strategies;

    @Autowired
    public StateFactory(Set<StateStrategy> strategySet) {
        createStrategy(strategySet);
    }

    public StateStrategy findStrategy(State state) {
        return strategies.get(state);
    }

    private void createStrategy(Set<StateStrategy> strategySet) {
        strategies = new HashMap<>();
        strategySet.forEach(strategy  -> strategies.put(strategy.getState(), strategy));
    }
}
