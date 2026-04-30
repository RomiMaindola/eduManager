package org.example.edumanager.Dto;

/**
 * DTO used by AttendanceController to receive a single student's attendance row.
 * The staff-attendance.html form sends a list of these (studentId + status pairs).
 */
public class AttendanceRequest {

    private Long studentId;
    private String status; // "Present" or "Absent"

    public AttendanceRequest() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}