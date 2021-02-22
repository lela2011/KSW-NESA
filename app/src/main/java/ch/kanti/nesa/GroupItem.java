package ch.kanti.nesa;

public class GroupItem {
    private String groupItemName;
    private Float groupItemWeight;

    public GroupItem(String groupItemName, Float groupItemWeight) {
        this.groupItemName = groupItemName;
        this.groupItemWeight = groupItemWeight;
    }

    public String getGroupItemName() {
        return groupItemName;
    }

    public void setGroupItemName(String groupItemName) {
        this.groupItemName = groupItemName;
    }

    public Float getGroupItemWeight() {
        return groupItemWeight;
    }

    public void setGroupItemWeight(Float groupItemWeight) {
        this.groupItemWeight = groupItemWeight;
    }
}
