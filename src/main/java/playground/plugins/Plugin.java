package playground.plugins;

import playground.logic.ActivityEntity;;

public interface Plugin {
	public Object execute (ActivityEntity command) throws Exception;
}
