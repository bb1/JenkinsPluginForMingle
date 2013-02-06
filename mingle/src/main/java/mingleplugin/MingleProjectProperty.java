package mingleplugin;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.util.CopyOnWriteList;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

/**
 * Associates {@link AbstractProject} with {@link MingleRestService}.
 * 
 * @author Kohsuke Kawaguchi, Birk Brauer
 */
public class MingleProjectProperty extends JobProperty<AbstractProject<?, ?>> {

	/**
	 * Used to find {@link MingleRestService}. Matches {@link MingleRestService#getName()}. Always
	 * non-null (but beware that this value might become stale if the system
	 * config is changed.)
	 */
	public final String siteName;

	@DataBoundConstructor
	public MingleProjectProperty(String siteName) {
		if (siteName == null) {
			// defaults to the first one
			MingleRestService[] sites = DESCRIPTOR.getSites();
			if (sites.length > 0)
				siteName = sites[0].getName();
		}
		this.siteName = siteName;
	}

	/**
	 * Gets the {@link MingleRestService} that this project belongs to.
	 * 
	 * @return null if the configuration becomes out of sync.
	 */
	public MingleRestService getSite() {
		MingleRestService[] sites = DESCRIPTOR.getSites();
		if (siteName == null && sites.length > 0)
			// default
			return sites[0];

		for (MingleRestService site : sites) {
			if (site.getName().equals(siteName))
				return site;
		}
		return null;
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return DESCRIPTOR;
	}

	@Extension
	public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

	public static final class DescriptorImpl extends JobPropertyDescriptor {
		private final CopyOnWriteList<MingleRestService> sites = new CopyOnWriteList<MingleRestService>();

		public DescriptorImpl() {
			super(MingleProjectProperty.class);
			load();
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean isApplicable(Class<? extends Job> jobType) {
			return AbstractProject.class.isAssignableFrom(jobType);
		}

		@Override
		public String getDisplayName() {
			return Messages.MingleProjectProperty_DisplayName();
		}

		public void setSites(MingleRestService site) {
			sites.add(site);
		}

		public MingleRestService[] getSites() {
			return sites.toArray(new MingleRestService[0]);
		}

		@Override
		public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData)
				throws FormException {
			MingleProjectProperty jpp = req.bindParameters(
					MingleProjectProperty.class, "mingle.");
			if (jpp.siteName == null)
				jpp = null; // not configured
			return jpp;
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) {
			sites.replaceBy(req.bindJSONToList(MingleRestService.class, formData.get("sites")));
			save();
			return true;
		}
	}

	private static final Logger LOGGER = Logger
			.getLogger(MingleProjectProperty.class.getName());
}
