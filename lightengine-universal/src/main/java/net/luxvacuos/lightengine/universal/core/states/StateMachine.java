/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.universal.core.states;

import java.util.HashMap;
import java.util.Map;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.universal.core.EngineType;

public final class StateMachine {

	private static IState currentState, previousState;

	private static EngineType engineType;

	private static InternalState internalState = InternalState.STOPPED;

	private static Map<String, IState> registeredStates = new HashMap<String, IState>();

	private StateMachine() {
	}

	public static void run() {
		Logger.log("StateMachine running");
		internalState = InternalState.RUNNING;
	}

	public static void stop() {
		internalState = InternalState.STOPPED;
	}

	public static boolean isRunning() {
		return internalState == InternalState.RUNNING || internalState == InternalState.LOADING;
	}

	public static boolean registerState(IState state) {
		if (!registeredStates.containsKey(state.getName())) {
			Logger.log("Registering State: " + state.getName());
			state.init();
			registeredStates.put(state.getName(), state);
			return true;
		} else
			return false;
	}

	public static boolean hasState(String state) {
		return registeredStates.containsKey(state);
	}

	public static boolean update(float deltaTime) {
		if (currentState == null)
			return false;
		if (internalState == InternalState.LOADING)
			return false;
		currentState.update(deltaTime);
		return true;
	}

	public static boolean render(float delta) {
		if (currentState == null || engineType != EngineType.CLIENT)
			return false;
		if (internalState == InternalState.LOADING)
			return false;
		currentState.render(delta);
		return true;
	}

	public static boolean setCurrentState(/* @Nonnull */ String name) {
		if (registeredStates.containsKey(name)) {
			internalState = InternalState.LOADING;
			IState state = registeredStates.get(name);
			Logger.log("Setting current state to " + state.getName());
			if (currentState != null) {
				if (currentState.equals(state)) {
					internalState = InternalState.RUNNING;
					return false;
				}

				currentState.end();
				previousState = currentState;
			}

			currentState = state;
			currentState.start();
			internalState = InternalState.RUNNING;
			return true;
		} else
			return false;
	}

	public static void setEngineType(EngineType type) {
		engineType = type;
	}

	public static IState getCurrentState() {
		return currentState;
	}

	public static IState getPreviousState() {
		return previousState;
	}

	public static void dispose() {
		internalState = InternalState.STOPPED;
		for (IState state : registeredStates.values())
			if (state.isRunning())
				state.end();
		registeredStates.clear();
	}

	public static InternalState getInternalState() {
		return internalState;
	}

	public enum InternalState {
		STOPPED, RUNNING, LOADING
	}

}
