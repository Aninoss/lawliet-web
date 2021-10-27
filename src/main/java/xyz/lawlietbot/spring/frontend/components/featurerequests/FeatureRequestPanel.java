package xyz.lawlietbot.spring.frontend.components.featurerequests;

import java.util.ArrayList;
import java.util.List;
import com.github.appreciated.css.grid.GridLayoutComponent;
import com.github.appreciated.css.grid.sizes.Flex;
import com.github.appreciated.css.grid.sizes.Length;
import com.github.appreciated.css.grid.sizes.MinMax;
import com.github.appreciated.css.grid.sizes.Repeat;
import com.github.appreciated.layout.FlexibleGridLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Article;
import com.vaadin.flow.component.html.Div;
import xyz.lawlietbot.spring.backend.featurerequests.FRDynamicBean;
import xyz.lawlietbot.spring.backend.featurerequests.FREntry;
import xyz.lawlietbot.spring.backend.userdata.SessionData;
import xyz.lawlietbot.spring.backend.userdata.UIData;
import xyz.lawlietbot.spring.frontend.components.ConfirmationDialog;
import xyz.lawlietbot.spring.frontend.components.featurerequests.sort.FeatureRequestSort;

public class FeatureRequestPanel extends Div {

    private static final int ENTRIES_PER_PAGE = 12;

    private final SessionData sessionData;
    private final UIData uiData;
    private final FRDynamicBean frDynamicBean;
    private final ConfirmationDialog confirmationDialog;

    public FeatureRequestPanel(SessionData sessionData, UIData uiData, FRDynamicBean frDynamicBean, ConfirmationDialog confirmationDialog) {
        this.sessionData = sessionData;
        this.uiData = uiData;
        this.frDynamicBean = frDynamicBean;
        this.confirmationDialog = confirmationDialog;
        setWidthFull();
    }

    public void updateEntries(int page, FeatureRequestSort comparator, String search) {
        ArrayList<FREntry> entryList = generateEntryList(search);
        entryList.sort(comparator);

        ArrayList<Article> articles = new ArrayList<>();
        for (int i = ENTRIES_PER_PAGE * page; i < Math.min(entryList.size(), ENTRIES_PER_PAGE * (page + 1)); i++) {
            FREntry entry = entryList.get(i);
            FeatureRequestCard featureRequestCard = new FeatureRequestCard(entry, confirmationDialog, sessionData, uiData);
            Article article = new Article(featureRequestCard);
            articles.add(article);
        }

        removeAll();
        if (entryList.size() > 0) {
            FlexibleGridLayout gridLayout = new FlexibleGridLayout()
                    .withColumns(Repeat.RepeatMode.AUTO_FILL, new MinMax(new Length("270px"), new Flex(1)))
                    .withItems(articles.toArray(new Article[0]))
                    .withPadding(false)
                    .withSpacing(true)
                    .withAutoFlow(GridLayoutComponent.AutoFlow.ROW_DENSE)
                    .withOverflow(GridLayoutComponent.Overflow.AUTO);
            gridLayout.setSizeFull();
            gridLayout.getStyle().set("overflow", "visible");
            add(gridLayout);
        } else {
            Div textDiv = new Div(new Text(getTranslation("fr.noresults")));
            textDiv.setWidthFull();
            textDiv.getStyle().set("text-align", "center");
            add(textDiv);
        }
    }

    private ArrayList<FREntry> generateEntryList(String search) {
        ArrayList<FREntry> entryList = new ArrayList<>(frDynamicBean.getEntryList());
        entryList.removeIf(entry -> {
            if (search != null && search.length() > 0) {
                String newSearch = search.toLowerCase();
                return !entry.getTitle().toLowerCase().contains(newSearch) &&
                        !entry.getDescription().toLowerCase().contains(newSearch) &&
                        !String.valueOf(entry.getId()).equals(newSearch);
            }
            return false;
        });
        return entryList;
    }

    public int getPageSize(String search) {
        List<FREntry> entryList = generateEntryList(search);
        return (entryList.size() - 1) / ENTRIES_PER_PAGE + 1;
    }

}
