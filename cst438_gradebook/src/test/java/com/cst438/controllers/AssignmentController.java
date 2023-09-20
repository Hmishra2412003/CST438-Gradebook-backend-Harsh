package com.cst438.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @PostMapping("/assignment")
    public AssignmentDTO addAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        Assignment newAssignment = new Assignment();
        newAssignment.setName(assignmentDTO.assignmentName()); // Use assignmentName from AssignmentDTO
        // Set other assignment properties like due date, course, etc.

        // Save the new assignment to the database
        Assignment savedAssignment = assignmentRepository.save(newAssignment);

        // Convert the saved assignment to DTO and return it
        return new AssignmentDTO(
                savedAssignment.getId(),
                savedAssignment.getName(),
                savedAssignment.getDueDate().toString(),
                savedAssignment.getCourse().getTitle(),
                savedAssignment.getCourse().getCourse_id()
        );
    }

    @GetMapping("/assignment/{id}")
    public AssignmentDTO getAssignmentById(@PathVariable int id) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (assignmentOptional.isPresent()) {
            Assignment assignment = assignmentOptional.get();
            return new AssignmentDTO(
                    assignment.getId(),
                    assignment.getName(),
                    assignment.getDueDate().toString(),
                    assignment.getCourse().getTitle(),
                    assignment.getCourse().getCourse_id()
            );
        } else {
            // Handle assignment not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }
    }

    @PutMapping("/assignment/{id}")
    public AssignmentDTO updateAssignment(@PathVariable int id, @RequestBody AssignmentDTO updatedAssignmentDTO) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (assignmentOptional.isPresent()) {
            Assignment assignment = assignmentOptional.get();
            assignment.setName(updatedAssignmentDTO.assignmentName()); // Use assignmentName from AssignmentDTO
            // Update other assignment properties as needed

            // Save the updated assignment to the database
            Assignment updatedAssignment = assignmentRepository.save(assignment);

            // Convert the updated assignment to DTO and return it
            return new AssignmentDTO(
                    updatedAssignment.getId(),
                    updatedAssignment.getName(),
                    updatedAssignment.getDueDate().toString(),
                    updatedAssignment.getCourse().getTitle(),
                    updatedAssignment.getCourse().getCourse_id()
            );
        } else {
            // Handle assignment not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }
    }

    @DeleteMapping("/assignment/{id}")
    public void deleteAssignment(@PathVariable int id) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (assignmentOptional.isPresent()) {
            Assignment assignment = assignmentOptional.get();
            assignmentRepository.delete(assignment);
        } else {
            // Handle assignment not found
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }
    }
}

