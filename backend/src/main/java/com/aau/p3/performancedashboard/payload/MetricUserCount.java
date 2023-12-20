package com.aau.p3.performancedashboard.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetricUserCount {
    private String userId;
    private String displayName;
    private int count;
}
