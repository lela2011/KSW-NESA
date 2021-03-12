package ch.kanti.nesa.objects;

public class PromotionRule {

    private int subjectsCount;

    private String id1;
    private String id2;
    private String id3;

    private float weight1;
    private float weight2;
    private float weight3;

    private boolean round;

    public PromotionRule(int subjectsCount, String id1, float weight1, boolean round) {
        this.subjectsCount = subjectsCount;
        this.id1 = id1;
        this.weight1 = weight1;
        this.round = round;
    }

    public PromotionRule(int subjectsCount, String id1, String id2, float weight1, float weight2, boolean round) {
        this.subjectsCount = subjectsCount;
        this.id1 = id1;
        this.id2 = id2;
        this.weight1 = weight1;
        this.weight2 = weight2;
        this.round = round;
    }

    public PromotionRule(int subjectsCount, String id1, String id2, String id3, float weight1, float weight2, float weight3, boolean round) {
        this.subjectsCount = subjectsCount;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.weight1 = weight1;
        this.weight2 = weight2;
        this.weight3 = weight3;
        this.round = round;
    }

    public int getSubjectsCount() {
        return subjectsCount;
    }

    public void setSubjectsCount(int subjectsCount) {
        this.subjectsCount = subjectsCount;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getId3() {
        return id3;
    }

    public void setId3(String id3) {
        this.id3 = id3;
    }

    public float getWeight1() {
        return weight1;
    }

    public void setWeight1(float weight1) {
        this.weight1 = weight1;
    }

    public float getWeight2() {
        return weight2;
    }

    public void setWeight2(float weight2) {
        this.weight2 = weight2;
    }

    public float getWeight3() {
        return weight3;
    }

    public void setWeight3(float weight3) {
        this.weight3 = weight3;
    }

    public boolean isRound() {
        return round;
    }

    public void setRound(boolean round) {
        this.round = round;
    }
}
