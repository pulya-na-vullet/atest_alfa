package tests.collection.core;

import java.util.Map;

public abstract class BaseSectionTest {
    protected static final Map<String, Scenario> SCENARIOS = ScenarioCatalog.all();
    protected static final ScenarioExecutor EXECUTOR = new ScenarioExecutor();

    protected void run(String id) {
        EXECUTOR.execute(SCENARIOS.get(id));
    }
}
