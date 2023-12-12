package com.cst438.controllers;

import java.security.Principal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin
public class AssignmentController {

	@Autowired
	AssignmentRepository assignmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@GetMapping("/assignment") //get by professor
	public AssignmentDTO[] getAllAssignmentsForInstructor(Principal principal) {
		// get all assignments for this instructor
		System.out.print(principal.getName());
		String instructorEmail = principal.getName();

		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i = 0; i < assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(),
					as.getName(),
					as.getDueDate().toString(),
					as.getCourse().getTitle(),
					as.getCourse().getCourse_id());
			result[i] = dto;
		}
		return result;
	}

	@PostMapping("/assignment")
	public AssignmentDTO addAssignment(@RequestBody AssignmentDTO assignmentDTO, Principal principal) {
		String instructorEmail = principal.getName();

		// Ensure that the user is the instructor for the course
		if (!isInstructorForCourse(instructorEmail, assignmentDTO.courseId())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to add assignment for this course");
		}

		// Create a new Assignment object
		Assignment newAssignment = new Assignment();

		// Set the name property of the Assignment object using assignmentDTO
		newAssignment.setName(assignmentDTO.assignmentName());
		newAssignment.setCourse(courseRepository.findById(assignmentDTO.courseId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found")));
		newAssignment.setDueDate(Date.valueOf(assignmentDTO.dueDate()));
		// Set other assignment properties like due date, course, etc.

		// Save the new assignment to the database
		Assignment savedAssignment = assignmentRepository.save(newAssignment);

		// Convert the saved assignment to DTO and return it
		AssignmentDTO savedDTO = new AssignmentDTO(
				savedAssignment.getId(),
				savedAssignment.getName(),
				savedAssignment.getDueDate().toString(),
				savedAssignment.getCourse().getTitle(),
				savedAssignment.getCourse().getCourse_id()
		);
		return savedDTO;
	}

	@GetMapping("/assignment/{id}")
	public AssignmentDTO getListAssignment(@PathVariable("id") int id, Principal principal) {
		String instructorEmail = principal.getName();
		// Find the assignment by its ID
		Assignment assignment = assignmentRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

		// Check if the user is the instructor for the course
		if (!assignment.getCourse().getInstructor().equals(instructorEmail)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to view this assignment");
		}

		// Convert the assignment to DTO and return it
		AssignmentDTO assignmentDTO = new AssignmentDTO(
				assignment.getId(),
				assignment.getName(),
				assignment.getDueDate().toString(),
				assignment.getCourse().getTitle(),
				assignment.getCourse().getCourse_id()
		);
		return assignmentDTO;
	}

	@PutMapping("/assignment/{id}")
	public AssignmentDTO updateAssignment(@PathVariable int id, @RequestBody AssignmentDTO updatedAssignmentDTO,
										  Principal principal) {
		String instructorEmail = principal.getName();
		// Find the assignment by its ID
		Assignment assignment = assignmentRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

		// Check if the user is the instructor for the course
		if (!assignment.getCourse().getInstructor().equals(instructorEmail)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this assignment");
		}

		// Update the assignment properties from the DTO
		assignment.setName(updatedAssignmentDTO.assignmentName());
		// Update other assignment properties as needed
		assignment.setDueDate(Date.valueOf(updatedAssignmentDTO.dueDate()));
		// Save the updated assignment to the database
		Assignment updatedAssignment = assignmentRepository.save(assignment);

		// Convert the updated assignment to DTO and return it
		AssignmentDTO updatedDTO = new AssignmentDTO(
				updatedAssignment.getId(),
				updatedAssignment.getName(),
				updatedAssignment.getDueDate().toString(),
				updatedAssignment.getCourse().getTitle(),
				updatedAssignment.getCourse().getCourse_id()
		);
		return updatedDTO;
	}

	@DeleteMapping("/assignment/{id}")
	public void deleteAssignment(@PathVariable("id") int id, @RequestParam(value = "force") Optional<String> force,
								 Principal principal) {
		String instructorEmail = principal.getName();
		// Check if the assignment exists
		Assignment assignment = assignmentRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

		// Check if the user is the instructor for the course
		if (!assignment.getCourse().getInstructor().equals(instructorEmail)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this assignment");
		}

		// Delete the assignment
		assignmentRepository.delete(assignment);
	}

	private boolean isInstructorForCourse(String instructorEmail, int courseId) {
		// Implement logic to check if the given user is the instructor for the specified course
		// You can use courseRepository to fetch the course and check the instructor's email
		return courseRepository.findById(courseId)
				.map(course -> course.getInstructor().equals(instructorEmail))
				.orElse(false);
	}
}
