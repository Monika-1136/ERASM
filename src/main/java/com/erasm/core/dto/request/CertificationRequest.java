package com.erasm.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class CertificationRequest {

    @NotBlank(message = "Certification name is required")
    private String certificationName;

    @NotBlank(message = "Issuing organization is required")
    private String issuingOrganization;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    public CertificationRequest() {
    }

    public CertificationRequest(String certificationName, String issuingOrganization, LocalDate issueDate, LocalDate expiryDate) {
        this.certificationName = certificationName;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }

    public String getCertificationName() {
        return certificationName;
    }

    public void setCertificationName(String certificationName) {
        this.certificationName = certificationName;
    }

    public String getIssuingOrganization() {
        return issuingOrganization;
    }

    public void setIssuingOrganization(String issuingOrganization) {
        this.issuingOrganization = issuingOrganization;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
