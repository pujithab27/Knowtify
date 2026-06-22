package com.knowtify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesRequest {
    private List<String> domains;
    private String notificationTime;
    private String notificationFrequency;
}
