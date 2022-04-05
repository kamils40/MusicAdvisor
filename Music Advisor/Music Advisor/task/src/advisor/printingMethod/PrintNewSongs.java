package advisor.printingMethod;

import advisor.DataEntities.Song;
import advisor.config.Configuration;

import java.util.List;

public class PrintNewSongs implements PrintingMethod {
    
    private List<Song> songList;

    public PrintNewSongs(List<Song> songList) {
        this.songList = songList;
    }
    @Override
    public void print(int pageNumber) {
        if(pageNumber != 0 && getMaximumPages() >= pageNumber) {
            System.out.println("---PAGE " + pageNumber + " OF " + getMaximumPages() + "---");
            int position = (pageNumber - 1) * Configuration.NUMBER_OF_RESULTS_PER_PAGE;
            for (int i = 0 + position; i < pageNumber * Configuration.NUMBER_OF_RESULTS_PER_PAGE; i++) {
                System.out.println(songList.get(i).toString());
            }
        } else {
            System.out.println("No more pages");
        }
    }

    public int getMaximumPages() {
        int x = songList.size() / Configuration.NUMBER_OF_RESULTS_PER_PAGE;
        int reminder = songList.size() - (x * Configuration.NUMBER_OF_RESULTS_PER_PAGE);
        //if list size is dividable by number of results, maximum pages is equal to quotient, otherwise quotient+1
        return reminder == 0 ? x : x + 1;
    }
}
