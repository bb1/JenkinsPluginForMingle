package mingleplugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
//import hudson.plugins.mingle.mingleIssueUpdater.DescriptorImpl;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.tasks.BuildWrapper;


//TODO: delete this file because it only affects the "Release Notes in Jira and Mingle doesn't support this! T_T"

public class MingleCreateReleaseNotes extends BuildWrapper {
	
	public static final String DEFAULT_FILTER = "status in (Accepted)";
	
	private String mingleEnvironmentVariable;
	private String mingleProjectKey;
	private String mingleRelease;
	private String mingleFilter;

	@DataBoundConstructor
	public MingleCreateReleaseNotes(String mingleProjectKey, String mingleRelease, String mingleEnvironmentVariable) {
		this(mingleProjectKey, mingleRelease, mingleEnvironmentVariable, DEFAULT_FILTER);
	}
	
	public MingleCreateReleaseNotes(String mingleProjectKey, String mingleRelease, String mingleEnvironmentVariable, String mingleFilter) {
		this.mingleRelease = mingleRelease;
		this.mingleProjectKey = mingleProjectKey;
		this.mingleEnvironmentVariable = mingleEnvironmentVariable;
		this.mingleFilter = mingleFilter;
	}
	
	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
		String realRelease = null;
		String releaseNotes = "No Release Notes";
		String realFilter = DEFAULT_FILTER;
		try {
			realRelease = build.getEnvironment(listener).expand(mingleRelease);

			if (realRelease == null || realRelease.isEmpty()) {
				throw new IllegalArgumentException("Release is Empty");
			}
			
			if( mingleFilter != null ) realFilter = build.getEnvironment(listener).expand(mingleFilter);

			//TODO: There is no conection written in the Build yet.
			MingleRestService serv = build.getService();
			
			releaseNotes = serv.getReleaseNotesForFixVersion(mingleProjectKey, realRelease, realFilter);

		} catch (Exception e) {
			e.printStackTrace(listener.fatalError(
				"Unable to generate release notes for mingle version %s/%s: %s", realRelease,
				mingleProjectKey, e));
			listener.finished(Result.FAILURE);
			return new Environment() { };
		}
		
		Map<String,String> envMap = new HashMap<String,String>();
		envMap.put(mingleEnvironmentVariable, releaseNotes);
		
		final Map<String,String> resultVariables = envMap; 
	
		return new Environment() {
            @Override
            public void buildEnvVars(Map<String, String> env) {
                env.putAll(resultVariables);
            }
        };
	}
	
	public String getMingleEnvironmentVariable() {
		return mingleEnvironmentVariable;
	}

	public void setMingleEnvironmentVariable(String mingleEnvironmentVariable) {
		this.mingleEnvironmentVariable = mingleEnvironmentVariable;
	}

	public String getMingleRelease() {
		return mingleRelease;
	}

	public void setMingleRelease(String mingleRelease) {
		this.mingleRelease = mingleRelease;
	}

	public String getMingleProjectKey() {
		return mingleProjectKey;
	}

	public void setMingleProjectKey(String mingleProjectKey) {
		this.mingleProjectKey = mingleProjectKey;
	}
	
	public String getMingleFilter() {
		return mingleFilter;
	}

	public void setMingleFilter(String mingleFilter) {
		this.mingleFilter = mingleFilter;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}
	
	@Extension
	public final static class Descriptor extends BuildWrapperDescriptor {

		@Override
		public String getDisplayName() {
			return "Generate Release Notes";
		}

		@Override
		public boolean isApplicable(AbstractProject<?, ?> item) {
			return true;
		}
	}
}
