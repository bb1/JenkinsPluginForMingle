package hudson.plugins.jira;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hudson.Extension;
import hudson.MarkupText;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet.Entry;

/**
 * {@link ChangeLogAnnotator} that picks up MingleCard numbers.
 */
@Extension
public class MingleChangeLogAnnotator extends ChangeLogAnnotator {
    private static final Logger LOGGER = Logger.getLogger(MingleChangeLogAnnotator.class.getName());
    @Override
	public void annotate(AbstractBuild<?,?> build, Entry change, MarkupText text) {
        MingleRestService serv = getMingleServiceForProject(build.getProject());
        if(serv==null) return;    // not configured with Mingle

        // if there's any recorded detail information, try to use that, too.
        JiraBuildAction a = build.getAction(JiraBuildAction.class);
        
        Set<MingleCard> cardToBeSaved = new HashSet<MingleCard>();
        
        /* TODO: we have no pattern. just the number of the card? maybe pattern for branch names??
        Pattern pattern = serv.getIssuePattern();
        
        if (LOGGER.isLoggable(Level.FINE)) {
        	LOGGER.fine("Using issue pattern: " + pattern);
        }
        
        String plainText = text.getText();
        
        Matcher m = pattern.matcher(plainText);
        
        while (m.find()) {
        	if (m.groupCount() >= 1) {
        		
        		String id = m.group(1);
        		LOGGER.info("Annotating JIRA id: '" + id + "'");
            	
                if(!serv.existsIssue(id)) {
                    continue;
                }
                
                URL url;
                try {
                	url = serv.getUrl(id);
                } catch (MalformedURLException e) {
                	throw new AssertionError(e); // impossible
                }

                JiraIssue issue = null;
                if (a != null) {
                    issue = a.getIssue(id);
                }

                if (issue == null) {
                    try {
                        issue = serv.getIssue(id);+
                        if (issue != null) {
                        	cardToBeSaved.add(issue);
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error getting remote issue " + id, e);
                    }
                }

                if(issue==null) {
                	text.addMarkup(m.start(1), m.end(1), "<a href='"+url+"'>", "</a>");
                } else {
                	text.addMarkup(m.start(1), m.end(1),
            			String.format("<a href='%s' tooltip='%s'>",url, Util.escape(issue.title)), "</a>");
                }
        		
        	} else {
        		LOGGER.log(Level.WARNING, "The JIRA pattern " + pattern + " doesn't define a capturing group!");
        	}
        }
        
        if (!cardToBeSaved.isEmpty()) {
            saveIssues(build, a, cardToBeSaved);
        }
    }
    */
    private void saveCards(AbstractBuild<?, ?> build, JiraBuildAction a,
            Set<MingleCard> cardToBeSaved) {
        if (a != null) {
            a.addCard(cardToBeSaved);
        } else {
            JiraBuildAction action = new JiraBuildAction(build, cardToBeSaved);
            build.addAction(action);
        }
        
        try {
            build.save();
        } catch (final IOException e) {
            LOGGER.log(Level.WARNING, "Error saving updated build", e);
        }
    }

    MingleRestService getMingleServiceForProject(AbstractProject<?, ?> project) {
        return MingleRestService.get(project);
    }
}
