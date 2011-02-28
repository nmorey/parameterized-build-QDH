package parameterizedbuildQDH;

import hudson.Util;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.Queue.Task;
import hudson.model.Queue.QueueDecisionHandler;
import hudson.model.Queue.Item;
import hudson.model.ParametersAction;
import hudson.model.ParameterValue;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.kohsuke.stapler.DataBoundConstructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Level.FINE;

@Extension
public class QueueDecisionHandlerPB extends QueueDecisionHandler {
    // private static final Logger LOGGER = Logger.getLogger(QueueDecisionHandlerPB.class.getName());
    @Override
    public boolean shouldSchedule(Task p, List<Action> actions) {	    
	    boolean list_match=true;
	    List<ParametersAction> param_actions = Util.filter(actions, ParametersAction.class);
	    ParametersAction param_action = null;
	    List<String> checkedParams;

	    // LOGGER.log(Level.INFO, "Entering QDHPB");

	    /* No parameters */
	    if(param_actions.isEmpty()){
		    // LOGGER.log(Level.INFO, "Build has no parameter. Return true");
		    return true;
	    }

	    /* There is no real list, this a a hack to get the first one ! */
	    for (ParametersAction action: param_actions)
		    param_action = action;

	    /* FIXME : Generate list of compared param names */
	    Job job = (Job)p;
	    QDHProperty prop = (QDHProperty)job.getProperty(QDHProperty.class);
	    if (prop == null){
		    // LOGGER.log(Level.INFO, "Build has no QDHProperty. Return true");
		    return true;
	    }

	    checkedParams = prop.getMergeParamsArray();
	    if (checkedParams.isEmpty()){
		    // LOGGER.log(Level.INFO, "Build has an QDHProperty with no params. Return true");
		    return true;
	    }

	    /* Match parameters with elements in the queue */
	    for(Item item : Hudson.getInstance().getQueue().getItems(p)) {
		boolean param_match=true;
		// LOGGER.log(Level.INFO, "Treating Item: " + item.getDisplayName() );
    		for (ParametersAction action: item.getActions(ParametersAction.class)) {
			for(String param_name : checkedParams){
				ParameterValue val1 = param_action.getParameter(param_name);
				ParameterValue val2 = action.getParameter(param_name);
				
				if((val1 == null && val2 != null) || (val1 != null && val2 == null)){
					// LOGGER.log(Level.INFO, "Mismatch on " + param_name + ". Next Item");
					param_match = false;
					break;
				} else if(val1 == null && val2 == null){
					// LOGGER.log(Level.INFO, "No parem " + param_name + " on either. Next param");
					continue;
				} else if( ! val1.equals(val2)){
					// LOGGER.log(Level.INFO, "Mismatch on " + param_name + ". Next item");
					param_match = false;
					break;
				} else {
					// LOGGER.log(Level.INFO, "Match on " + param_name + ". Next param");
				}
			}
			if(param_match == false)
				break;
		}
		if(param_match == true){
			// LOGGER.log(Level.INFO, "We found a match. We should not add it");
			list_match=false;
			break;
		}
	    }
	    // LOGGER.log(Level.INFO, "Result is " + list_match);
	    return list_match;
    }
}
