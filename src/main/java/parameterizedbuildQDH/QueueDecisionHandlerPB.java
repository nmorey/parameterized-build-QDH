package hudson.plugins.parameterizedbuildQDH;

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

@Extension
public class QueueDecisionHandlerPB extends QueueDecisionHandler {
    
    @Override
    public boolean shouldSchedule(Task p, List<Action> actions) {	    
	    boolean list_match=false;
	    List<ParametersAction> param_actions = Util.filter(actions, ParametersAction.class);
	    ParametersAction param_action = null;
	    List<String> checkedParams;

	    /* No parameters */
	    if(param_actions.isEmpty())
		    return true;


	    /* FIXME : Generate list of compared param names */
	    Job job = (Job)p;
	    QDHProperty prop = (QDHProperty)job.getProperty(QDHProperty.class);
	    if (prop == null)
		    return true;

	    checkedParams = prop.getMergeParams();
	    if (checkedParams.isEmpty())
		    return true;

	    /* Match parameters with elements in the queue */
	    for(Item item : Hudson.getInstance().getQueue().getItems(p)) {
		boolean param_match=true;
    		for (ParametersAction action: item.getActions(ParametersAction.class)) {
			for(String param_name : checkedParams){
				ParameterValue val1 = param_action.getParameter(param_name);
				ParameterValue val2 = action.getParameter(param_name);
				
				if((val1 == null && val2 != null) || (val1 != null && val2 == null)){
					param_match = false;
					break;
				} else if(val1 == null && val2 == null){
					continue;
				} else if( ! val1.equals(val2)){
					param_match = false;
					break;
				}
			}
			if(param_match == false)
				break;
		}
		if(param_match == true){
			list_match=true;
			break;
		}
	    }
	    return list_match;
    }
}
