package com.aau.p3.performancedashboard.payload.response;

import java.util.List;
import java.util.Date;
import java.util.LinkedList;

import com.aau.p3.performancedashboard.payload.MetricUserCount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetricResultResponse {
    private String metricId;
    private String metricName;
    private Date startDate;
    private Date endDate;
    private List<MetricUserCount> metricUserCounts = new LinkedList<>();
}
