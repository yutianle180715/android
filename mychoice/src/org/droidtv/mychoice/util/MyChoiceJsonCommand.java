package org.droidtv.mychoice.util;

import java.util.HashMap;
import java.util.Map;

public class MyChoiceJsonCommand {
	/*
	 * command keys
	 */
	public final String COMMAND_ACTION = "Action";
	public final String COMMAND_MYCHOICEPIN = "MyChoicePIN";
	public final String COMMAND_STARTDATE = "StartDate";
	public final String COMMAND_STARTTIME = "StartTime";
	public final String COMMAND_STOPDATE = "StopDate";
	public final String COMMAND_STOPTIME = "StopTime";
	
	private Map<String, String> commandData;

	public MyChoiceJsonCommand(Map<String, String> commandData) {
		super();
		this.commandData = commandData;
	}

	public MyChoiceJsonCommand() {
		super();
		// TODO Auto-generated constructor stub
		commandData = new HashMap<String, String>();
	}

	public void clearCommandData() {
		synchronized (commandData) {
			commandData.clear();
		}
	}
	
	public String getCommandItemValue(String key) {
		String result = null;
		synchronized (commandData) {
			if (!commandData.containsKey(key)) {
				return result;
			}
			result = commandData.get(key);
		}
		return result;
	}
	
	public void setCommandItemValue(String key, String value) {
		synchronized (commandData) {
			commandData.put(key, value);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return commandData.toString();
	}

}
