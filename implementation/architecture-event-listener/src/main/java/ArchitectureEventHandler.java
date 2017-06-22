import org.inaetics.dronessimulator.common.architecture.Action;
import org.inaetics.dronessimulator.common.architecture.State;

public interface ArchitectureEventHandler {
    //TODO use when atomic ChangedValuesAtNode is integrated
    // void handle(State fromState, Action action, State toState);
    void handle(State newState);
}
