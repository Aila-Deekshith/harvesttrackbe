package com.aila.harvesttrack.service;

import com.aila.harvesttrack.dto.ActivityRequestDto;
import com.aila.harvesttrack.model.Activity;

import java.util.List;

public interface ActivityService {

    Activity       getActivityById(Integer id);
    Activity       addActivity(ActivityRequestDto dto);
    Activity       updateActivity(Integer id, ActivityRequestDto dto);
    void           deleteActivity(Integer id);

    List<Activity> getAllActivities();
}