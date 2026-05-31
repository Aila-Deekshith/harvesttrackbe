package com.aila.harvesttrack.service.impl;

import com.aila.harvesttrack.dto.DashboardResponseDto;
import com.aila.harvesttrack.dto.ReportDashboardResponseDto;
import com.aila.harvesttrack.model.Customer;
import com.aila.harvesttrack.model.Jobs;
import com.aila.harvesttrack.repository.CustomerRepository;
import com.aila.harvesttrack.repository.JobsRepository;
import com.aila.harvesttrack.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final JobsRepository jobsRepository;

    @Override
    public DashboardResponseDto getDashboardMetrics(Integer ownerId) {

        DashboardResponseDto dashboardResponseDto = new DashboardResponseDto();

        List<Customer> customers = customerRepository.findByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(ownerId);
        List<Jobs> activeJobs = jobsRepository.findByOwner_IdAndStatus(ownerId, "in-progress");
        List<Jobs> completedJobs = jobsRepository.findByOwner_IdAndStatusAndCreatedAtAfter(ownerId, "finished", Instant.now().minusSeconds(24 * 3600));
        Long todayEarnings = completedJobs.stream().map(this::calculateJobAmount).reduce(0f, Float::sum).longValue();

        dashboardResponseDto.setActiveJobs(activeJobs.size());
        dashboardResponseDto.setTodayEarnings(todayEarnings);
        dashboardResponseDto.setTotalCustomers(customers.size());
        dashboardResponseDto.setJobsCompletedToday(completedJobs.size());

        return dashboardResponseDto;
    }

    @Override
    public ReportDashboardResponseDto generateDashboardReport(Integer ownerId, Instant from, Instant to) {
        ReportDashboardResponseDto reportDashboardResponseDto = new ReportDashboardResponseDto();
        List<Jobs> jobs;
        if(from != null && to!=null){
            jobs = jobsRepository.findByOwner_IdAndDeletedAtIsNullAndCreatedAtAfterAndCreatedAtBefore(ownerId, from, to);
        } else if(from != null) {
            jobs = jobsRepository.findByOwner_IdAndDeletedAtIsNullAndCreatedAtAfter(ownerId, from);
        } else {
            jobs = jobsRepository.findByOwner_IdAndDeletedAtIsNull(ownerId);
        }
        reportDashboardResponseDto.setTotalJobs(jobs.size());
        reportDashboardResponseDto.setTotalEarnings(jobs.stream().filter(job -> job.getStatus().equalsIgnoreCase("finished")).map(this::calculateJobAmount).reduce(0f, Float::sum).longValue());
        reportDashboardResponseDto.setTotalAcres((int) jobs.stream().map(Jobs::getAcres).reduce(0f, Float::sum).longValue());
        reportDashboardResponseDto.setTotalHours(Math.toIntExact(jobs.stream().filter(job -> job.getStatus().equalsIgnoreCase("finished")).map(job -> Duration.between(job.getStartDate(), job.getEndDate()).toHours()).reduce(0L, Long::sum)));
        reportDashboardResponseDto.setAvgAmountPerJob((long) (jobs.stream().filter(job -> job.getStatus().equalsIgnoreCase("finished")).map(this::calculateJobAmount).reduce(0f, Float::sum) / Math.max(jobs.stream().filter(job -> job.getStatus().equalsIgnoreCase("finished")).count(), 1)));
        reportDashboardResponseDto.setAvgAmountPerAcre((long) (jobs.stream().filter(job -> job.getStatus().equalsIgnoreCase("finished")).map(this::calculateJobAmount).reduce(0f, Float::sum) / Math.max(jobs.stream().filter(job -> job.getStatus().equalsIgnoreCase("finished")).map(Jobs::getAcres).reduce(0f, Float::sum), 1)));
        return reportDashboardResponseDto;
    }

    @Override
    public List<Map<Instant, Long>> dailyEarnings(Integer ownerId, Instant from, Instant to) {
        to = to != null ? to : Instant.now();
        Instant currentDate = from != null ? from : Instant.now().minusSeconds(7L * 24 * 3600); // Default to last 7 days
        List<Map<Instant, Long>> dailyEarnings = new ArrayList<>();
        while(currentDate.isBefore(to)) {
            List<Jobs> jobs = jobsRepository.findByOwner_IdAndDeletedAtIsNullAndCreatedAtAfterAndCreatedAtBefore(ownerId, currentDate, currentDate.plusSeconds(24 * 3600));
            long earnings = jobs.stream().filter(job -> job.getStatus().equalsIgnoreCase("finished")).map(this::calculateJobAmount).reduce(0f, Float::sum).longValue();
            dailyEarnings.add(Map.of(currentDate, earnings));
            currentDate = currentDate.plusSeconds(24 * 3600);
        }
        return dailyEarnings;
    }

    public Float calculateJobAmount(Jobs job) {
        long seconds = Duration.between(job.getStartDate(), job.getEndDate()).getSeconds();
        Float cost = job.getCost();
        if(cost != null) {
            return cost * ((float) seconds / (60 * 60));
        }
        return 0f;
    }

}
