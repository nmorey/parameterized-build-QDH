package parameterizedbuildQDH;

import hudson.Util;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.Queue;
import hudson.model.Queue.Task;
import hudson.model.Queue.QueueDecisionHandler;
import hudson.model.Queue.Item;
import hudson.model.ParametersAction;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import org.kohsuke.stapler.DataBoundConstructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Level.FINE;

@Extension
public class QueueDecisionHandlerPB extends QueueDecisionHandler {
	private static final Logger LOGGER = Logger.getLogger(QueueDecisionHandlerPB.class.getName());
    @Override
    public boolean shouldSchedule(Task p, List<Action> actions) {	    
	    List<ParametersAction> param_actions = Util.filter(actions, ParametersAction.class);
	    ParametersAction new_job_params = null;
	    List<String> checkedParams;
	    List<String> fusedParams;
	    Queue queue = Hudson.getInstance().getQueue();

	    // LOGGER.log(Level.INFO, "Entering QDHPB");

	    /* No parameters */
	    if(param_actions.isEmpty()){
		    // LOGGER.log(Level.INFO, "Build has no parameter. Return true");
		    return true;
	    }

	    /* There is no real list, this a a hack to get the first one ! */
	    for (ParametersAction action: param_actions)
		    new_job_params = action;

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

	    fusedParams = prop.getFuseParamsArray();
		Map<String, String> current_params = new HashMap<String, String>();

	    /* Match parameters with elements in the queue */
	    for(Item item : Hudson.getInstance().getQueue().getItems(p)) {
			boolean param_match=true;
			// LOGGER.log(Level.INFO, "Treating Item: " + item.getDisplayName() );

			// We keep a hash map of this build parameters so we can merge them more easily later

    		for (ParametersAction action: item.getActions(ParametersAction.class)) {
				for(String param_name : checkedParams){
					ParameterValue new_val = new_job_params.getParameter(param_name);
					ParameterValue prev_val = action.getParameter(param_name);
				
					if((new_val == null && prev_val != null) || (new_val != null && prev_val == null)){
						// LOGGER.log(Level.INFO, "Mismatch on " + param_name + ". Next Item");
						param_match = false;
						break;
					} else if(new_val == null && prev_val == null){
						// LOGGER.log(Level.INFO, "No parem " + param_name + " on either. Next param");
						continue;
					} else if( ! new_val.equals(prev_val)){
						// LOGGER.log(Level.INFO, "Mismatch on " + param_name + ". Next item");
						param_match = false;
						break;
					} else {
						// LOGGER.log(Level.INFO, "Match on " + param_name + ". Next param");
					}
				}
				if(param_match == false)
					break;

				for(String param_name : fusedParams){
					ParameterValue prev_val = action.getParameter(param_name);
					if(prev_val == null)
						continue;
					if(!(prev_val instanceof StringParameterValue))
						continue;
					StringParameterValue str_value = (StringParameterValue)prev_val;

					String prev_string = current_params.get(param_name);
					// LOGGER.log(Level.INFO, "Found param with value '" + str_value.value + "'.");

					if(prev_string != null){
						current_params.put(param_name, prev_string + "," + str_value.value);
					} else {
						current_params.put(param_name, str_value.value);
					}
				}
			}
			if(param_match == true){
				// LOGGER.log(Level.INFO, "Remove duplicate job in queue");
				queue.cancel(item);
			}
	    }
		if(!current_params.isEmpty()){
			List<ParameterValue> new_param_list = new ArrayList<ParameterValue>();
			for(ParameterValue item_param : new_job_params.getParameters()){
				String fused_string = current_params.get(item_param.getName());
				if(fused_string == null || !( item_param instanceof StringParameterValue)){
					new_param_list.add(item_param);
				} else {
					StringParameterValue str_item_param = (StringParameterValue)item_param;
					// LOGGER.log(Level.INFO, "Fusing arg '" + item_param.getName() +"' with  '" + fused_string +
					// 		   "' + '"+ str_item_param.value+"'.");
					String new_value = fused_string + "," + str_item_param.value;
					new_param_list.add(new StringParameterValue(item_param.getName(), new_value));
				}

			}
			actions.remove(new_job_params);
			ParametersAction newActionParams = new ParametersAction(new_param_list);
			actions.add(newActionParams);
		}

	    // LOGGER.log(Level.INFO, "Result is " + list_match);
	    return true;
    }
}
