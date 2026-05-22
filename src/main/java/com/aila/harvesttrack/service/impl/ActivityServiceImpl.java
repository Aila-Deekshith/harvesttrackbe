package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.dto.ActivityRequestDto;
import com.aila.harvesttrack.model.Activity;
import com.aila.harvesttrack.repository.ActivityRepository;
import com.aila.harvesttrack.repository.JobsRepository;
import com.aila.harvesttrack.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private JobsRepository jobsRepository;

    @Override
    public Activity getActivityById(Integer id) {
        return activityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException(
                        "Activity not found with id: " + id));
    }

    @Override
    public Activity addActivity(ActivityRequestDto dto) {

        Activity activity = new Activity();
        activity.setActivityName(dto.getActivityName());
        activity.setCropType(dto.getCropType());

        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Integer id, ActivityRequestDto dto) {

        Activity existing = activityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException(
                        "Activity not found with id: " + id));

        if (dto.getActivityName() != null) existing.setActivityName(dto.getActivityName());
        if (dto.getCropType()     != null) existing.setCropType(dto.getCropType());

        return activityRepository.save(existing);
    }

    @Override
    public void deleteActivity(Integer id) {
        activityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException(
                        "Activity not found with id: " + id));
        activityRepository.softDeleteById(id);
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

}