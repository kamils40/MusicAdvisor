package advisor.DataEntities;

import java.util.List;

public class Song {
    private String name;
    private List<String> artists;
    private String url;

    public Song(String name, List<String> artists, String url) {
        this.name = name;
        this.artists = artists;
        this.url = url;
    }
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return name + "\n" + artists.toString() + "\n" + url;
    }
}
