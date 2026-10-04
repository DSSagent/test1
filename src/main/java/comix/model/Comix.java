package comix.model;

import java.time.LocalDate;

public class Comix {

    private String publisher;
    private String series;
    private String title;
    private int volumeNumber;
    private String issueNumber;
    private LocalDate publicationDate;
    private String description;
    private int grade;
    private boolean isSlabbed;
    private double value;

    public Comix(String publisher, String series
                
    ){
        this.publisher = publisher;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getTitle() {
        return title;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public String getSeries() {
        return series;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

}
