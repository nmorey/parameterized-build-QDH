package parameterizedbuildQDH;

import hudson.Util;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import net.sf.json.JSONObject;

import java.io.IOException;

public class QDHProperty extends  JobProperty<AbstractProject<?,?>> {

    public final String mergeParams;
    public final String fuseParams;
    private String[] mergeParamsArray;
    private String[] fuseParamsArray;

    @DataBoundConstructor
		public QDHProperty(String mergeParams, String fuseParams) {
	    this.mergeParams = mergeParams;
	    this.mergeParamsArray = StringUtils.split( this.mergeParams, "," );
	    this.fuseParams = fuseParams;
	    this.fuseParamsArray = StringUtils.split( this.fuseParams, "," );
    }

    public String getMergeParams(){
	    return mergeParams;
    }
    public ArrayList<String> getMergeParamsArray(){
	    return new ArrayList<String>(Arrays.asList(this.mergeParamsArray));
    }

    public String getFuseParams(){
	    return fuseParams;
    }
    public ArrayList<String> getFuseParamsArray(){
	    return new ArrayList<String>(Arrays.asList(this.fuseParamsArray));
    }
 
    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {

	    @Override
	    public String getDisplayName() {
		    return Messages.QDHProperty_DisplayName();
	    }

	    @Override
	    public QDHProperty newInstance(StaplerRequest req, JSONObject formData) throws FormException {
		    if (formData.has("paramBlock"))
			    return req.bindJSON(QDHProperty.class, formData.getJSONObject("paramBlock"));
		    return null;
	    }

    }
}
