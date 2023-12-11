package com.cst438.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Enrollment;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "rest")
@RestController
public class RegistrationServiceREST implements RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}")
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}

	@Override
	public void sendFinalGrades(int course_id, FinalGradeDTO[] grades) {
		// Build the URL for the registration service based on the course_id
		String url = registration_url +"/course/" + course_id;

		// Use RestTemplate to send final grades to the registration service
		restTemplate.put(url, grades);
	}


	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	
	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
		// Receive an EnrollmentDTO from the registration service to enroll a student into a course.

		// Convert the EnrollmentDTO to an Enrollment entity
		Course course = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);
		Enrollment enrollment = new Enrollment();
		enrollment.setStudentEmail(enrollmentDTO.studentEmail());
		enrollment.setStudentName(enrollmentDTO.studentName());

		// Save the enrollment in the database using the EnrollmentRepository
		enrollmentRepository.save(enrollment);

		// Return the received EnrollmentDTO as a response to the registration service
		return enrollmentDTO;
	}



}
