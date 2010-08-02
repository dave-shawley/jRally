package standup;

public interface StateObserver<Observed,StateType> {
	public void update(HasObservableState<Observed,StateType> obs,
                       StateType oldState, StateType newState);
}
