package standup.model;

import java.util.ArrayList;
import java.util.List;

import standup.HasObservableState;
import standup.StateObserver;

/**
 * The unit of work.
 * 
 * A task is the smallest unit of work in a {@link UserStory}.  A task
 * has a detailed estimate, a remaining amount of work, and an amount of
 * effort applied.  These attributes share the same base of measurement
 * which is usually hours.
 * 
 * Most of the attributes of a task are treated as being completely
 * independent.  The one exception is that the state of the task and the
 * "effort remaining" (to do) or "effort applied".  If effort is applied
 * to a task that has been completed, then it is moved back into the
 * {@link State#IN_PROGRESS} state.  The same is true for any modification
 * of "to do"; increasing the "effort remaining" will place the task back
 * into progress.  When a task is moved into the {@link State#FINISHED}
 * state, then the "effort remaining" is set to zero automatically.
 */
public class Task
	implements HasObservableState<Task, Task.State>
{

	/**
	 * The state of the task.
	 */
	public enum State {
		NOT_STARTED, IN_PROGRESS, WAITING, BLOCKED, FINISHED
	}

	private String taskName;
	private double toDoRemaining;
	private double effort;
	private State state;
	private final List<StateObserver<Task,Task.State>> observers;
	private double detailedEstimate;

	/**
	 * Create a new task with the specified information.
	 * 
	 * The newly created task is in the {@link State#NOT_STARTED} state
	 * with an "effort remaining" equal to the initial detailed estimate.
	 * The initial effort is also set to zero.
	 * 
	 * @param taskName The name of the new task.
	 * @param detailedEstimate The expected effort that this task will require.
	 */
	public Task(String taskName, double detailedEstimate) {
		this.observers = new ArrayList<StateObserver<Task,Task.State>>();
		this.setTaskName(taskName);
		this.setDetailedEstimate(detailedEstimate);
		this.state = State.NOT_STARTED;
		this.toDoRemaining = detailedEstimate;
	}

	/**
	 * @return The name of this task,
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @return The estimated effort for this task.
	 */
	public double getDetailedEstimate() {
		return detailedEstimate;
	}

	/**
	 * @param taskName the new name for the task.
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @param detailedEstimate the new effort estimate.
	 */
	public void setDetailedEstimate(double detailedEstimate) {
		this.detailedEstimate = detailedEstimate;
	}

	/**
	 * @return The amount of effort remaining.
	 */
	public double getToDo() {
		return toDoRemaining;
	}

	/**
	 * Increase the remaining effort.
	 * @param amount the additional effort that this task will require.
	 */
	public void increaseToDo(double amount) {
		// TODO add confirmation
		if (amount < 0.0) {
			throw new IllegalArgumentException("increaseToDo requires a non-negative value");
		}
		synchronized(this) {
			this.toDoRemaining += amount;
			if (this.getState() == State.FINISHED) {
				this.setState(State.IN_PROGRESS);
			}
		}
	}

	/**
	 * Decrease the remaining effort.
	 * @param amount the amount of effort to remove.
	 */
	public void decreaseToDo(double amount)	{
		if (amount < 0.0) {
			throw new IllegalArgumentException("decreaseToDo requires a non-negative value");
		}
		this.toDoRemaining -= amount; 
	}

	/**
	 * Sets the task's current state.
	 * If the {@code state} argument is {@link State#FINISHED}, then the
	 * remaining effort is set to zero. 
	 * @param state the new state of the task.
	 */
	public void setState(State state) {
		synchronized(this.state) {
			if (this.state != state) {
				for (StateObserver<Task,State> obs: this.observers) {
					obs.update(this, this.state, state);
				}
				this.state = state;
				if (this.state == State.FINISHED) {
					this.toDoRemaining = 0.0;
				}
			}
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
	public void addObserver(StateObserver<Task,Task.State> obs) {
		this.observers.add(obs);
	}

	/* (non-Javadoc)
	 * @see standup.HasObservableState#removeObserver(standup.StateObserver)
	 */
	public void removeObserver(StateObserver<Task,Task.State> obs) {
		this.observers.remove(obs);
	}

	/**
	 * @return The total amount of effort applied to this task.
	 */
	public double getEffortApplied() {
		return effort;
	}

	/**
	 * Increase the amount of effort applied.
	 * @param effortApplied the amount of effort applied to this task.
	 */
	public void applyEffort(double effortApplied) {
		// TODO is negative effort allowed?
		if (this.getState() == State.NOT_STARTED || this.getState() == State.FINISHED) {
			this.setState(State.IN_PROGRESS);
		}
		this.effort += effortApplied;
	}

}
