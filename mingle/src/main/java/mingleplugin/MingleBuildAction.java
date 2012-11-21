package mingleplugin;

import hudson.model.AbstractBuild;
import hudson.model.Action;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
* Mingle cards related to the build.
*
* @author Birk Brauer
*/
public class MingleBuildAction implements Action {
    public final AbstractBuild<?, ?> owner;

    public MingleCard[] cards;

    public MingleBuildAction(AbstractBuild<?, ?> owner, Collection<MingleCard> cards) {
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