import org.inaetics.dronessimulator.common.architecture.State;

public interface ArchitectureEventListener {
    void setHandler(State newState, ArchitectureEventHandler handler);
}
