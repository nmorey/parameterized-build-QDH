package hudson.plugins.parameterizedbuildQDH;

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

import java.io.IOException;

public class QDHProperty extends  JobProperty<AbstractProject<?,?>> {

    private String[] mergeParams;

    public QDHProperty(String mergeParams) {
	    this.mergeParams = StringUtils.split( mergeParams, "," );
    }

    public ArrayList<String> getMergeParams(){
	    return new ArrayList<String>(Arrays.asList(mergeParams));
    }
    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {
	    public DescriptorImpl() {
		    super(QDHProperty.class);
	    }

	    // public boolean isApplicable(Class<? extends Job> jobType) {
	    // 	    return AbstractProject.class.isParameterized(jobType);
	    // }

	    public String getDisplayName() {
		    return "Queue merge parameters";
	    }
    }
}