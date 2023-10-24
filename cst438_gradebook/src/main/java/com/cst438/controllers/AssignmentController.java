package com.cst438.controllers;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
@CrossOrigin
public class AssignmentController {

	@Autowired
	AssignmentRepository assignmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@GetMapping("/assignment")
	public AssignmentDTO[] getAllAssignmentsForInstructor() {
		// get all assignments for this instructor
		String instructorEmail = "dwisneski@csumb.edu";  // user name (should be instructor's email) 
		List<Assignment> assignments = assignmentRepository.findByEmail(instructorEmail);
		AssignmentDTO[] result = new AssignmentDTO[assignments.size()];
		for (int i=0; i<assignments.size(); i++) {
			Assignment as = assignments.get(i);
			AssignmentDTO dto = new AssignmentDTO(
					as.getId(),
					as.getName(),
					as.getDueDate().toString(),
					as.getCourse().getTitle(),
					as.getCourse().getCourse_id());
			result[i]=dto;
		}
		return result;
	}
	@PostMapping("/assignment")
	public AssignmentDTO addAssignment(@RequestBody AssignmentDTO assignmentDTO) {
		// Create a new Assignment object
		Assignment newAssignment = new Assignment();

		// Set the name property of the Assignment object using assignmentDTO
		newAssignment.setName(assignmentDTO.assignmentName());
		newAssignment.setCourse(courseRepository.findById(assignmentDTO.courseId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Assignment not found")));
		newAssignment.setDueDate(Date.valueOf(assignmentDTO.dueDate()));
		// Set other assignment properties like due date, course, etc.

		// Save the new assignment to the database
		Assignment savedAssignment = assignmentRepository.save(newAssignment);

		// Convert the saved assignment to DTO and return it
		AssignmentDTO savedDTO = new AssignmentDTO(
				savedAssignment.getId(),
				savedAssignment.getName(), // You can use savedAssignment.getName() here
				savedAssignment.getDueDate().toString(),
				savedAssignment.getCourse().getTitle(),
				savedAssignment.getCourse().getCourse_id()
		);
		return savedDTO;
	}


	@GetMapping("/assignment/{id}")
	public AssignmentDTO getAssignmentById(@PathVariable int id) {
		// Find the assignment by its ID
		Assignment assignment = assignmentRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

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
	public AssignmentDTO updateAssignment(@PathVariable int id, @RequestBody AssignmentDTO updatedAssignmentDTO) {
		// Find the assignment by its ID
		Assignment assignment = assignmentRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

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
	public void deleteAssignment(@PathVariable int id) {
		// Check if the assignment exists
		Assignment assignment = assignmentRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found"));

		// Delete the assignment
		assignmentRepository.delete(assignment);
	}

}
