package org.smartregister.growthmonitoring.domain;

public class WeightHeight implements Comparable<WeightHeight> {

    private Weight weight;
    private Height height;

    public WeightHeight(Weight weight, Height height) {
        this.weight = weight;
        this.height = height;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public Height getHeight() {
        return height;
    }

    public void setHeight(Height height) {
        this.height = height;
    }

    @Override
    public int compareTo(WeightHeight weightHeight) {
        return this.weight.getDate().compareTo(weightHeight.weight.getDate());
    }
}
