



import org.example.edumanager.Dto.LoginRequest;
import org.example.edumanager.Dto.PaperGenerationRequest;
import org.example.edumanager.Repositiory.*;
import org.example.edumanager.Service.Imp.*;
import org.example.edumanager.entity.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ─────────────────────────────────────────────────────────────────────────────
// 1.  AnnouncementServiceImpTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class EduManagerApplicationTests {

    @Mock
    AnnouncementRepository announcementRepository;

    @InjectMocks
    AnnouncementServiceImp announcementService;

    private Staff staff;
    private Announcement announcement;

    @BeforeEach
    void setUp() {
        staff = new Staff(1L, "Alice", "ST001", "alice@edu.com", "pass", "CS", "ROLE_STAFF");
        announcement = new Announcement(1L, "Holiday", "College closed", staff, null, null, null);
    }

    // save() – sets createdAt when null
    @Test
    void save_setsCreatedAtWhenNull() {
        when(announcementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = announcementService.save(announcement);

        assertNotNull(result.getCreatedAt(), "createdAt should be auto-set");
        verify(announcementRepository).save(announcement);
    }

    // save() – does NOT overwrite existing createdAt
    @Test
    void save_doesNotOverwriteExistingCreatedAt() {
        LocalDateTime fixed = LocalDateTime.of(2024, 1, 1, 10, 0);
        announcement.setCreatedAt(fixed);
        when(announcementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = announcementService.save(announcement);

        assertEquals(fixed, result.getCreatedAt());
    }

    // save() – defaults audience to "All Students"
    @Test
    void save_defaultsAudienceToAllStudents() {
        when(announcementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = announcementService.save(announcement);

        assertEquals("All Students", result.getAudience());
    }

    // save() – keeps existing audience when provided
    @Test
    void save_keepsExistingAudience() {
        announcement.setAudience("3rd Semester");
        when(announcementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = announcementService.save(announcement);

        assertEquals("3rd Semester", result.getAudience());
    }

    // save() – defaults priority to "Normal"
    @Test
    void save_defaultsPriorityToNormal() {
        when(announcementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = announcementService.save(announcement);

        assertEquals("Normal", result.getPriority());
    }

    // save() – keeps explicit priority
    @Test
    void save_keepsExplicitPriority() {
        announcement.setPriority("Urgent");
        when(announcementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Announcement result = announcementService.save(announcement);

        assertEquals("Urgent", result.getPriority());
    }

    // findById() – found
    @Test
    void findById_returnsAnnouncementWhenPresent() {
        when(announcementRepository.findById(1L)).thenReturn(Optional.of(announcement));

        Optional<Announcement> result = announcementService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(announcement, result.get());
    }

    // findById() – not found
    @Test
    void findById_returnsEmptyWhenAbsent() {
        when(announcementRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(announcementService.findById(99L).isEmpty());
    }

    // deleteById()
    @Test
    void deleteById_callsRepositoryDelete() {
        announcementService.deleteById(1L);
        verify(announcementRepository).deleteById(1L);
    }

    // findAllNewestFirst()
    @Test
    void findAllNewestFirst_returnsList() {
        List<Announcement> list = List.of(announcement);
        when(announcementRepository.findAllByOrderByCreatedAtDesc()).thenReturn(list);

        assertEquals(list, announcementService.findAllNewestFirst());
    }

    // findForStudent() – builds correct audience list
    @Test
    void findForStudent_buildsCorrectAudienceList() {
        List<Announcement> list = List.of(announcement);
        when(announcementRepository.findByAudienceInOrderByCreatedAtDesc(
                List.of("All Students", "3rd Semester"))).thenReturn(list);

        List<Announcement> result = announcementService.findForStudent("3rd");

        assertEquals(list, result);
        verify(announcementRepository)
                .findByAudienceInOrderByCreatedAtDesc(List.of("All Students", "3rd Semester"));
    }

    // findByStaff()
    @Test
    void findByStaff_delegatesToRepository() {
        when(announcementRepository.findByCreatedBy(staff)).thenReturn(List.of(announcement));

        assertEquals(1, announcementService.findByStaff(staff).size());
    }

    // findByStaffId()
    @Test
    void findByStaffId_delegatesToRepository() {
        when(announcementRepository.findByCreatedById(1L)).thenReturn(List.of(announcement));

        assertEquals(1, announcementService.findByStaffId(1L).size());
    }

    // findByStaffNewestFirst()
    @Test
    void findByStaffNewestFirst_delegatesToRepository() {
        when(announcementRepository.findByCreatedByOrderByCreatedAtDesc(staff))
                .thenReturn(List.of(announcement));

        assertEquals(1, announcementService.findByStaffNewestFirst(staff).size());
    }

    // findByPriority()
    @Test
    void findByPriority_delegatesToRepository() {
        when(announcementRepository.findByPriorityOrderByCreatedAtDesc("Urgent"))
                .thenReturn(List.of(announcement));

        assertEquals(1, announcementService.findByPriority("Urgent").size());
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// 2.  AttendanceServiceImpTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class AttendanceServiceImpTest {

    @Mock AttendanceRepository attendanceRepository;
    @Mock StudentRepository    studentRepository;
    @Mock SubjectRepository    subjectRepository;

    @InjectMocks AttendanceServiceImp attendanceService;

    private Student student;
    private Subject subject;
    private Staff   staff;
    private Attendance attendance;
    private final LocalDate TODAY = LocalDate.of(2024, 5, 1);

    @BeforeEach
    void setUp() {
        student    = new Student(1L, "ADM001", "Bob", "bob@edu.com", "p", "2000-01-01", "5th", "CS", "ROLE_STUDENT");
        subject    = new Subject(1L, "Math", "5th");
        staff      = new Staff(1L, "Alice", "ST001", "alice@edu.com", "p", "CS", "ROLE_STAFF");
        attendance = new Attendance(1L, student, subject, staff, TODAY, "Present");
    }

    // markAttendance()
    @Test
    void markAttendance_savesAndReturns() {
        when(attendanceRepository.save(attendance)).thenReturn(attendance);

        Attendance result = attendanceService.markAttendance(attendance);

        assertEquals(attendance, result);
        verify(attendanceRepository).save(attendance);
    }

    // findExisting() – record found
    @Test
    void findExisting_returnsOptionalWhenFound() {
        when(attendanceRepository.findByStudentAndSubjectAndDate(student, subject, TODAY))
                .thenReturn(Optional.of(attendance));

        assertTrue(attendanceService.findExisting(student, subject, TODAY).isPresent());
    }

    // findExisting() – not found
    @Test
    void findExisting_returnsEmptyWhenNotFound() {
        when(attendanceRepository.findByStudentAndSubjectAndDate(student, subject, TODAY))
                .thenReturn(Optional.empty());

        assertTrue(attendanceService.findExisting(student, subject, TODAY).isEmpty());
    }

    // isAlreadyMarked() – true path
    @Test
    void isAlreadyMarked_returnsTrueWhenRecordExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(attendanceRepository.findByStudentAndSubjectAndDate(student, subject, TODAY))
                .thenReturn(Optional.of(attendance));

        assertTrue(attendanceService.isAlreadyMarked(1L, 1L, TODAY));
    }

    // isAlreadyMarked() – false path
    @Test
    void isAlreadyMarked_returnsFalseWhenNoRecord() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(attendanceRepository.findByStudentAndSubjectAndDate(student, subject, TODAY))
                .thenReturn(Optional.empty());

        assertFalse(attendanceService.isAlreadyMarked(1L, 1L, TODAY));
    }

    // isAlreadyMarked() – student not found
    @Test
    void isAlreadyMarked_throwsWhenStudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> attendanceService.isAlreadyMarked(99L, 1L, TODAY));
    }

    // isAlreadyMarked() – subject not found
    @Test
    void isAlreadyMarked_throwsWhenSubjectNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> attendanceService.isAlreadyMarked(1L, 99L, TODAY));
    }

    // findByStudent()
    @Test
    void findByStudent_delegatesToRepository() {
        when(attendanceRepository.findByStudent(student)).thenReturn(List.of(attendance));

        assertEquals(1, attendanceService.findByStudent(student).size());
    }

    // findByStudentId()
    @Test
    void findByStudentId_delegatesToRepository() {
        when(attendanceRepository.findByStudentId(1L)).thenReturn(List.of(attendance));

        assertEquals(1, attendanceService.findByStudentId(1L).size());
    }

    // findByStudentNewestFirst()
    @Test
    void findByStudentNewestFirst_delegatesToRepository() {
        when(attendanceRepository.findByStudentIdOrderByDateDesc(1L)).thenReturn(List.of(attendance));

        assertEquals(1, attendanceService.findByStudentNewestFirst(1L).size());
    }

    // findByStudentAndSubject()
    @Test
    void findByStudentAndSubject_delegatesToRepository() {
        when(attendanceRepository.findByStudentIdAndSubjectId(1L, 1L)).thenReturn(List.of(attendance));

        assertEquals(1, attendanceService.findByStudentAndSubject(1L, 1L).size());
    }

    // getAttendancePercentage() – normal case (2 present out of 4 classes = 50 %)
    @Test
    void getAttendancePercentage_calculatesCorrectly() {
        when(attendanceRepository.countBySubjectId(1L)).thenReturn(4L);
        when(attendanceRepository.countByStudentIdAndSubjectIdAndStatus(1L, 1L, "Present"))
                .thenReturn(2L);

        assertEquals(50.0, attendanceService.getAttendancePercentage(1L, 1L));
    }

    // getAttendancePercentage() – zero classes → 0.0
    @Test
    void getAttendancePercentage_returnsZeroWhenNoClasses() {
        when(attendanceRepository.countBySubjectId(1L)).thenReturn(0L);

        assertEquals(0.0, attendanceService.getAttendancePercentage(1L, 1L));
    }

    // findBySubjectAndDate()
    @Test
    void findBySubjectAndDate_delegatesToRepository() {
        when(attendanceRepository.findBySubjectIdAndDate(1L, TODAY)).thenReturn(List.of(attendance));

        assertEquals(1, attendanceService.findBySubjectAndDate(1L, TODAY).size());
    }

    // findByStaff()
    @Test
    void findByStaff_delegatesToRepository() {
        when(attendanceRepository.findByMarkedBy(staff)).thenReturn(List.of(attendance));

        assertEquals(1, attendanceService.findByStaff(staff).size());
    }

    // findByStaffId()
    @Test
    void findByStaffId_delegatesToRepository() {
        when(attendanceRepository.findByMarkedById(1L)).thenReturn(List.of(attendance));

        assertEquals(1, attendanceService.findByStaffId(1L).size());
    }

    // countPresent()
    @Test
    void countPresent_returnsCorrectCount() {
        when(attendanceRepository.countByStudentIdAndSubjectIdAndStatus(1L, 1L, "Present"))
                .thenReturn(3L);

        assertEquals(3L, attendanceService.countPresent(1L, 1L));
    }

    // countAbsent()
    @Test
    void countAbsent_returnsCorrectCount() {
        when(attendanceRepository.countByStudentIdAndSubjectIdAndStatus(1L, 1L, "Absent"))
                .thenReturn(1L);

        assertEquals(1L, attendanceService.countAbsent(1L, 1L));
    }

    // countTotalClassesBySubject()
    @Test
    void countTotalClassesBySubject_returnsCorrectCount() {
        when(attendanceRepository.countBySubjectId(1L)).thenReturn(10L);

        assertEquals(10L, attendanceService.countTotalClassesBySubject(1L));
    }

    // deleteById()
    @Test
    void deleteById_callsRepository() {
        attendanceService.deleteById(1L);
        verify(attendanceRepository).deleteById(1L);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// 3.  AuthServiceTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock StudentRepository studentRepository;
    @Mock StaffRepository   staffRepository;
    @Mock BCryptPasswordEncoder encoder;

    @InjectMocks AuthService authService;

    private Student student;
    private Staff   staff;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "ADM001", "Bob", "bob@edu.com", "hashed", "2000-01-01", "5th", "CS", "ROLE_STUDENT");
        staff   = new Staff(1L, "Alice", "ST001", "alice@edu.com", "hashed", "CS", "ROLE_STAFF");
    }

    // Student login – success
    @Test
    void authenticateUser_returnsStudentOnValidCredentials() {
        LoginRequest req = new LoginRequest();
        req.setEmail("bob@edu.com");
        req.setAdmissionNo("ADM001");
        req.setPassword("secret");

        when(studentRepository.findByEmailAndAdmissionNo("bob@edu.com", "ADM001"))
                .thenReturn(Optional.of(student));
        when(encoder.matches("secret", "hashed")).thenReturn(true);

        Object result = authService.authenticateUser(req);

        assertInstanceOf(Student.class, result);
        assertEquals(student, result);
    }

    // Student login – wrong password
    @Test
    void authenticateUser_returnsNullOnWrongStudentPassword() {
        LoginRequest req = new LoginRequest();
        req.setEmail("bob@edu.com");
        req.setAdmissionNo("ADM001");
        req.setPassword("wrong");

        when(studentRepository.findByEmailAndAdmissionNo("bob@edu.com", "ADM001"))
                .thenReturn(Optional.of(student));
        when(encoder.matches("wrong", "hashed")).thenReturn(false);

        assertNull(authService.authenticateUser(req));
    }

    // Student login – student not found
    @Test
    void authenticateUser_returnsNullWhenStudentNotFound() {
        LoginRequest req = new LoginRequest();
        req.setEmail("unknown@edu.com");
        req.setAdmissionNo("ADM999");
        req.setPassword("p");

        when(studentRepository.findByEmailAndAdmissionNo("unknown@edu.com", "ADM999"))
                .thenReturn(Optional.empty());

        assertNull(authService.authenticateUser(req));
    }

    // Staff login – success
    @Test
    void authenticateUser_returnsStaffOnValidCredentials() {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@edu.com");
        req.setStaffNo("ST001");
        req.setPassword("secret");

        when(staffRepository.findByEmailAndStaffNo("alice@edu.com", "ST001"))
                .thenReturn(Optional.of(staff));
        when(encoder.matches("secret", "hashed")).thenReturn(true);

        Object result = authService.authenticateUser(req);

        assertInstanceOf(Staff.class, result);
        assertEquals(staff, result);
    }

    // Staff login – wrong password
    @Test
    void authenticateUser_returnsNullOnWrongStaffPassword() {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@edu.com");
        req.setStaffNo("ST001");
        req.setPassword("bad");

        when(staffRepository.findByEmailAndStaffNo("alice@edu.com", "ST001"))
                .thenReturn(Optional.of(staff));
        when(encoder.matches("bad", "hashed")).thenReturn(false);

        assertNull(authService.authenticateUser(req));
    }

    // No credentials provided – returns null
    @Test
    void authenticateUser_returnsNullWhenNoCredentialsProvided() {
        LoginRequest req = new LoginRequest();
        req.setEmail("x@edu.com");
        req.setPassword("p");
        // admissionNo and staffNo are null

        assertNull(authService.authenticateUser(req));
    }

    // Blank admissionNo falls through to staff path
    @Test
    void authenticateUser_blankAdmissionNoFallsThroughToStaffPath() {
        LoginRequest req = new LoginRequest();
        req.setEmail("alice@edu.com");
        req.setAdmissionNo("  ");  // blank
        req.setStaffNo("ST001");
        req.setPassword("secret");

        when(staffRepository.findByEmailAndStaffNo("alice@edu.com", "ST001"))
                .thenReturn(Optional.of(staff));
        when(encoder.matches("secret", "hashed")).thenReturn(true);

        Object result = authService.authenticateUser(req);

        assertInstanceOf(Staff.class, result);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// 4.  LeaveApplicationServiceImpTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class LeaveApplicationServiceImpTest {

    @Mock LeaveRepository   leaveRepository;
    @Mock StudentRepository studentRepository;
    @Mock StaffRepository   staffRepository;

    @InjectMocks LeaveApplicationServiceImp leaveService;

    private Student         student;
    private Staff           staff;
    private LeaveApplication leave;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "ADM001", "Bob", "bob@edu.com", "p", "2000", "5th", "CS", "ROLE_STUDENT");
        staff   = new Staff(1L, "Alice", "ST001", "alice@edu.com", "p", "CS", "ROLE_STAFF");
        leave   = new LeaveApplication(1L, student, null, "ROLE_STUDENT",
                "Medical", LocalDate.now(), LocalDate.now().plusDays(2), "Sick", null);
    }

    // save() – sets default status "Pending" when null
    @Test
    void save_setsDefaultStatusPending() {
        when(leaveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveApplication result = leaveService.save(leave);

        assertEquals("Pending", result.getStatus());
    }

    // save() – does not overwrite existing status
    @Test
    void save_doesNotOverwriteExistingStatus() {
        leave.setStatus("Approved");
        when(leaveRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LeaveApplication result = leaveService.save(leave);

        assertEquals("Approved", result.getStatus());
    }

    // findById() – found
    @Test
    void findById_returnsLeaveWhenFound() {
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));

        assertTrue(leaveService.findById(1L).isPresent());
    }

    // findById() – not found
    @Test
    void findById_returnsEmptyWhenAbsent() {
        when(leaveRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(leaveService.findById(99L).isEmpty());
    }

    // deleteById()
    @Test
    void deleteById_callsRepository() {
        leaveService.deleteById(1L);
        verify(leaveRepository).deleteById(1L);
    }

    // updateStatus() – success
    @Test
    void updateStatus_updatesAndReturns() {
        when(leaveRepository.findById(1L)).thenReturn(Optional.of(leave));
        when(leaveRepository.save(leave)).thenReturn(leave);

        LeaveApplication result = leaveService.updateStatus(1L, "Approved");

        assertEquals("Approved", result.getStatus());
    }

    // updateStatus() – not found → throws
    @Test
    void updateStatus_throwsWhenNotFound() {
        when(leaveRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> leaveService.updateStatus(99L, "Approved"));
    }

    // findByStudent()
    @Test
    void findByStudent_delegatesToRepository() {
        when(leaveRepository.findByStudent(student)).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStudent(student).size());
    }

    // findByStudentId()
    @Test
    void findByStudentId_delegatesToRepository() {
        when(leaveRepository.findByStudentId(1L)).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStudentId(1L).size());
    }

    // findByStudentNewestFirst()
    @Test
    void findByStudentNewestFirst_delegatesToRepository() {
        when(leaveRepository.findByStudentIdOrderByFromDateDesc(1L)).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStudentNewestFirst(1L).size());
    }

    // findByStudentAndStatus() – student found
    @Test
    void findByStudentAndStatus_returnsFilteredList() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(leaveRepository.findByStudentAndStatus(student, "Pending")).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStudentAndStatus(1L, "Pending").size());
    }

    // findByStudentAndStatus() – student not found → throws
    @Test
    void findByStudentAndStatus_throwsWhenStudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> leaveService.findByStudentAndStatus(99L, "Pending"));
    }

    // findByStaff()
    @Test
    void findByStaff_delegatesToRepository() {
        when(leaveRepository.findByStaff(staff)).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStaff(staff).size());
    }

    // findByStaffId()
    @Test
    void findByStaffId_delegatesToRepository() {
        when(leaveRepository.findByStaffId(1L)).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStaffId(1L).size());
    }

    // findByStaffNewestFirst()
    @Test
    void findByStaffNewestFirst_delegatesToRepository() {
        when(leaveRepository.findByStaffIdOrderByFromDateDesc(1L)).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStaffNewestFirst(1L).size());
    }

    // findByStaffAndStatus() – staff found
    @Test
    void findByStaffAndStatus_returnsFilteredList() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
        when(leaveRepository.findByStaffAndStatus(staff, "Approved")).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStaffAndStatus(1L, "Approved").size());
    }

    // findByStaffAndStatus() – staff not found → throws
    @Test
    void findByStaffAndStatus_throwsWhenStaffNotFound() {
        when(staffRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> leaveService.findByStaffAndStatus(99L, "Approved"));
    }

    // findByStatus()
    @Test
    void findByStatus_delegatesToRepository() {
        when(leaveRepository.findByStatus("Pending")).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByStatus("Pending").size());
    }

    // findByRole()
    @Test
    void findByRole_delegatesToRepository() {
        when(leaveRepository.findByRole("ROLE_STUDENT")).thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByRole("ROLE_STUDENT").size());
    }

    // findByRoleAndStatus()
    @Test
    void findByRoleAndStatus_delegatesToRepository() {
        when(leaveRepository.findByRoleAndStatus("ROLE_STUDENT", "Pending"))
                .thenReturn(List.of(leave));

        assertEquals(1, leaveService.findByRoleAndStatus("ROLE_STUDENT", "Pending").size());
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// 5.  NotesServiceImpTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class NotesServiceImpTest {

    @Mock NotesRepository   notesRepository;
    @Mock SubjectRepository subjectRepository;

    @InjectMocks NotesServiceImp notesService;

    private Subject subject;
    private Staff   staff;
    private Notes   notes;

    @BeforeEach
    void setUp() {
        subject = new Subject(1L, "OS", "5th");
        staff   = new Staff(1L, "Alice", "ST001", "alice@edu.com", "p", "CS", "ROLE_STAFF");
        notes   = new Notes(1L, "Unit-1", subject, staff, "/uploads/u1.pdf", "Intro", "Notes");
    }

    // save()
    @Test
    void save_delegatesToRepository() {
        when(notesRepository.save(notes)).thenReturn(notes);

        assertEquals(notes, notesService.save(notes));
        verify(notesRepository).save(notes);
    }

    // findById() – found
    @Test
    void findById_returnsNotesWhenFound() {
        when(notesRepository.findById(1L)).thenReturn(Optional.of(notes));

        assertTrue(notesService.findById(1L).isPresent());
    }

    // findById() – not found
    @Test
    void findById_returnsEmptyWhenAbsent() {
        when(notesRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(notesService.findById(99L).isEmpty());
    }

    // deleteById()
    @Test
    void deleteById_callsRepository() {
        notesService.deleteById(1L);
        verify(notesRepository).deleteById(1L);
    }

    // findBySubject()
    @Test
    void findBySubject_delegatesToRepository() {
        when(notesRepository.findBySubject(subject)).thenReturn(List.of(notes));

        assertEquals(1, notesService.findBySubject(subject).size());
    }

    // findBySubjectId()
    @Test
    void findBySubjectId_delegatesToRepository() {
        when(notesRepository.findBySubjectId(1L)).thenReturn(List.of(notes));

        assertEquals(1, notesService.findBySubjectId(1L).size());
    }

    // findBySubjectIdNewestFirst()
    @Test
    void findBySubjectIdNewestFirst_delegatesToRepository() {
        when(notesRepository.findBySubjectIdOrderByIdDesc(1L)).thenReturn(List.of(notes));

        assertEquals(1, notesService.findBySubjectIdNewestFirst(1L).size());
    }

    // findByType()
    @Test
    void findByType_delegatesToRepository() {
        when(notesRepository.findByType("Assignment")).thenReturn(List.of(notes));

        assertEquals(1, notesService.findByType("Assignment").size());
    }

    // findBySubjectAndType() – subject found
    @Test
    void findBySubjectAndType_returnsFilteredNotes() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(notesRepository.findBySubjectAndType(subject, "Notes")).thenReturn(List.of(notes));

        assertEquals(1, notesService.findBySubjectAndType(1L, "Notes").size());
    }

    // findBySubjectAndType() – subject not found → throws
    @Test
    void findBySubjectAndType_throwsWhenSubjectNotFound() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> notesService.findBySubjectAndType(99L, "Notes"));
    }

    // findByStaff()
    @Test
    void findByStaff_delegatesToRepository() {
        when(notesRepository.findByUploadedBy(staff)).thenReturn(List.of(notes));

        assertEquals(1, notesService.findByStaff(staff).size());
    }

    // findByStaffId()
    @Test
    void findByStaffId_delegatesToRepository() {
        when(notesRepository.findByUploadedById(1L)).thenReturn(List.of(notes));

        assertEquals(1, notesService.findByStaffId(1L).size());
    }

    // findByStaffAndSubject()
    @Test
    void findByStaffAndSubject_delegatesToRepository() {
        when(notesRepository.findByUploadedByIdAndSubjectId(1L, 1L)).thenReturn(List.of(notes));

        assertEquals(1, notesService.findByStaffAndSubject(1L, 1L).size());
    }

    // findAllNewestFirst()
    @Test
    void findAllNewestFirst_delegatesToRepository() {
        when(notesRepository.findAllByOrderByIdDesc()).thenReturn(List.of(notes));

        assertEquals(1, notesService.findAllNewestFirst().size());
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// 6.  ResultServiceImpTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class ResultServiceImpTest {

    @Mock ResultRepository  resultRepository;
    @Mock StudentRepository studentRepository;
    @Mock SubjectRepository subjectRepository;

    @InjectMocks ResultServiceImp resultService;

    private Student student;
    private Subject subject;
    private Result  result;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "ADM001", "Bob", "bob@edu.com", "p", "2000", "5th", "CS", "ROLE_STUDENT");
        subject = new Subject(1L, "Math", "5th");
        result  = new Result(1L, student, subject, "5th", "Mid-Term", 80, "A");
    }

    // save()
    @Test
    void save_delegatesToRepository() {
        when(resultRepository.save(result)).thenReturn(result);

        assertEquals(result, resultService.save(result));
    }

    // findById() – found
    @Test
    void findById_returnsResultWhenFound() {
        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));

        assertTrue(resultService.findById(1L).isPresent());
    }

    // findById() – not found
    @Test
    void findById_returnsEmptyWhenAbsent() {
        when(resultRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(resultService.findById(99L).isEmpty());
    }

    // deleteById()
    @Test
    void deleteById_callsRepository() {
        resultService.deleteById(1L);
        verify(resultRepository).deleteById(1L);
    }

    // findExisting() – both entities found, record exists
    @Test
    void findExisting_returnsOptionalWhenFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(resultRepository.findByStudentAndSubjectAndSemesterAndExamType(
                student, subject, "5th", "Mid-Term")).thenReturn(Optional.of(result));

        assertTrue(resultService.findExisting(1L, 1L, "5th", "Mid-Term").isPresent());
    }

    // findExisting() – student not found → throws
    @Test
    void findExisting_throwsWhenStudentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> resultService.findExisting(99L, 1L, "5th", "Mid-Term"));
    }

    // findExisting() – subject not found → throws
    @Test
    void findExisting_throwsWhenSubjectNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> resultService.findExisting(1L, 99L, "5th", "Mid-Term"));
    }

    // existsResult() – true
    @Test
    void existsResult_returnsTrueWhenResultExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(resultRepository.findByStudentAndSubjectAndSemesterAndExamType(
                student, subject, "5th", "Mid-Term")).thenReturn(Optional.of(result));

        assertTrue(resultService.existsResult(1L, 1L, "5th", "Mid-Term"));
    }

    // existsResult() – false
    @Test
    void existsResult_returnsFalseWhenResultAbsent() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(resultRepository.findByStudentAndSubjectAndSemesterAndExamType(
                student, subject, "5th", "Mid-Term")).thenReturn(Optional.empty());

        assertFalse(resultService.existsResult(1L, 1L, "5th", "Mid-Term"));
    }

    // findByStudent()
    @Test
    void findByStudent_delegatesToRepository() {
        when(resultRepository.findByStudent(student)).thenReturn(List.of(result));

        assertEquals(1, resultService.findByStudent(student).size());
    }

    // findByStudentId()
    @Test
    void findByStudentId_delegatesToRepository() {
        when(resultRepository.findByStudentId(1L)).thenReturn(List.of(result));

        assertEquals(1, resultService.findByStudentId(1L).size());
    }

    // findByStudentAndSemester()
    @Test
    void findByStudentAndSemester_delegatesToRepository() {
        when(resultRepository.findByStudentIdAndSemester(1L, "5th")).thenReturn(List.of(result));

        assertEquals(1, resultService.findByStudentAndSemester(1L, "5th").size());
    }

    // findBySubjectSemesterExamType()
    @Test
    void findBySubjectSemesterExamType_delegatesToRepository() {
        when(resultRepository.findBySubjectIdAndSemesterAndExamType(1L, "5th", "Mid-Term"))
                .thenReturn(List.of(result));

        assertEquals(1, resultService.findBySubjectSemesterExamType(1L, "5th", "Mid-Term").size());
    }

    // findBySubject()
    @Test
    void findBySubject_delegatesToRepository() {
        when(resultRepository.findBySubject(subject)).thenReturn(List.of(result));

        assertEquals(1, resultService.findBySubject(subject).size());
    }

    // findBySubjectId()
    @Test
    void findBySubjectId_delegatesToRepository() {
        when(resultRepository.findBySubjectId(1L)).thenReturn(List.of(result));

        assertEquals(1, resultService.findBySubjectId(1L).size());
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// 7.  StaffServiceImpTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class StaffServiceImpTest {

    @Mock StaffRepository staffRepository;

    @InjectMocks StaffServiceImp staffService;

    private Staff staff;

    @BeforeEach
    void setUp() {
        staff = new Staff(1L, "Alice", "ST001", "alice@edu.com", "hashed", "CS", "ROLE_STAFF");
    }

    // save()
    @Test
    void save_delegatesToRepository() {
        when(staffRepository.save(staff)).thenReturn(staff);

        assertEquals(staff, staffService.save(staff));
    }

    // findById() – found
    @Test
    void findById_returnsStaffWhenFound() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));

        assertTrue(staffService.findById(1L).isPresent());
    }

    // findById() – not found
    @Test
    void findById_returnsEmptyWhenAbsent() {
        when(staffRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(staffService.findById(99L).isEmpty());
    }

    // findAll()
    @Test
    void findAll_returnsAllStaff() {
        when(staffRepository.findAll()).thenReturn(List.of(staff));

        assertEquals(1, staffService.findAll().size());
    }

    // deleteById()
    @Test
    void deleteById_callsRepository() {
        staffService.deleteById(1L);
        verify(staffRepository).deleteById(1L);
    }

    // findByDepartment()
    @Test
    void findByDepartment_delegatesToRepository() {
        when(staffRepository.findByDepartment("CS")).thenReturn(List.of(staff));

        assertEquals(1, staffService.findByDepartment("CS").size());
    }

    // findByEmail() – found
    @Test
    void findByEmail_returnsStaffWhenFound() {
        when(staffRepository.findByEmail("alice@edu.com")).thenReturn(Optional.of(staff));

        assertTrue(staffService.findByEmail("alice@edu.com").isPresent());
    }

    // findByEmail() – not found
    @Test
    void findByEmail_returnsEmptyWhenNotFound() {
        when(staffRepository.findByEmail("x@edu.com")).thenReturn(Optional.empty());

        assertTrue(staffService.findByEmail("x@edu.com").isEmpty());
    }

    // findByStaffNo() – found
    @Test
    void findByStaffNo_returnsStaffWhenFound() {
        when(staffRepository.findByStaffNo("ST001")).thenReturn(Optional.of(staff));

        assertTrue(staffService.findByStaffNo("ST001").isPresent());
    }

    // existsByEmail() – true
    @Test
    void existsByEmail_returnsTrueWhenExists() {
        when(staffRepository.existsByEmail("alice@edu.com")).thenReturn(true);

        assertTrue(staffService.existsByEmail("alice@edu.com"));
    }

    // existsByEmail() – false
    @Test
    void existsByEmail_returnsFalseWhenAbsent() {
        when(staffRepository.existsByEmail("x@edu.com")).thenReturn(false);

        assertFalse(staffService.existsByEmail("x@edu.com"));
    }

    // existsByStaffNo() – true
    @Test
    void existsByStaffNo_returnsTrueWhenExists() {
        when(staffRepository.existsByStaffNo("ST001")).thenReturn(true);

        assertTrue(staffService.existsByStaffNo("ST001"));
    }

    // existsByStaffNo() – false
    @Test
    void existsByStaffNo_returnsFalseWhenAbsent() {
        when(staffRepository.existsByStaffNo("XX")).thenReturn(false);

        assertFalse(staffService.existsByStaffNo("XX"));
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// 8.  StudentServiceImpTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class StudentServiceImpTest {

    @Mock StudentRepository studentRepository;

    @InjectMocks StudentServiceImp studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "ADM001", "Bob", "bob@edu.com", "hashed",
                "2000-01-01", "5th", "CS", "ROLE_STUDENT");
    }

    // save()
    @Test
    void save_delegatesToRepository() {
        when(studentRepository.save(student)).thenReturn(student);

        assertEquals(student, studentService.save(student));
    }

    // findById() – found
    @Test
    void findById_returnsStudentWhenFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertTrue(studentService.findById(1L).isPresent());
    }

    // findById() – not found
    @Test
    void findById_returnsEmptyWhenAbsent() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(studentService.findById(99L).isEmpty());
    }

    // findAll()
    @Test
    void findAll_returnsAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        assertEquals(1, studentService.findAll().size());
    }

    // deleteById()
    @Test
    void deleteById_callsRepository() {
        studentService.deleteById(1L);
        verify(studentRepository).deleteById(1L);
    }

    // findBySemester()
    @Test
    void findBySemester_delegatesToRepository() {
        when(studentRepository.findBySemester("5th")).thenReturn(List.of(student));

        assertEquals(1, studentService.findBySemester("5th").size());
    }

    // findByBranch()
    @Test
    void findByBranch_delegatesToRepository() {
        when(studentRepository.findByBranch("CS")).thenReturn(List.of(student));

        assertEquals(1, studentService.findByBranch("CS").size());
    }

    // findBySemesterAndBranch()
    @Test
    void findBySemesterAndBranch_delegatesToRepository() {
        when(studentRepository.findBySemesterAndBranch("5th", "CS")).thenReturn(List.of(student));

        assertEquals(1, studentService.findBySemesterAndBranch("5th", "CS").size());
    }

    // findByEmail() – found
    @Test
    void findByEmail_returnsStudentWhenFound() {
        when(studentRepository.findByEmail("bob@edu.com")).thenReturn(Optional.of(student));

        assertTrue(studentService.findByEmail("bob@edu.com").isPresent());
    }

    // findByEmail() – not found
    @Test
    void findByEmail_returnsEmptyWhenAbsent() {
        when(studentRepository.findByEmail("x@edu.com")).thenReturn(Optional.empty());

        assertTrue(studentService.findByEmail("x@edu.com").isEmpty());
    }

    // findByAdmissionNo() – found
    @Test
    void findByAdmissionNo_returnsStudentWhenFound() {
        when(studentRepository.findByAdmissionNo("ADM001")).thenReturn(Optional.of(student));

        assertTrue(studentService.findByAdmissionNo("ADM001").isPresent());
    }

    // existsByEmail() – true / false
    @Test
    void existsByEmail_returnsTrueWhenExists() {
        when(studentRepository.existsByEmail("bob@edu.com")).thenReturn(true);
        assertTrue(studentService.existsByEmail("bob@edu.com"));
    }

    @Test
    void existsByEmail_returnsFalseWhenAbsent() {
        when(studentRepository.existsByEmail("x@edu.com")).thenReturn(false);
        assertFalse(studentService.existsByEmail("x@edu.com"));
    }

    // existsByAdmissionNo() – true / false
    @Test
    void existsByAdmissionNo_returnsTrueWhenExists() {
        when(studentRepository.existsByAdmissionNo("ADM001")).thenReturn(true);
        assertTrue(studentService.existsByAdmissionNo("ADM001"));
    }

    @Test
    void existsByAdmissionNo_returnsFalseWhenAbsent() {
        when(studentRepository.existsByAdmissionNo("XYZXYZ")).thenReturn(false);
        assertFalse(studentService.existsByAdmissionNo("XYZXYZ"));
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// 9.  SubjectServiceImpTest
// ─────────────────────────────────────────────────────────────────────────────
@ExtendWith(MockitoExtension.class)
class SubjectServiceImpTest {

    @Mock SubjectRepository subjectRepository;

    @InjectMocks SubjectServiceImp subjectService;

    private Subject subject;

    @BeforeEach
    void setUp() {
        subject = new Subject(1L, "Data Structures", "3rd");
    }

    // save()
    @Test
    void save_delegatesToRepository() {
        when(subjectRepository.save(subject)).thenReturn(subject);

        assertEquals(subject, subjectService.save(subject));
    }

    // findById() – found
    @Test
    void findById_returnsSubjectWhenFound() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        assertTrue(subjectService.findById(1L).isPresent());
    }

    // findById() – not found
    @Test
    void findById_returnsEmptyWhenAbsent() {
        when(subjectRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(subjectService.findById(99L).isEmpty());
    }

    // findAll()
    @Test
    void findAll_returnsAllSubjects() {
        when(subjectRepository.findAll()).thenReturn(List.of(subject));

        assertEquals(1, subjectService.findAll().size());
    }

    // deleteById()
    @Test
    void deleteById_callsRepository() {
        subjectService.deleteById(1L);
        verify(subjectRepository).deleteById(1L);
    }

    // findBySemester()
    @Test
    void findBySemester_delegatesToRepository() {
        when(subjectRepository.findBySemester("3rd")).thenReturn(List.of(subject));

        assertEquals(1, subjectService.findBySemester("3rd").size());
    }

    // findBySubjectName() – found
    @Test
    void findBySubjectName_returnsSubjectWhenFound() {
        when(subjectRepository.findBySubjectName("Data Structures"))
                .thenReturn(Optional.of(subject));

        assertTrue(subjectService.findBySubjectName("Data Structures").isPresent());
    }

    // findBySubjectName() – not found
    @Test
    void findBySubjectName_returnsEmptyWhenAbsent() {
        when(subjectRepository.findBySubjectName("Unknown")).thenReturn(Optional.empty());

        assertTrue(subjectService.findBySubjectName("Unknown").isEmpty());
    }

    // findBySubjectNameAndSemester() – found
    @Test
    void findBySubjectNameAndSemester_returnsSubjectWhenFound() {
        when(subjectRepository.findBySubjectNameAndSemester("Data Structures", "3rd"))
                .thenReturn(Optional.of(subject));

        assertTrue(subjectService.findBySubjectNameAndSemester("Data Structures", "3rd").isPresent());
    }

    // findBySubjectNameAndSemester() – not found
    @Test
    void findBySubjectNameAndSemester_returnsEmptyWhenAbsent() {
        when(subjectRepository.findBySubjectNameAndSemester("X", "Y"))
                .thenReturn(Optional.empty());

        assertTrue(subjectService.findBySubjectNameAndSemester("X", "Y").isEmpty());
    }

    // existsBySubjectNameAndSemester() – true
    @Test
    void existsBySubjectNameAndSemester_returnsTrueWhenExists() {
        when(subjectRepository.existsBySubjectNameAndSemester("Data Structures", "3rd"))
                .thenReturn(true);

        assertTrue(subjectService.existsBySubjectNameAndSemester("Data Structures", "3rd"));
    }

    // existsBySubjectNameAndSemester() – false
    @Test
    void existsBySubjectNameAndSemester_returnsFalseWhenAbsent() {
        when(subjectRepository.existsBySubjectNameAndSemester("X", "Y")).thenReturn(false);

        assertFalse(subjectService.existsBySubjectNameAndSemester("X", "Y"));
    }
}