package mingleplugin;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet.Entry;

/**
 * A {@link Recorder} Set the desciption for the build as a list of all conected mingle cards.
 */
//TODO: This class is never called! It's extention point is obviously totally useless. :/ 
public class MingleDescriptionSetter extends Recorder {
  private static final Logger LOGGER = Logger.getLogger(MingleChangeLogAnnotator.class.getName());

  @Override
  public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
  BuildListener listener) throws InterruptedException {
    LOGGER.info("Started MingleDescriptionSetter"); // NEVER HAPPEND!

    AbstractProject<?,?> project = build.getProject();
    MingleRestService service = MingleRestService.get(project);

    List<MingleBuildAction> actions = build.getActions(MingleBuildAction.class);
    MingleBuildAction action;

    if (actions.size() > 0) action = (MingleBuildAction) actions.get(0);
    else throw new InterruptedException("No cards in this build! "); // No MingleBuildAction = No Cards.

    List<MingleCard> cards = action.getCards();
    String newDescription = "<p>This build updates the following cards:</p>\n<ol>";

    for (MingleCard card : cards) {
      int id = card.getNumber();
      URL url = null;
      String name = card.getName();
      try {
        url = service.getCardUrl(id);
      } catch (MalformedURLException e) {
        LOGGER.log(Level.WARNING, "Couldn't get URL for Card " + id, e);
      }
      newDescription += "<li><a href=\""+url.toString()+"\">#"+id+"</a>: "+name+"</li>"; 
    }
    newDescription += "</ul>";
    // 	result = build.getEnvironment(listener).expand(result);.
    LOGGER.log(Level.WARNING, "Description set to: " + newDescription);
    try {
      build.setDescription(newDescription);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING,"Couldn't set the build description!");
    }

    //if anything goes wrong throw new AbortException()

    return true;
  }

  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.NONE;
  }

  @Extension
  public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    public DescriptorImpl() {
      super(MingleDescriptionSetter.class);
    }

    @Override
    public String getDisplayName() {
      return "MingleDescriptionSetter";
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

  }

  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

}