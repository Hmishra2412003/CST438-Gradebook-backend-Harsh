package com.cst438;
import com.cst438.controllers.AssignmentController;
import com.cst438.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TestAssignments {

    @InjectMocks
    private AssignmentController assignmentController;

    @Mock
    private AssignmentRepository assignmentRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(assignmentController).build();
    }

    @Test
    public void testAddAssignment() throws Exception {
        // Create a sample AssignmentDTO with a valid dueDate
        AssignmentDTO assignmentDTO = new AssignmentDTO(1, "Sample Assignment", "2023-09-20", "Sample Course", 123);

        // Check if assignmentDTO.dueDate returns a non-null value
        if (assignmentDTO.dueDate() == null) {
            // Handle the case where dueDate is null (e.g., log a warning or take appropriate action)
            System.out.println("Warning: DueDate in AssignmentDTO is null.");
            // You may choose to return or throw an exception in this case
            return; // or throw an exception
        }

        // Create a sample Assignment with a non-null dueDate
        Assignment sampleAssignment = new Assignment();
        sampleAssignment.setId(1);
        sampleAssignment.setName(assignmentDTO.assignmentName());

        // Check if the dueDate is not null before setting it
        if (assignmentDTO.dueDate() != null) {
            sampleAssignment.setDueDate(Date.valueOf(assignmentDTO.dueDate())); // Set the dueDate from DTO
        }

        // Set other properties of the assignment as needed

        // Mock the behavior of assignmentRepository.save()
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(new Assignment());

        // Perform a POST request to add an assignment
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/assignment")
                        .content("{}") // You can provide the JSON content here
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void testGetAssignmentById() throws Exception {
        // Create a sample Course
        Course sampleCourse = new Course();
        sampleCourse.setTitle("Sample Course"); // Set a valid title for the course
        // Set other properties of the course as needed

        // Create a sample Assignment with a non-null Course
        Assignment sampleAssignment = new Assignment();
        sampleAssignment.setId(1);
        sampleAssignment.setName("Sample Assignment");
        sampleAssignment.setDueDate(Date.valueOf("2023-09-20"));
        sampleAssignment.setCourse(sampleCourse); // Set the course for the assignment

        // Mock the behavior of assignmentRepository.findById()
        when(assignmentRepository.findById(anyInt())).thenReturn(Optional.of(sampleAssignment));

        // Perform a GET request to retrieve an assignment by ID
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/assignment/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }



    @Test
    public void testUpdateAssignment() throws Exception {
        // Create a sample AssignmentDTO for the update
        AssignmentDTO updatedAssignmentDTO = new AssignmentDTO(1, "Updated Assignment", "2023-09-21", "Updated Course", 456);

        // Mock the behavior of assignmentRepository.findById() and assignmentRepository.save()
        when(assignmentRepository.findById(anyInt())).thenReturn(Optional.of(new Assignment()));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(new Assignment());

        // Perform a PUT request to update an assignment
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/assignment/1")
                        .content("{}") // You can provide the JSON content here
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteAssignment() throws Exception {
        // Create a sample Assignment
        Assignment sampleAssignment = new Assignment();
        sampleAssignment.setId(1);
        sampleAssignment.setName("Sample Assignment");
        sampleAssignment.setDueDate(Date.valueOf("2023-09-20"));
        sampleAssignment.setCourse(new Course());

        // Simulate having associated grades (you can adjust this based on your data model)
        List<AssignmentGrade> associatedGrades = new ArrayList<>();
        associatedGrades.add(new AssignmentGrade());
        sampleAssignment.setAssignmentGrades(associatedGrades);

        // Mock the behavior of assignmentRepository.findById()
        when(assignmentRepository.findById(anyInt())).thenReturn(Optional.of(sampleAssignment));

        // Mock the behavior of assignmentRepository.delete()
        Mockito.doNothing().when(assignmentRepository).delete(sampleAssignment);

        // Perform a DELETE request to delete an assignment
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/assignment/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Check if the assignment has associated grades
        if (!sampleAssignment.getAssignmentGrades().isEmpty()) {
            // Issue a warning or take appropriate action
            System.out.println("Warning: This assignment has associated grades.");
        }
    }

}
