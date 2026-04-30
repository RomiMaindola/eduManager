package org.example.edumanager.Dto;

public class PaperGenerationRequest {

    private String subject;
    private String courseCode;
    private String department;
    private String semester;
    private int totalQuestions;
    private int totalMarks;
    private String duration;
    private String difficulty;
    private String syllabus;

    // Section A
    private Integer sectionAQuestions;
    private Integer sectionAMarksEach;

    // Section B
    private Integer sectionBQuestions;
    private Integer sectionBMarksEach;

    // Section C
    private Integer sectionCQuestions;
    private Integer sectionCMarksEach;

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getSyllabus() { return syllabus; }
    public void setSyllabus(String syllabus) { this.syllabus = syllabus; }

    public Integer getSectionAQuestions() { return sectionAQuestions; }
    public void setSectionAQuestions(Integer sectionAQuestions) { this.sectionAQuestions = sectionAQuestions; }

    public Integer getSectionAMarksEach() { return sectionAMarksEach; }
    public void setSectionAMarksEach(Integer sectionAMarksEach) { this.sectionAMarksEach = sectionAMarksEach; }

    public Integer getSectionBQuestions() { return sectionBQuestions; }
    public void setSectionBQuestions(Integer sectionBQuestions) { this.sectionBQuestions = sectionBQuestions; }

    public Integer getSectionBMarksEach() { return sectionBMarksEach; }
    public void setSectionBMarksEach(Integer sectionBMarksEach) { this.sectionBMarksEach = sectionBMarksEach; }

    public Integer getSectionCQuestions() { return sectionCQuestions; }
    public void setSectionCQuestions(Integer sectionCQuestions) { this.sectionCQuestions = sectionCQuestions; }

    public Integer getSectionCMarksEach() { return sectionCMarksEach; }
    public void setSectionCMarksEach(Integer sectionCMarksEach) { this.sectionCMarksEach = sectionCMarksEach; }
}