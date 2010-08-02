package standup;

public interface HasObservableState<Observed,StateType> {

	public StateType getState();
	public void addObserver(StateObserver<Observed,StateType> obs);
	public void removeObserver(StateObserver<Observed,StateType> obs);

}
