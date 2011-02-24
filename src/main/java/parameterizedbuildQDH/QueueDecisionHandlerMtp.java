package parameterizedbuildQDH;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Queue.Task;
import hudson.model.Queue.QueueDecisionHandler;
import java.util.List;
import java.io.IOException;
import hudson.matrix.MatrixProject;
import hudson.model.Label;
import hudson.model.labels.LabelAtom;
import hudson.tasks.BuildWrapper;

@Extension
public class QueueDecisionHandlerPB extends QueueDecisionHandler {
    
    @Override
    public boolean shouldSchedule(Task p, List<Action> actions) {
        return true;
    }
}
