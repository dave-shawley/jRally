package standup.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import standup.HasObservableState;
import standup.StateObserver;


/**
 * A user story is an actionable list of tasks.
 * 
 * The story has an associated task list that is made available using the
 * {@link Iterable} interface as well as simple accessors
 * {@link UserStory#getTask} and {@link UserStory#getNumberOfTasks}.
 */
public class UserStory
	implements HasObservableState<UserStory, UserStory.State>, Iterable<Task>
{

	/**
	 * The state of a story.
	 */
	public enum State {
		NOT_STARTED, IN_PROGRESS, COMPLETED, ACCEPTED
	}

	private String storyName;
	private final List<Task> taskList;
	private long storyPoints;
	private State state;
	private final List<StateObserver<UserStory,UserStory.State>> stateObservers;

	/**
	 * Create a story.
	 * Stories begin their life as {@link State#NOT_STARTED} without any
	 * tasks and a story point estimate of zero. 
	 * @param storyName    The name of the new story.
	 */
	public UserStory(String storyName) {
		this.setStoryName(storyName);
		this.state = State.NOT_STARTED;
		this.taskList = new LinkedList<Task>();
		this.stateObservers = new ArrayList<StateObserver<UserStory,UserStory.State>>();
		this.storyPoints = 0;
	}

	/**
	 * @return the name of the story.
	 */
	public String getStoryName() {
		return storyName;
	}

	/**
	 * @param storyName the new name for this story.
	 */
	public void setStoryName(String storyName) {
		this.storyName = storyName;
	}

	/**
	 * @return The sum total of the task's estimates.
	 */
	public double getDetailedEstimate() {
		double estimate = 0.0;
		for (Task t : this) {
			estimate += t.getDetailedEstimate();
		}
		return estimate;
	}

	/**
	 * @return The number of points that this story is worth.
	 */
	public long getStoryPoints() {
		return storyPoints;
	}

	/**
	 * @param storyPoints The number of points that this story is worth.
	 */
	public void setStoryPoints(long storyPoints) {
		this.storyPoints = storyPoints;
	}

	/**
	 * @return The amount of effort remaining on this story.
	 */
	public double getToDo() {
		double todoRemaining = 0.0;
		for (Task t: this) {
			todoRemaining += t.getToDo();
		}
		return todoRemaining;
	}

	/**
	 * @param state The new state for the story.
	 */
	public void setState(State state) {
		synchronized(this.state) {
			for (StateObserver<UserStory,State> obs: this.stateObservers) {
				obs.update(this, this.state, state);
			}
			this.state = state;
		}
	}

	/* (non-Javadoc)
	 * @see standup.HasObservableState#getState()
	 */
	public State getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see standup.HasObservableState#addObserver(standup.StateObserver)
	 */
	public void addObserver(StateObserver<UserStory, State> obs) {
		stateObservers.add(obs);
	}

	/* (non-Javadoc)
	 * @see standup.HasObservableState#removeObserver(standup.StateObserver)
	 */
	public void removeObserver(StateObserver<UserStory, State> obs) {
		stateObservers.remove(obs);
	}

	/**
	 * @return The number of tasks associated with this story.
	 */
	public int getNumberOfTasks() {
		return this.taskList.size();
	}

	/**
	 * Add a task to the user story.
	 * 
	 * If the story has already been completed, then it is placed back
	 * into progress since there is a new story.
	 * 
	 * @param task  the task to associate with this story.
	 */
	public void addTask(Task task) {
		synchronized(this.state) {
			this.taskList.add(task);
			if (this.state == State.COMPLETED || this.state == State.ACCEPTED) {
				this.setState(State.IN_PROGRESS);
			}
		}
	}

	/**
	 * @param index   index of the task to retrieve.
	 * @return The task at the specified index.
	 */
	public Task getTask(int index) {
		return this.taskList.get(index);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Task> iterator() {
		return this.taskList.iterator();
	}

}
