package advisor.printingMethod;

import advisor.DataEntities.Categories;
import advisor.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PrintCategories implements PrintingMethod{

    private List<String> categories;

    public PrintCategories(List<String> categories) {
        this.categories = categories;
    }
    @Override
    public void print(int pageNumber) {
        if(pageNumber != 0 && getMaximumPages() >= pageNumber) {
            System.out.println("---PAGE " + pageNumber + " OF " + getMaximumPages() + "---");
            int position = (pageNumber - 1) * Configuration.NUMBER_OF_RESULTS_PER_PAGE;
            for (int i = 0 + position; i < pageNumber * Configuration.NUMBER_OF_RESULTS_PER_PAGE; i++) {
                System.out.println(categories.get(i).toString());
            }
        } else {
            System.out.println("No more pages");
        }
    }
    @Override
    public int getMaximumPages() {
        int x = categories.size() / Configuration.NUMBER_OF_RESULTS_PER_PAGE;
        int reminder = categories.size() - (x * Configuration.NUMBER_OF_RESULTS_PER_PAGE);
        //if list size is dividable by number of results, maximum pages is equal to quotient, otherwise quotient+1
        return reminder == 0 ? x : x + 1;
    }

}
