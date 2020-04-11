package pasindu.dev.classie.ro_pos2.Model;

import java.util.Map;

public class CommentsModel {
    private float ratingValue;
    private String comments;
    private String name;
    private String uid;
    private Map<String, Object> commentsTimeStamp;

    public CommentsModel() {
    }

    public CommentsModel(float ratingValue, String comments, String name, String uid, Map<String, Object> commentsTimeStamp) {
        this.ratingValue = ratingValue;
        this.comments = comments;
        this.name = name;
        this.uid = uid;
        this.commentsTimeStamp = commentsTimeStamp;
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> getCommentsTimeStamp() {
        return commentsTimeStamp;
    }

    public void setCommentsTimeStamp(Map<String, Object> commentsTimeStamp) {
        this.commentsTimeStamp = commentsTimeStamp;
    }
}
