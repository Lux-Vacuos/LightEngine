/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
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

package net.luxvacuos.lightengine.universal.core.subsystems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.loader.EngineData;
import net.luxvacuos.lightengine.universal.resources.config.SubsystemConfig;
import net.luxvacuos.lightengine.universal.util.IEvent;

public class EventSubsystem extends Subsystem<SubsystemConfig> {

	private static Map<String, List<IEvent>> events = new HashMap<>();

	@Override
	public void init(EngineData ed) {
	}

	@Override
	public void dispose() {
		events.clear();
	}

	public static IEvent addEvent(String key, IEvent event) {
		var eventList = events.get(key);
		if (eventList != null)
			eventList.add(event);
		else {
			List<IEvent> newEventList = new ArrayList<>();
			newEventList.add(event);
			events.put(key, newEventList);
		}
		return event;
	}

	public static boolean removeEvent(String key, IEvent event) {
		var eventList = events.get(key);
		if (eventList != null)
			return eventList.remove(event);
		else
			return true;
	}

	public static void triggerEvent(String key) {
		TaskManager.tm.addTaskBackgroundThread(() -> {
			var eventList = events.get(key);
			if (eventList != null)
				for (IEvent event : eventList)
					event.onTrigger();
		});
	}

}
