package com.dataracy.modules.data.support.validator;

import com.dataracy.modules.data.adapter.web.request.DataUploadWebRequest;
import com.dataracy.modules.data.support.annotation.ValidDateRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, DataUploadWebRequest> {

    @Override
    public boolean isValid(DataUploadWebRequest request, ConstraintValidatorContext context) {
        LocalDate start = request.startDate();
        LocalDate end = request.endDate();

        if (start == null || end == null) return false;

        return !start.isAfter(end);
    }
}
