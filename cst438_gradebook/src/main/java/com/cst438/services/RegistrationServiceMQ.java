package com.cst438.services;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.Course;
import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnProperty(prefix = "registration", name = "service", havingValue = "mq")
public class RegistrationServiceMQ implements RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}


	Queue registrationQueue = new Queue("registration-queue", true);

	/*
	 * Receive message for student added to course
	 */
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(String message) {
		System.out.println("Gradebook has received: " + message);

		// Deserialize the received JSON message to EnrollmentDTO
		EnrollmentDTO enrollmentDTO = fromJsonString(message, EnrollmentDTO.class);

		Course course = courseRepository.findById(enrollmentDTO.courseId()).orElse(null);
		Enrollment enroll = new Enrollment();
		enroll.setStudentEmail(enrollmentDTO.studentEmail());
		enroll.setStudentName(enrollmentDTO.studentName());
		enroll.setCourse(course);

		enrollmentRepository.save(enroll);
	}


	/*
	 * Send final grades to Registration Service 
	 */
	@Override
	public void sendFinalGrades(int course_id, FinalGradeDTO[] grades) {
		System.out.println("Start sendFinalGrades " + course_id);

		try {
			// Convert the grades array to a JSON string
			String gradesJson = asJsonString(grades);

			// Create an EnrollmentDTO object with the course_id and the grades JSON
			EnrollmentDTO enrollmentDTO = new EnrollmentDTO(0, null, null, course_id);

			// Serialize the EnrollmentDTO to a JSON string
			String enrollmentJson = asJsonString(enrollmentDTO);

			// Send the enrollmentJson message to the registration-queue in RabbitMQ
			rabbitTemplate.convertAndSend(registrationQueue.getName(), enrollmentJson);

			System.out.println("Final grades sent to Registration Service: " + enrollmentJson);
		} catch (Exception e) {
			// Handle exceptions appropriately
			e.printStackTrace();
		}
	}


	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
