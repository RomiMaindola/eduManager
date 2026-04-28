package org.example.edumanager.Dto;


public class LoginRequest {

    private String email;
    private String admissionNo;
    private String staffNo;
    private String password;

    public LoginRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdmissionNo() { return admissionNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }

    public String getStaffNo() { return staffNo; }
    public void setStaffNo(String staffNo) { this.staffNo = staffNo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
