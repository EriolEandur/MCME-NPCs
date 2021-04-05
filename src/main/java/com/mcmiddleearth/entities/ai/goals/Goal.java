package com.mcmiddleearth.entities.ai.goals;

public interface Goal {

    void updatePath();

    void updateTick();

    int getUpdateInterval();

}
