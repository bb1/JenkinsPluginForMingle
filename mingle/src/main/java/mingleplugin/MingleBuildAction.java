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
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;

/**
* Mingle cards related to the build.
*
* @author Birk Brauer
*/
public class MingleBuildAction implements Action {

    public final AbstractBuild<?, ?> owner;

    public MingleCard[] cards;

    private static final Logger LOGGER = Logger.getLogger(MingleChangeLogAnnotator.class.getName());

    public MingleBuildAction(AbstractBuild<?, ?> owner, Collection<MingleCard> cards) {
        LOGGER.info("Started Build");
        this.owner = owner;
        this.cards = cards.toArray(new MingleCard[cards.size()]);
        Arrays.sort(this.cards);
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
 * Returns all ID connected to the build.
 */
  public List<Integer> getCardIds() {
    List<Integer> ids = new ArrayList<Integer>();
    for (MingleCard card : cards) {
      ids.add(card.getNumber());
    }
    return ids;
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

}