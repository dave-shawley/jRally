package standup.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import standup.HasObservableState;
import standup.StateObserver;


/**
 * A group of {@link UserStory} objects worked together.
 */
public class Iteration
	implements HasObservableState<Iteration, Iteration.State>, Iterable<UserStory>
{

	/**
	 * The state of an iteration.
	 */
	public enum State {
		PLANNED, ACTIVE, COMPLETED
	}

	private String name;
	private State state;
	private Date startDate;
	private Date endDate;
	private final List<UserStory> stories;
	private final List<StateObserver<Iteration, State>> observers;


	/**
	 * @param name
	 * @param startDate
	 * @param endDate
	 */
	public Iteration(String name, Date startDate, Date endDate) {
		this.name = name;
		this.state = null;
		this.startDate = startDate;
		this.endDate = endDate;
		this.stories = new ArrayList<UserStory>();
		this.observers = new ArrayList<StateObserver<Iteration, State>>();

		// TODO verify that startDate < endDate

		resetState();
	}

	/**
	 * Utility function to set the state based on the start
	 * and end dates. 
	 */
	private void resetState() {
		State newState = calculateStateForDates(this.startDate, this.endDate);
		if (this.state == null) {
			this.state = newState;
		} else {
			synchronized(this.state) {
				if (this.state != newState) {
					for (StateObserver<Iteration,State> obs : this.observers) {
						obs.update(this, this.state, newState);
					}
					this.state = newState;
				}
			}
		}
	}
	
	/**
	 * Helper function for determining iteration state.
	 * @param startDate start date to probe.
	 * @param endDate end date to probe.
	 * @return The state for an iteration starting on <code>startDate</code>
	 *         and ending on <code>endDate</code>.
	 */
	public static State calculateStateForDates(Date startDate, Date endDate) {
		State state = null;
		Date now = new Date();
		int compareToStart = now.compareTo(startDate);
		int compareToEnd = now.compareTo(endDate);
		if (compareToStart >= 0 && compareToEnd < 0) {
			state = State.ACTIVE;
		} else if (compareToStart < 0) {
			state = State.PLANNED;
		} else {
			state = State.COMPLETED;
		}
		return state;
	}

	/**
	 * @param story a story to add to the iteration.
	 */
	public void addStory(UserStory story) {
		stories.add(story);
	}
	
	/**
	 * @param index the zero-based index of the story to retrieve.
	 * @return The story at the specified index.
	 * @see List#get(int)
	 */
	public UserStory getStory(int index) {
		return stories.get(index);
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<UserStory> iterator() {
		return stories.iterator();
	}

	/**
	 * @param name the new name for the iteration.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The name of this iteration.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The state of this iteration.
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param startDate the new start date for the iteration.
	 * @throws IllegalArgumentException
	 *         when startDate is later than the current end date. 
	 */
	public void setStartDate(Date startDate) throws IllegalArgumentException {
		if (startDate.after(this.getEndDate())) {
			throw new IllegalArgumentException(startDate.toString() +
					                           " is after the current end date");
		}
		this.startDate = startDate;
		resetState();
	}

	/**
	 * @return The starting date for this iteration.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param endDate the new end date for the iteration.
	 * @throws IllegalArgumentException
	 *         when endDate is earlier than the current start date. 
	 */
	public void setEndDate(Date endDate) {
		if (endDate.before(this.getStartDate())) {
			throw new IllegalArgumentException(endDate.toString() +
					                           " is before the current start date");
		}
		this.endDate = endDate;
		resetState();
	}

	/**
	 * @return The ending date for this iteration.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/*
	 * (non-Javadoc)
	 * @see standup.HasObservableState#addObserver(standup.StateObserver)
	 */
	public void addObserver(StateObserver<Iteration, State> obs) {
		this.observers.add(obs);
	}

	/*
	 * (non-Javadoc)
	 * @see standup.HasObservableState#removeObserver(standup.StateObserver)
	 */
	public void removeObserver(StateObserver<Iteration, State> obs) {
		this.observers.remove(obs);
	}

	/**
	 * @return The total number of stories.
	 */
	public int getStoryCount() {
		return this.stories.size();
	}

}
