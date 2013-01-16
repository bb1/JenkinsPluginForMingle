package mingleplugin;

import java.net.URL;

import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* Mingle cards related to the build.
*
* @author Birk Brauer
*/
public class MingleBuildAction implements Action {

    public final AbstractBuild<?, ?> owner;

    public MingleCard[] cards;

    //TODO: connection service <-> Build? here or somewhere else? and when will it be initialized?
    public MingleRestService service;

    private static final Logger LOGGER = Logger.getLogger(MingleChangeLogAnnotator.class.getName());

    public MingleBuildAction(AbstractBuild<?, ?> owner, Collection<MingleCard> cards) {
        LOGGER.info("Started Build");
        this.owner = owner;
        this.cards = cards.toArray(new MingleCard[cards.size()]);
        Arrays.sort(this.cards);

        // should be changable, savable via DESCRIPTOR etc.
        URL url;
        try {
            url = new URL("http://mingle/");
        } catch (MalformedURLException e) {
            //do nothing
        }
        String userName = "birk";
        String password = "Wae6ohl8";
        String project = "scrum__with_two_teams_";
        String userPattern = "";
        boolean supportsWikiStyleComment = false;
        this.service = new MingleRestService(url, userName, password, project, userPattern, supportsWikiStyleComment);
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Mingle Builder stuff";
    }

    public String getUrlName() {
        return "mingle";
    }

/**
 * Finds {@link MingleCard} whose ID matches the given one.
 */
    public MingleCard getCard(int number) {
        for (MingleCard card : cards) {
            if(card.number == number)
                return card;
        }
        return null;
    }

    public void addCard(Set<MingleCard> cardToBeSaved) {
        SortedSet<MingleCard> allCard = new TreeSet<MingleCard>();
        allCard.addAll(cardToBeSaved);
        allCard.addAll(Arrays.asList(this.cards));
        
        this.cards = allCard.toArray(new MingleCard[allCard.size()]);
    }

/**
 * Finds {@link MingleCard} whose ID matches the given one.
 */
    public MingleRestService getService() {
        return service;
    }
}