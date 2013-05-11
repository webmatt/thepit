package core.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import core.model.Dude.State;

public class Dude extends Rectangle {
	public enum State {
		IDLE, WALKING, JUMPING
	}

	public static final float SIZE = 0.5f; // half a unit

	private Vector2 acceleration = new Vector2();
	private Vector2 velocity = new Vector2();
	private State state = State.IDLE;
	private boolean facingLeft = true;
	private float stateTime = 0;
	
	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		if (!this.state.equals(state))
		{
			setStateTime(0f);
		}
		this.state = state;
	}

	public boolean isFacingLeft() {
		return facingLeft;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

	public Dude(Vector2 position)
	{
		set(position.x, position.y, SIZE, SIZE);
	}

	public void update(float delta) {
//		getPosition().add(getVelocity().cpy().scl(delta));
		stateTime += delta;
	}

	public void add (Vector2 v) {
		x += v.x;
		y += v.y;
	}
}
