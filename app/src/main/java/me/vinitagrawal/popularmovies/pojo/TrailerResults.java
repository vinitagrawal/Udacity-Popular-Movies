package me.vinitagrawal.popularmovies.pojo;

import java.util.ArrayList;
import java.util.List;

public class TrailerResults {
    private int id;
    private List<Trailer> results = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Trailer> getResults() {
        return results;
    }

    public void setResults(List<Trailer> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "TrailerResults{" +
                "id=" + id +
                ", results=" + results +
                '}';
    }
}
